package com.angel.galeriaproximidad

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GalleryActivity : AppCompatActivity() {

    private lateinit var recyclerViewGallery: RecyclerView
    private lateinit var adapter: GalleryAdapter
    private lateinit var imageList: MutableList<Imagen>
    private lateinit var emptyState: LinearLayout
    private lateinit var tvImageCount: TextView
    private lateinit var tvLastUpdate: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        recyclerViewGallery = findViewById(R.id.recyclerViewGallery)
        emptyState = findViewById(R.id.emptyState)
        tvImageCount = findViewById(R.id.tvImageCount)
        tvLastUpdate = findViewById(R.id.tvLastUpdate)

        findViewById<View>(R.id.btnDelete).setOnClickListener { showDeleteConfirmation() }

        sharedPreferences = getSharedPreferences("GaleriaPrefs", Context.MODE_PRIVATE)
        dbHelper = DatabaseHelper(this)

        setupRecyclerView()
        loadImagesFromSQLite()
    }

    private fun setupRecyclerView() {
        imageList = ArrayList()

        val layoutManager = GridLayoutManager(this, getSpanCount())
        recyclerViewGallery.layoutManager = layoutManager

        recyclerViewGallery.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            val newSpanCount = getSpanCount()
            if (newSpanCount != layoutManager.spanCount) {
                layoutManager.spanCount = newSpanCount
                adapter.notifyItemRangeChanged(0, adapter.itemCount)
            }
        }

        adapter = GalleryAdapter(imageList, this)
        recyclerViewGallery.adapter = adapter

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.grid_spacing)
        recyclerViewGallery.addItemDecoration(GridSpacingItemDecoration(getSpanCount(), spacingInPixels, true))
    }

    private fun getSpanCount(): Int {
        val screenWidthDp = resources.displayMetrics.run { widthPixels / density }
        return when {
            screenWidthDp > 600 -> 4
            screenWidthDp > 480 -> 3
            else -> 2
        }
    }

    private fun loadImagesFromSQLite() {
        imageList.clear()

        val listaDesdeDb = dbHelper.obtenerTodas()
        imageList.addAll(listaDesdeDb)

        if (imageList.isEmpty()) showEmptyState() else showGallery()
        updateImageCountAndDate()
        adapter.notifyDataSetChanged()
    }

    private fun showEmptyState() {
        emptyState.visibility = View.VISIBLE
        recyclerViewGallery.visibility = View.GONE
    }

    private fun showGallery() {
        emptyState.visibility = View.GONE
        recyclerViewGallery.visibility = View.VISIBLE
    }

    private fun updateImageCountAndDate() {
        val count = imageList.size
        tvImageCount.text = "📸 $count imágenes"

        val now = getCurrentTime()
        tvLastUpdate.text = "Última actualización: $now"
        saveUpdateTime(now)
    }

    private fun getCurrentTime(): String {
        val formatter = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
        return formatter.format(java.util.Date())
    }

    private fun saveUpdateTime(time: String) {
        sharedPreferences.edit().putString("ultima_actualizacion", time).apply()
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar imágenes")
            .setMessage("¿Deseas borrar todas las imágenes locales?")
            .setPositiveButton("Sí") { _, _ -> deleteAllLocalImages() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteAllLocalImages() {
        val lista = dbHelper.obtenerTodas()
        for (imagen in lista) {
            dbHelper.eliminarImagen(imagen.id)
            val archivo = java.io.File(imagen.ruta)
            if (archivo.exists()) archivo.delete()
        }
        loadImagesFromSQLite()
        Toast.makeText(this, "✅ Todas las imágenes han sido eliminadas", Toast.LENGTH_SHORT).show()
    }
    override fun onResume() {
        super.onResume()
        loadImagesFromSQLite() // Recarga imágenes desde la base de datos
    }


}
