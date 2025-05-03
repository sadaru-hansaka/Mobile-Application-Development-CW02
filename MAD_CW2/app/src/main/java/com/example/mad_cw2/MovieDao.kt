package com.example.mad_cw2

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDao {
    @Query("select * from Movie")
    suspend fun getAll():List<Movie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg movies:Movie)

    @Insert
    suspend fun insertMovie(movie: Movie)

    @Delete
    suspend fun deleteMovie(movie: Movie)

    @Query("select * from Movie where actor LIKE '%'||:actor||'%'")
    suspend fun searchMovieByActor(actor:String):List<Movie>

}