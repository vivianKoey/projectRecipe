package com.example.myapplication

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.util.concurrent.Executors


class activityReceipe : ComponentActivity() {

    @OptIn(ExperimentalMaterialApi::class)
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
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

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
                    Column {
                        Row {
                            Text(
                                text = "Select Recipe Type: ",

                                modifier = Modifier.padding(4.dp),

                                color = Color.Black, textAlign = TextAlign.Center
                            )

                            Box(
                                modifier = Modifier
                                    .width(150.dp)
                                    .height(70.dp)
                                    .padding(10.dp)
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
                        }
                        Spacer(modifier = Modifier
                            .width(16.dp)
                            .height(20.dp))
                        var txtName by rememberSaveable { mutableStateOf("") }
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Name: ")
                            TextField(
                                value = txtName,
                                onValueChange = {
                                    txtName = it
                                },
                                label = { Text("Name") }
                            )
                        }
                        Spacer(modifier = Modifier
                            .width(16.dp)
                            .height(20.dp))
                        var txtIngredient by rememberSaveable { mutableStateOf("") }
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Ingredient: ")
                            TextField(
                                value = txtIngredient,
                                onValueChange = {
                                    txtIngredient = it
                                },
                                label = { Text("Ingredient") }
                            )
                        }
                        Spacer(modifier = Modifier
                            .width(16.dp)
                            .height(20.dp))
                        var txtStep by rememberSaveable { mutableStateOf("") }
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Steps: ")
                            TextField(
                                value = txtStep,
                                onValueChange = {
                                    txtStep = it
                                },
                                label = { Text("Steps") }
                            )
                        }

                        Spacer(modifier = Modifier
                            .width(16.dp)
                            .height(20.dp))

                        var imageUri by remember {
                            mutableStateOf<Uri?>(null)
                        }
                        val context = LocalContext.current
                        val bitmap =  remember {
                            mutableStateOf<Bitmap?>(null)
                        }

                        val launcher = rememberLauncherForActivityResult(contract =
                        ActivityResultContracts.GetContent()) { uri: Uri? ->
                            imageUri = uri
                        }
                        Column() {
                            Button(onClick = {
                                launcher.launch("image/*")
                            }) {
                                Text(text = "Pick image")
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text("imageUri="+imageUri)
                            Spacer(modifier = Modifier.height(12.dp))

                            imageUri?.let {
                                if (Build.VERSION.SDK_INT < 28) {
                                    bitmap.value = MediaStore.Images
                                        .Media.getBitmap(context.contentResolver,it)

                                } else {
                                    val source = ImageDecoder
                                        .createSource(context.contentResolver,it)
                                    bitmap.value = ImageDecoder.decodeBitmap(source)
                                }

                                bitmap.value?.let {  btm ->
                                    Image(bitmap = btm.asImageBitmap(),
                                        contentDescription =null,
                                        modifier = Modifier.size(100.dp))
                                }
                            }

                        }

                        Spacer(modifier = Modifier
                            .width(16.dp)
                            .height(20.dp))

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                            ) {
                                Button(modifier = Modifier.padding(5.dp),
                                    onClick = {
                                        val executor = Executors.newSingleThreadExecutor()

                                        executor.execute {
                                        val allRecipes = recipeDao.getAllRecipes()
                                            var id=0
                                        for (item in allRecipes) {
                                            id = item.id
                                        }

                                            id=id+1
                                            val newRecipe = imageUri?.let {
                                                Recipe(
                                                    id = id,
                                                    name = txtName,
                                                    type = selecteddrinks,
                                                    ingredients = txtIngredient,
                                                    instructions = txtStep,
                                                    imageUri = it
                                                )
                                            }
                                            if (newRecipe != null) {
                                                recipeDao.insertRecipe(newRecipe)
                                            }
                                        }
                                    }

                                ) {
                                    Text(text = "Insert")
                                }
                                Button(modifier = Modifier.padding(5.dp),

                                    onClick = {
                                        val intent = Intent(
                                            this@activityReceipe,
                                            activityDisplayRecipe::class.java
                                        )
                                        startActivity(intent)
                                    }
                                ) {
                                    Text(text = "Display")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}