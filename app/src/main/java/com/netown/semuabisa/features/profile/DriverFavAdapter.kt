package com.netown.semuabisa.features.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.netown.semuabisa.R

class DriverFavAdapter(
    private val list: List<DriverFavorite>
) : RecyclerView.Adapter<DriverFavAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val imgProfile = view.findViewById<ImageView>(R.id.imgProfile)
        val txtName = view.findViewById<TextView>(R.id.txtName)
        val txtRating = view.findViewById<TextView>(R.id.txtRating)
        val btnFavorite = view.findViewById<ImageView>(R.id.btnFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_driver_fav, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.imgProfile.setImageResource(item.image)
        holder.txtName.text = item.name
        holder.txtRating.text = "${item.rating} (${item.reviews}+ Reviews)"
        holder.btnFavorite.setImageResource(
            if (item.isFavorite) R.drawable.icon_park_solid_like else R.drawable.icon_park_solid_like
        )

        holder.btnFavorite.setOnClickListener {
            Toast.makeText(holder.view.context, "Favorite clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
