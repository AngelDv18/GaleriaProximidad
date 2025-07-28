package com.angel.galeriaproximidad

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.io.File

class FullscreenImageActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private var imagePath: String? = null
    private lateinit var dbHelper: DatabaseHelper

    private val editImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val newPath = result.data?.getStringExtra("editedPath") ?: return@registerForActivityResult

            // Crear nueva entrada en SQLite
            val nombreArchivo = File(newPath).name
            val fecha = obtenerFechaActual()

            val nuevaImagen = Imagen(
                id = 0,
                nombre = nombreArchivo,
                ruta = newPath,
                fecha = fecha
            )
            dbHelper.insertarImagen(nuevaImagen)

            Toast.makeText(this, "✅ Imagen editada guardada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_image)

        imageView = findViewById(R.id.fullscreenImageView)
        imagePath = intent.getStringExtra("ruta")
        dbHelper = DatabaseHelper(this)

        imagePath?.let {
            Glide.with(this).load(File(it)).into(imageView)
        }

        findViewById<ImageButton>(R.id.btnDelete).setOnClickListener {
            confirmDelete()
        }

        findViewById<ImageButton>(R.id.btnEdit).setOnClickListener {
            abrirEditor()
        }
    }

    private fun confirmDelete() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar imagen")
            .setMessage("¿Seguro que deseas eliminar esta imagen?")
            .setPositiveButton("Sí") { _, _ -> deleteImage() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteImage() {
        imagePath?.let { path ->
            val file = File(path)
            if (file.exists()) {
                if (file.delete()) {
                    dbHelper.eliminarPorRuta(path)
                    Toast.makeText(this, "✅ Imagen eliminada", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "❌ No se pudo borrar el archivo", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "⚠️ El archivo ya no existe", Toast.LENGTH_SHORT).show()
                dbHelper.eliminarPorRuta(path)
                finish()
            }
        }
    }

    private fun abrirEditor() {
        val intent = Intent(this, ImageEditorActivity::class.java)
        intent.putExtra("imagePath", imagePath)
        editImageLauncher.launch(intent)
    }

    private fun obtenerFechaActual(): String {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }
}
