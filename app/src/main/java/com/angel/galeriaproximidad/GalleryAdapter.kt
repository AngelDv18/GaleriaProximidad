package com.angel.galeriaproximidad

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File

class GalleryAdapter(
    private val imageList: List<Imagen>,
    private val context: Context
) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gallery_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = imageList[position]

        // Cargar imagen desde ruta local (archivo)
        Glide.with(context)
            .load(File(image.ruta))  // Aquí es importante usar File
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, FullscreenImageActivity::class.java)
            intent.putExtra("ruta", image.ruta)  // Pasamos la ruta del archivo
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = imageList.size
}
