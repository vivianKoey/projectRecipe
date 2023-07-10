package com.example.myapplication


import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import coil.compose.rememberImagePainter
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.io.File
import java.util.concurrent.Executors


class activityDetail : ComponentActivity() {

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1

// Check if the permission is already granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
            )
        }

        setContent {
            var expanded by remember {
                mutableStateOf(false)
            }
            var totalRecipeType = ArrayList<String>()

            val recipeTypes = resources.getStringArray(R.array.recipe_types)
            for (recipeType in recipeTypes) {
                totalRecipeType.add(recipeType)
            }

            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    Column {
                        var recipeList2 = ArrayList<Recipe>()
                        var selectedID: Int = 0
                        var selectedName: String = ""
                        var selectedType: String = ""
                        var selectedIngredients: String = ""
                        var selectedInstructions: String = ""
                        var selectedUri: Uri? =null
                        var recipeList = DataHolder.arrayList2

                        for (recipe in recipeList) {
                            selectedID=recipe.id
                            selectedName = recipe.name
                            selectedType = recipe.type
                            selectedIngredients = recipe.ingredients
                            selectedInstructions = recipe.instructions
                            selectedUri= recipe.imageUri
                        }
                        var txtID by rememberSaveable { mutableStateOf(selectedID) }
                        var txtUsername by rememberSaveable { mutableStateOf(selectedName) }
                        var txtType by rememberSaveable { mutableStateOf(selectedType) }
                        var txtIngredients by rememberSaveable { mutableStateOf(selectedIngredients) }
                        var txtInstructions by rememberSaveable { mutableStateOf(selectedInstructions) }
                        var txtImageUri by rememberSaveable {
                            mutableStateOf(selectedUri)

                        }
                        var imageUri by remember {
                            mutableStateOf<Uri?>(txtImageUri)
                        }
                        var selecteddrinks by remember {
                            mutableStateOf(selectedType)
                        }
                        Toolbar(
                            title = "Detail Recipe",
                            navigationIcon = Icons.Default.ArrowBack,
                            onNavigationIconClick = {
                                val intent =
                                    Intent(this@activityDetail, activityDisplayRecipe::class.java)
                                startActivity(intent)
                            })
                        Spacer(
                            modifier = Modifier
                                .width(16.dp)
                                .height(20.dp)
                        )
                        val context = LocalContext.current
                        val bitmap =  remember {
                            mutableStateOf<Bitmap?>(null)
                        }
                        val launcher = rememberLauncherForActivityResult(contract =
                        ActivityResultContracts.GetContent()) { uri: Uri? ->
                            imageUri = uri
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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
                                        modifier = Modifier.size(100.dp)
                                            .clickable {
                                                launcher.launch("image/*")
                                            }
                                    )
                                }
                            }
                        }


                        Spacer(
                            modifier = Modifier
                                .width(16.dp)
                                .height(20.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Name: ")
                                TextField(
                                    value = txtUsername,
                                    onValueChange = {
                                        txtUsername = it
                                    },
                                    label = { Text("Name") }
                                )
                        }
                        Spacer(
                            modifier = Modifier
                                .width(16.dp)
                                .height(20.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Type: ")

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
                                    txtType=selecteddrinks
                                }
                            }
                        }

                        Spacer(
                            modifier = Modifier
                                .width(16.dp)
                                .height(20.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Ingredients: ")
                                TextField(
                                    value = txtIngredients,
                                    onValueChange = {
                                        txtIngredients = it
                                    },
                                    label = { Text("Ingredients") }
                                )
                        }

                        Spacer(
                            modifier = Modifier
                                .width(16.dp)
                                .height(20.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Instructions: ")
                                TextField(
                                    value = txtInstructions,
                                    onValueChange = {
                                        txtInstructions = it
                                    },
                                    label = { Text("Instructions") }
                                )
                        }
                        Log.d(ContentValues.TAG, "txtInstructions1=" + txtInstructions)

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.padding(5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(modifier = Modifier.padding(5.dp),

                                onClick = {
                                    val executor = Executors.newSingleThreadExecutor()

                                    executor.execute {
                                        val database =
                                            RecipeDatabase.getDatabase(applicationContext)
                                        val recipeDao = database.recipeDao()
                                        recipeDao.deleteRecipe(txtID)
                                    }
                                    val intent = Intent(
                                        this@activityDetail,
                                        activityDisplayRecipe::class.java
                                    )
                                    startActivity(intent)
                                }
                            ) {
                                Text(text = "Delete")
                            }

                            Button(modifier = Modifier.padding(5.dp),
                                onClick = {
                                     val executor = Executors.newSingleThreadExecutor()

                                    executor.execute {
                                        val database =
                                            RecipeDatabase.getDatabase(applicationContext)

                                        val recipeDao = database.recipeDao()

                                        imageUri?.let {
                                            recipeDao.updateRecipe(txtID,txtUsername,selecteddrinks,txtIngredients,txtInstructions,
                                                it
                                            )
                                        }

                                    }
                                    val intent = Intent(
                                        this@activityDetail,
                                        activityDisplayRecipe::class.java
                                    )
                                    startActivity(intent)
                                }
                            ){
                                Text(text = "Edit")
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
