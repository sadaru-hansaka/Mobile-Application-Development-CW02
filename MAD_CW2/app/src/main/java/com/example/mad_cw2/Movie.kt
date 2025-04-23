package com.example.mad_cw2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Movie (
    @PrimaryKey(autoGenerate = true)var id: Int = 0,

    val title:String?,
    val year:String?,
    var rated:String?,
    var release:String?,
    var runtime:String?,
    var genre:String?,
    var director:String?,
    var writer:String?,
    var actor:String?,
    var plot:String?
)