package com.example.mad_cw2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.launch

class SearchActors : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Room.databaseBuilder(this, AppDatabase::class.java, "movie-database").build()
        movieDao = database.movieDao()

        enableEdgeToEdge()
        setContent {
            searchActor()
        }
    }
}

@Composable
fun searchActor(){
    var movieData by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var actorName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp,60.dp,16.dp,16.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = "Search for Actors",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        TextField(
            value = actorName,
            onValueChange = {actorName = it},
            label = { Text("Enter Actor Name") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
        )
        Button(onClick = {
            scope.launch {
                movieData = retrieveMovies(movieDao, actorName)
            }
        }, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Text(text = "Search")
        }

        Text(
            text = movieData
        )
    }
}

suspend fun retrieveMovies(movieDao: MovieDao, actorName: String):String{
    val movies : List<Movie> = movieDao.searchMovieByActor(actorName)
    return if(movies.isEmpty()){
        "No Movies Found !"
    }else{
        movies.joinToString("\n\n"){
            "Title: ${it.title}\nYear: ${it.year}\nActors: ${it.actor}"
        }
    }
}