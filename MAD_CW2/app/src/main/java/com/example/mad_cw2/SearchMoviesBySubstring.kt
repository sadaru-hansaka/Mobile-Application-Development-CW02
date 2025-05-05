package com.example.mad_cw2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mad_cw2.ui.theme.MAD_CW2Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class SearchMoviesBySubstring : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            searchMovies()
        }
    }
}

@Composable
fun searchMovies(){
    var movieName by rememberSaveable { mutableStateOf("") }
    var results by rememberSaveable { mutableStateOf(listOf<String>()) }
    var errorMessage by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp,60.dp,16.dp,16.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = "Search for Movies",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        TextField(
            value = movieName,
            onValueChange = {movieName = it},
            label = { Text("Enter Movie Name") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
        )
        Button(onClick = {
            errorMessage = ""
            CoroutineScope(Dispatchers.Main).launch {
                val (movieList, error) = fetchMoviesBySubstring(movieName)
                if (error.isNotEmpty()) {
                    errorMessage = error
                } else {
                    results = movieList
                }
            }
        },modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp))  {
            Text(text = "Search")
        }

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        } else {
            LazyColumn {
                items(results) { movie ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(4.dp),
                    ){
                        Column(modifier = Modifier.padding(16.dp)){
                            Text(text = movie)
                        }
                    }

                }
            }
        }
    }
}

suspend fun fetchMoviesBySubstring(movieName: String): Pair<List<String>,String> {
    val urlString = "https://www.omdbapi.com/?s=${movieName}&apikey="

    return try {
        val url = URL(urlString)
        val con = url.openConnection() as HttpURLConnection
        val response = StringBuilder()

        withContext(Dispatchers.IO) {
            val bf = BufferedReader(InputStreamReader(con.inputStream))
            var line: String? = bf.readLine()
            while (line != null) {
                response.append(line)
                line = bf.readLine()
            }
            bf.close()
        }

        val json = JSONObject(response.toString())
        if(json.getString("Response") == "False"){
            return Pair(emptyList(), "No result found !")
        }

        val searchResult = json.getJSONArray("Search")
        val movieList = mutableListOf<String>()
        for (i in 0 until searchResult.length()) {
            val item = searchResult.getJSONObject(i)
            val title = item.optString("Title", "N/A")
            val year = item.optString("Year", "N/A")
            movieList.add("$title ($year)")
        }

        Pair(movieList,"")
    } catch (e: Exception) {
        e.printStackTrace()
        Pair(emptyList(),"Error fetching movie: ${e.message}")
    }
}







