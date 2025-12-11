package com.netown.semuabisa

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class DriverAdapter(
    private val drivers: List<Driver>,
    private val onClick: (Driver) -> Unit
) : RecyclerView.Adapter<DriverAdapter.DriverViewHolder>() {

    private var selectedPosition = -1

    inner class DriverViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvRating: TextView = view.findViewById(R.id.tvRating)
        val tvDetails: TextView = view.findViewById(R.id.tvDetails)
        val card: CardView = view as CardView

        fun bind(driver: Driver, position: Int) {
            tvName.text = driver.name
            tvRating.text = "⭐ ${driver.rating}"
            tvDetails.text = "${driver.price} | ${driver.time} | ${driver.seats} Seats"

            // Efek Seleksi (Warna Biru Muda jika dipilih)
            if (selectedPosition == position) {
                card.setCardBackgroundColor(Color.parseColor("#DBEAFE")) // Primary-100
            } else {
                card.setCardBackgroundColor(Color.parseColor("#F3F4F6")) // Abu default
            }

            itemView.setOnClickListener {
                selectedPosition = position
                notifyDataSetChanged() // Refresh UI
                onClick(driver)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriverViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_driver, parent, false)
        return DriverViewHolder(view)
    }

    override fun onBindViewHolder(holder: DriverViewHolder, position: Int) {
        holder.bind(drivers[position], position)
    }

    override fun getItemCount() = drivers.size
}