package com.angel.galeriaproximidad

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "imagenes.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE imagenes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                ruta TEXT,
                fecha TEXT
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS imagenes")
        onCreate(db)
    }

    fun insertarImagen(imagen: Imagen): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", imagen.nombre)
            put("ruta", imagen.ruta)
            put("fecha", imagen.fecha)
        }
        return db.insert("imagenes", null, values)
    }

    fun obtenerTodas(): List<Imagen> {
        val db = readableDatabase
        val lista = mutableListOf<Imagen>()
        val cursor = db.rawQuery("SELECT * FROM imagenes ORDER BY id DESC", null)
        while (cursor.moveToNext()) {
            lista.add(
                Imagen(
                    id = cursor.getLong(0),
                    nombre = cursor.getString(1),
                    ruta = cursor.getString(2),
                    fecha = cursor.getString(3)
                )
            )
        }
        cursor.close()
        return lista
    }

    fun eliminarImagen(id: Long) {
        writableDatabase.delete("imagenes", "id=?", arrayOf(id.toString()))
    }

    fun eliminarPorRuta(ruta: String) {
        writableDatabase.delete("imagenes", "ruta = ?", arrayOf(ruta))
    }

}
