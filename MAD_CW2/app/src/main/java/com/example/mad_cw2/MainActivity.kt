package com.example.mad_cw2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Room
import com.example.mad_cw2.ui.theme.MAD_CW2Theme
import kotlinx.coroutines.launch

lateinit var database:AppDatabase
lateinit var movieDao:MovieDao

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Room.databaseBuilder(this, AppDatabase::class.java, "movie-database").build()
        movieDao = database.movieDao()

        enableEdgeToEdge()
        setContent {
            GUI()
        }
    }
}

@Composable
fun GUI(){
    var movieData by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            scope.launch {
                movieDao.insertAll(*getMovieData().toTypedArray())
            }
        }) {
            Text(text = "Add Movies to DB")
        }
        Button(onClick = {
            var i = Intent(context, SearchMovies::class.java)
            context.startActivity(i)
        }) {
            Text(text = "Search For Movies")
        }
        Button(onClick = {
            var x = Intent(context, SearchActors::class.java)
            context.startActivity(x)
        }) {
            Text(text = "Search for Actors")
        }
        Button(onClick = {
            var y = Intent(context, SearchMoviesBySubstring::class.java)
            context.startActivity(y)
        }) {
            Text(text = "Fetch Movies")
        }
        Button(onClick = {
            scope.launch {
                movieData = retrieveData(movieDao)
            }
        }) {
            Text(text = "Check")
        }
        Button(onClick = {
            scope.launch {
                movieDao.deleteMovie(getMovieData()[0])
            }
        }) {
            Text(text = "Delete")
        }
        Text(
            text = movieData
        )
    }
}


suspend fun retrieveData(movieDao: MovieDao):String{
    var allMovies = ""
    val movies : List<Movie> = movieDao.getAll()
    for (movie in movies){
        allMovies += movie.title + "\n"
    }
    return allMovies
}

fun getMovieData():List<Movie>{
    return listOf(
        Movie(0,
            "The Shawshank Redemption",
            "1994",
            "R",
            "14 Oct 1994",
            "142 min",
            "Drama",
            "Frank Darabont",
            "Stephen King, Frank Darabont",
            "Tim Robbins, Morgan Freeman, Bob Gunton",
            "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency."
        ),
        Movie(0,
            "Batman: The Dark Knight Returns, Part 1",
            "2012",
            "PG-13",
            "25 Sep 2012",
            "76 min",
            "Animation, Action, Crime, Drama, Thriller",
            "Jay Oliva",
            "Bob Kane (character created by: Batman), Frank Miller (comic book), Klaus Janson (comic book), Bob Goodman",
            "Peter Weller, Ariel Winter, David Selby, Wade Williams",
            "Batman has not been seen for ten years. A new breed of criminal ravages Gotham City, forcing 55-year-old Bruce Wayne back into the cape and cowl. But, does he still have what it takes to fight crime in a new era?"
        ),
        Movie(
            0,
            "The Lord of the Rings: The Return of the King",
            "2003",
            "PG-13",
            "17 Dec 2003",
            "201 min",
            "Action, Adventure, Drama",
            "Peter Jackson",
            "J.R.R. Tolkien, Fran Walsh, Philippa Boyens",
            "Elijah Wood, Viggo Mortensen, Ian McKellen",
            "Gandalf and Aragorn lead the World of Men against Sauron's army to draw his gaze from Frodo and Sam as they approach Mount Doom with the One Ring."
        ),
        Movie(
            0,
            "Inception",
            "2010",
            "PG-13",
            "16 Jul 2010",
            "148 min",
            "Action, Adventure, Sci-Fi",
            "Christopher Nolan",
            "Christopher Nolan",
            "Leonardo DiCaprio, Joseph Gordon-Levitt, Elliot Page",
            "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O., but his tragic past may doom the project and his team to disaster."
        ),
        Movie(
            0,
            "The Matrix",
            "1999",
            "R",
            "31 Mar 1999",
            "136 min",
            "Action, Sci-Fi",
            "Lana Wachowski, Lilly Wachowski",
            "Lilly Wachowski, Lana Wachowski",
            "Keanu Reeves, Laurence Fishburne, Carrie-Anne Moss",
            "When a beautiful stranger leads computer hacker Neo to a forbidding underworld, he discovers the shocking truth--the life he knows is the elaborate deception of an evil cyber-intelligence."
        )
    )
}
