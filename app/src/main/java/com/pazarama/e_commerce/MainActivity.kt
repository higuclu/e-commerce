@file:OptIn(ExperimentalMaterial3Api::class)

package com.pazarama.e_commerce

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pazarama.e_commerce.database.DatabaseHelper
import com.pazarama.e_commerce.model.Product

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProductListScreen()
        }
    }
}

@Composable
fun ProductListScreen() {
    val context = LocalContext.current
    val databaseHelper = remember { DatabaseHelper(context) }
    val categories = databaseHelper.getAllCategories()
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    var productName by remember { mutableStateOf("") }
    var productStock by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var expanded = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        Arrangement.Top, Alignment.Start
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable {
                    expanded.value = !expanded.value
                }.background(color = MaterialTheme.colorScheme.inverseOnSurface))
        {
            Text(text = "Select Category : ")
            Text(text = selectedCategory.name )
            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        onClick = {
                            selectedCategory = category
                            expanded.value = false
                        }, text = { Text(text = category.name) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.fillMaxWidth().padding(4.dp))
        TextField(
            value = productName,
            onValueChange = { productName = it },
            label = { Text("Product Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        TextField(
            value = productStock,
            onValueChange = { productStock = it },
            label = { Text("Stock") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        TextField(
            value = productPrice,
            onValueChange = { productPrice = it },
            label = { Text("Price") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        Button(
            onClick = {
                if (productName.isNotEmpty() && productStock.isNotEmpty() && productPrice.isNotEmpty()) {
                    val stock = productStock.toInt()
                    val price = productPrice.toFloat()
                    databaseHelper.insertProduct(productName, stock, price, selectedCategory.id)
                    productName = ""
                    productStock = ""
                    productPrice = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Product")
        }

        Text(
            text = "Products :",
            fontSize = 24.sp,
            modifier = Modifier.padding(8.dp),
            color = Color.Black
        )

        val products = databaseHelper.getAllProductsByCategoryId(selectedCategory.id)
        LazyColumn {
            items(products) { product ->
                ProductItem(product = product)
            }
        }
    }
}

@Composable
fun ProductItem(product: Product) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.onPrimary, MaterialTheme.shapes.medium)
            .border(BorderStroke(1.dp, Color.Black), MaterialTheme.shapes.medium)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            ProductHeader(text = product.name)
            ProductText(text = "Stock: ${product.stock}")
            ProductText(text = "Price: ${product.price} TL")
        }
    }
}


@Composable
fun ProductText(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        color = Color.Gray,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
    )
}

@Composable
fun ProductHeader(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        color = Color.Black,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
    )
}