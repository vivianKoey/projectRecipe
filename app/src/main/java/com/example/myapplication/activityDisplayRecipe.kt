package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.util.concurrent.Executors

class activityDisplayRecipe : ComponentActivity() {
    var bol:Boolean=false

    @OptIn(ExperimentalMaterialApi::class)
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = RecipeDatabase.getDatabase(applicationContext)

        val recipeDao = database.recipeDao()

        val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
            )
        }

        setContent {
            MyApplicationTheme {
                var arrayList5=remember { mutableStateListOf<Recipe>()}

                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    Column {
                        Toolbar(
                            title = "Recipe List",
                            navigationIcon = Icons.Default.ArrowBack,
                            onNavigationIconClick = {

                                val intent = Intent(this@activityDisplayRecipe, activityReceipe::class.java)

                                startActivity(intent)
                            })
                        val selectedID = remember { mutableStateOf(0) }
                        val selectedName = remember { mutableStateOf("Name") }
                        val selectedType = remember { mutableStateOf("Type") }
                        val selectedIngredients = remember { mutableStateOf("Ingredients") }
                        val selectedInstructions = remember { mutableStateOf("Instructions") }
                        val selectedUri =remember { mutableStateOf("") }


                        var expanded by remember {
                            mutableStateOf(false)
                        }
                        var totalRecipeType = ArrayList<String>()
                        val recipeTypes = resources.getStringArray(R.array.recipe_types)
                        for (recipeType in recipeTypes) {
                            totalRecipeType.add(recipeType)
                        }

                        var selecteddrinks by remember {
                            mutableStateOf(totalRecipeType[0])
                        }
                        Text(
                            fontSize=10.sp,
                            text = "Please Select One Recipe Type",

                            modifier = Modifier.padding(4.dp),

                            color = Color.Black, textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            fontSize=10.sp,
                            text = "Please Click One of the Recipe to View and Edit Recipe ",

                            modifier = Modifier.padding(4.dp),

                            color = Color.Black, textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Row {
                            Text(
                                fontSize=10.sp,
                                text = "Select Recipe Type: ",

                                modifier = Modifier.padding(4.dp),

                                color = Color.Black, textAlign = TextAlign.Center
                            )

                            Box(
                                modifier = Modifier
                                    .width(150.dp)
                                    .height(70.dp)
                                    .padding(5.dp)
                            ) {
                                ExposedDropdownMenuBox(
                                    expanded = expanded,
                                    onExpandedChange = { expanded = !expanded }) {
                                    TextField(
                                        value = selecteddrinks,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = expanded
                                            )
                                        },
                                        textStyle = TextStyle.Default.copy(fontSize = 10.sp)
                                    )
                                    ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }) {
                                        totalRecipeType?.forEach {
                                            DropdownMenuItem(
                                                onClick = {
                                                    selecteddrinks = it
                                                    expanded = false
                                                }) {
                                                Text(text = it)
                                            }
                                        }
                                    }

                                }
                            }

                            Button(modifier = Modifier.padding(5.dp),

                                onClick = {
                                    bol = true
                                    arrayList5.clear()
                                    val executor = Executors.newSingleThreadExecutor()

                                    executor.execute {
                                        val allRecipes = recipeDao.getAllRecipes()
                                        for (item in allRecipes) {
                                            if (item.type == selecteddrinks) {
                                                arrayList5.add(Recipe(item.id,item.name,item.type,item.ingredients,item.instructions,item.imageUri))
                                            }
                                        }
                                    }
                                }
                            ) {
                                Text(text = "Selected")
                            }
                        }

                        LazyColumn {

                          itemsIndexed(arrayList5) { index, item ->
                                var emID=arrayList5[index].id
                                var emName = arrayList5[index].name
                                var emType=arrayList5[index].type
                                var emIngredients=arrayList5[index].ingredients
                                var emInstructions=arrayList5[index].instructions
                                var emUri=arrayList5[index].imageUri

                                Spacer(modifier = Modifier.width(5.dp))

                                Card(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .clickable {
                                            selectedID.value=emID
                                            selectedName.value = emName
                                            selectedType.value = emType
                                            selectedIngredients.value = emIngredients
                                            selectedInstructions.value = emInstructions
                                            selectedUri.value = emUri.toString()

                                            val intent = Intent(
                                                this@activityDisplayRecipe,
                                                activityDetail::class.java
                                            )

                                            DataHolder.arrayList2.add(Recipe(selectedID.value,selectedName.value, selectedType.value, selectedIngredients.value,selectedInstructions.value, Uri.parse(selectedUri.value)))
                                            startActivity(intent)
                                        },
                                    elevation = 6.dp,

                                    ) {
                                    Row {
                                        Column(
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .fillMaxWidth(),
                                            horizontalAlignment = Alignment.Start,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "Name: " + emName,

                                                modifier = Modifier.padding(4.dp),

                                                color = Color.Black, textAlign = TextAlign.Center
                                            )

                                            Spacer(modifier = Modifier.width(5.dp))

                                            Text(

                                                text = "Type : " + emType,

                                                modifier = Modifier.padding(4.dp),

                                                color = Color.Black, textAlign = TextAlign.Center
                                            )

                                            Spacer(modifier = Modifier.width(5.dp))

                                            Text(
                                                text = "Ingredients : " + emIngredients,


                                                modifier = Modifier.padding(4.dp),

                                                color = Color.Black, textAlign = TextAlign.Center
                                            )

                                            Spacer(modifier = Modifier.width(5.dp))

                                            Text(

                                                text = "Instructions : " + emInstructions,

                                                modifier = Modifier.padding(4.dp),

                                                color = Color.Black, textAlign = TextAlign.Center
                                            )

                                            Spacer(modifier = Modifier.width(5.dp))

                                            Text(

                                                text = "Image Uri : " + emUri,

                                                modifier = Modifier.padding(4.dp),

                                                color = Color.Black
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    @Composable
    fun Toolbar(
        title: String,
        navigationIcon: ImageVector?,
        onNavigationIconClick: () -> Unit
    ) {
        TopAppBar(
            title = { Text(text = title) },
            navigationIcon = {
                if (navigationIcon != null) {
                    IconButton(onClick = onNavigationIconClick) {
                        Icon(imageVector = navigationIcon, contentDescription = null)
                    }
                }
            },
            modifier = Modifier.padding(bottom = 4.dp)
        )
    }

}