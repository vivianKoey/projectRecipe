package com.example.myapplication

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Recipe(
    @PrimaryKey
    val id: Int,
    var name: String,
    val type: String,
    val ingredients: String,
    val instructions: String,
    val imageUri: Uri
)


