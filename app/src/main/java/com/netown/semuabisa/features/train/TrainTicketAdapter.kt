package com.netown.semuabisa.features.train

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.netown.semuabisa.R

class TrainTicketAdapter(
    private val trains: List<TrainTicket>,
    private val vehicleType: String, // Receive vehicle type
    private val onSelect: (TrainTicket) -> Unit
) : RecyclerView.Adapter<TrainTicketAdapter.ViewHolder>() {

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val txtName = item.findViewById<TextView>(R.id.txtTrainName)
        val txtFrom = item.findViewById<TextView>(R.id.txtFrom)
        val txtTo = item.findViewById<TextView>(R.id.txtTo)
        val txtDuration = item.findViewById<TextView>(R.id.txtDuration)
        val imgIcon = item.findViewById<ImageView>(R.id.imgTransportIcon) // Bind Icon
        val btnSelect = item.findViewById<ImageView>(R.id.right_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_train_ticket, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = trains[position]

        holder.txtName.text = item.name
        holder.txtFrom.text = item.from
        holder.txtTo.text = item.to
        holder.txtDuration.text = "Duration: ${item.duration}"

        // Set Dynamic Icon
        val iconRes = when (vehicleType) {
            "Bus" -> R.drawable.bus
            "Plane" -> R.drawable.plane
            else -> R.drawable.train // Default Train
        }
        holder.imgIcon.setImageResource(iconRes)

        holder.itemView.setOnClickListener { onSelect(item) }
    }

    override fun getItemCount() = trains.size
}