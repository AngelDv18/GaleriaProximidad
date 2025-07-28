package com.angel.galeriaproximidad

import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ImageEditorActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private var currentBitmap: Bitmap? = null
    private var imagePath: String? = null
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_editor)

        dbHelper = DatabaseHelper(this)
        imageView = findViewById(R.id.imageViewEditor)
        imagePath = intent.getStringExtra("imagePath")

        // Cargar imagen original
        val imageFile = File(imagePath ?: "")
        if (imageFile.exists()) {
            currentBitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            imageView.setImageBitmap(currentBitmap)
        } else {
            Toast.makeText(this, "No se pudo cargar la imagen", Toast.LENGTH_SHORT).show()
            finish()
        }

        findViewById<ImageButton>(R.id.btnText).setOnClickListener {
            addTextOverlay("Hola")
        }

        findViewById<ImageButton>(R.id.btnRotate).setOnClickListener {
            rotateImage()
        }

        findViewById<ImageButton>(R.id.btnFilter).setOnClickListener {
            applySimpleFilter()
        }

        findViewById<ImageButton>(R.id.btnSave).setOnClickListener {
            saveEditedImageAsNew()
        }


    }

    private fun addTextOverlay(text: String) {
        currentBitmap?.let {
            val mutableBitmap = it.copy(Bitmap.Config.ARGB_8888, true)
            val canvas = Canvas(mutableBitmap)
            val paint = Paint().apply {
                color = Color.YELLOW
                textSize = 80f
                typeface = Typeface.DEFAULT_BOLD
                isAntiAlias = true
            }
            canvas.drawText(text, 100f, 200f, paint)
            currentBitmap = mutableBitmap
            imageView.setImageBitmap(currentBitmap)
        }
    }

    private fun rotateImage() {
        currentBitmap?.let {
            val matrix = Matrix().apply { postRotate(90f) }
            currentBitmap = Bitmap.createBitmap(it, 0, 0, it.width, it.height, matrix, true)
            imageView.setImageBitmap(currentBitmap)
        }
    }

    private fun applySimpleFilter() {
        currentBitmap?.let {
            val bmp = it.copy(Bitmap.Config.ARGB_8888, true)
            val width = bmp.width
            val height = bmp.height

            for (i in 0 until width) {
                for (j in 0 until height) {
                    val p = bmp.getPixel(i, j)
                    val r = 255 - Color.red(p)
                    val g = 255 - Color.green(p)
                    val b = 255 - Color.blue(p)
                    bmp.setPixel(i, j, Color.rgb(r, g, b))
                }
            }

            currentBitmap = bmp
            imageView.setImageBitmap(currentBitmap)
        }
    }

    private fun saveEditedImageAsNew() {
        try {
            val fileName = "editada_${System.currentTimeMillis()}.png"
            val file = File(filesDir, fileName)
            FileOutputStream(file).use { fos ->
                currentBitmap?.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }

            val now = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

            val imagen = Imagen(
                nombre = "Imagen editada",
                ruta = file.absolutePath,
                fecha = now
            )
            dbHelper.insertarImagen(imagen)

            Toast.makeText(this, "✅ Imagen guardada como nueva", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()

        } catch (e: Exception) {
            Toast.makeText(this, "❌ Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
