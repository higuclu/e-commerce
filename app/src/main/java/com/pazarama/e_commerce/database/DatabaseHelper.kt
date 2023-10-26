package com.pazarama.e_commerce.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.pazarama.e_commerce.model.Category
import com.pazarama.e_commerce.model.Product

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "Products.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE Categories (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)")
        db?.execSQL("CREATE TABLE Products (id INTEGER PRIMARY KEY AUTOINCREMENT, category_id INTEGER, name TEXT, stock INTEGER, price REAL, FOREIGN KEY (category_id) REFERENCES Categories(id))")

        initializeCategories(db)

    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS Products")
        db?.execSQL("DROP TABLE IF EXISTS Categories")
        onCreate(db)
    }

    private fun initializeCategories(db: SQLiteDatabase?) {
        val categories = listOf("Phone", "Laptop", "Tablet")

        categories
            .map {
                ContentValues().apply {
                    put("name", it)
                }
            }
            .forEach { db?.insert("Categories", null, it) }
    }

    //To insert into database
    fun insertProduct(name: String, stock: Int, price: Float, categoryId: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("category_id", categoryId)
            put("stock", stock)
            put("price", price)
        }
        db.insert("Products", null, values)
        db.close()
    }

    fun getAllProductsByCategoryId(categoryId: Int): List<Product> {
        val db = readableDatabase
        val products = mutableListOf<Product>()
        val cursor = db.rawQuery(
            "SELECT * FROM Products WHERE category_id = ?",
            arrayOf(categoryId.toString())
        )

        val idIndex = cursor.getColumnIndex("id")
        val nameIndex = cursor.getColumnIndex("name")
        val stockIndex = cursor.getColumnIndex("stock")
        val priceIndex = cursor.getColumnIndex("price")

        while (cursor.moveToNext()) {
            if (idIndex >= 0 && nameIndex >= 0 && stockIndex >= 0 && priceIndex >= 0) { // index can be -1 warning
                val id = cursor.getLong(idIndex)
                val name = cursor.getString(nameIndex)
                val stock = cursor.getInt(stockIndex)
                val price = cursor.getFloat(priceIndex)
                val product = Product(id, name, stock, price)
                products.add(product)
            }
        }
        cursor.close()
        db.close()
        return products
    }

    fun getAllCategories(): List<Category> {
        val db = readableDatabase
        val categories = mutableListOf<Category>()
        val cursor = db.rawQuery("SELECT * FROM Categories", null)

        val idIndex = cursor.getColumnIndex("id")
        val nameIndex = cursor.getColumnIndex("name")

        while (cursor.moveToNext()) {
            if (idIndex >= 0 && nameIndex >= 0) { // index can be -1 warning
                val id = cursor.getInt(idIndex)
                val name = cursor.getString(nameIndex)
                val category = Category(id, name)
                categories.add(category)
            }
        }
        cursor.close()
        db.close()
        return categories
    }
}
