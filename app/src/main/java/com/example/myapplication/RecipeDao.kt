package com.example.myapplication

import android.net.Uri
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.sql.Struct

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipe")
    fun getAllRecipes(): List<Recipe>

    @Insert
    fun insertRecipe(recipe: Recipe)

    @Query("UPDATE recipe SET name= :recipeName, type= :recipeType, ingredients= :recipeIngredients, instructions= :recipeInstruction, imageUri= :recipeImage WHERE id = :recipeId")
    fun updateRecipe(recipeId: Int,recipeName:String,recipeType:String,recipeIngredients:String,recipeInstruction:String,recipeImage: Uri)

    @Query("DELETE FROM recipe WHERE id = :recipeId")
    fun deleteRecipe(recipeId: Int)
}


