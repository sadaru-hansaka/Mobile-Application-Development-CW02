package com.example.mad_cw2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.mad_cw2.ui.theme.MAD_CW2Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class SearchMovies : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Room.databaseBuilder(this, AppDatabase::class.java, "movie-database").build()
        movieDao = database.movieDao()

        enableEdgeToEdge()
        setContent {
            searchMovie()
        }
    }
}

@Composable
fun searchMovie(){
    var movieInfo by remember { mutableStateOf("") }
    var keyword by remember { mutableStateOf("") }
    var lastJson by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(10.dp,60.dp,10.dp,10.dp),
//        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        TextField(
            value = keyword,
            onValueChange = {keyword = it},
            label = { Text("Enter Movie Name") },
//            modifier = Modifier.padding(16.dp)
        )
        Row{
            Button(onClick = {
                scope.launch {
                    val (info, json) = fetchMovies(keyword)
                    movieInfo = info
                    lastJson = json
                }
            }) {
                Text(text = "Retrieve Movie")
            }
            Spacer(Modifier.width(15.dp))
            Button(onClick = {
                scope.launch {
                    try {
                        val movie = parseJsonToEntity(lastJson)
                        movieDao.insertMovie(movie)
                        movieInfo = "Movie saved"
                    }catch (e:Exception){
                        movieInfo = "No movie found"
                    }
                }
            }) {
                Text(text = "Save Movie to DB")
            }
        }
        Text(text = movieInfo)
    }
}

suspend fun fetchMovies(keyword: String): Pair<String, String> {
    val urlString = "https://www.omdbapi.com/?t=${keyword}&apikey="

    return try {
        val url = URL(urlString)
        val con = url.openConnection() as HttpURLConnection
        val stb = StringBuilder()

        withContext(Dispatchers.IO) {
            val bf = BufferedReader(InputStreamReader(con.inputStream))
            var line: String? = bf.readLine()
            while (line != null) {
                stb.append(line + "\n")
                line = bf.readLine()
            }
            bf.close()
        }

        val info = parseJSON(StringBuilder(stb.toString()))
        Pair(info, stb.toString())
    } catch (e: Exception) {
        e.printStackTrace()
        Pair("Error fetching movie: ${e.message}", "")
    }
}

fun parseJSON(stb: StringBuilder): String {
    val json = JSONObject(stb.toString())
    val allMovies = StringBuilder()

    if (json.has("Response") && json.getString("Response") == "False") {
        return "No movie found for the given keyword."
    }

    val title = json.optString("Title", "N/A")
    val year = json.optString("Year", "N/A")
    val rated = json.optString("Rated", "N/A")
    val released = json.optString("Released", "N/A")
    val runtime = json.optString("Runtime", "N/A")
    val genre = json.optString("Genre", "N/A")
    val director = json.optString("Director", "N/A")
    val writer = json.optString("Writer", "N/A")
    val actors = json.optString("Actors", "N/A")
    val plot = json.optString("Plot", "N/A")

    allMovies.append("Title: $title\n")
    allMovies.append("Year: $year\n")
    allMovies.append("Rated: $rated\n")
    allMovies.append("Released: $released\n")
    allMovies.append("Runtime: $runtime\n")
    allMovies.append("Genre: $genre\n")
    allMovies.append("Director: $director\n")
    allMovies.append("Writer: $writer\n")
    allMovies.append("Actors: $actors\n")
    allMovies.append("Plot: $plot\n")

    return allMovies.toString()
}



fun parseJsonToEntity(jsonString: String): Movie {
    val json = JSONObject(jsonString)

    return Movie(
        title = json.optString("Title", "N/A"),
        year = json.optString("Year", "N/A"),
        rated = json.optString("Rated", "N/A"),
        release = json.optString("Released", "N/A"),
        runtime = json.optString("Runtime", "N/A"),
        genre = json.optString("Genre", "N/A"),
        director = json.optString("Director", "N/A"),
        writer = json.optString("Writer", "N/A"),
        actor = json.optString("Actors", "N/A"),
        plot = json.optString("Plot", "N/A")
    )
}
