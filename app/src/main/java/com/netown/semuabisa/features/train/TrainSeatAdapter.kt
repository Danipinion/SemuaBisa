package com.netown.semuabisa.features.train

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.netown.semuabisa.R

class TrainSeatAdapter(
    private val seats: List<Seat>,
    private val onSeatClick: (Seat, Int) -> Unit // Return Seat and Position
) : RecyclerView.Adapter<TrainSeatAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val seatBox: View = itemView.findViewById(R.id.seatBox)
        val seatTakenIcon: ImageView = itemView.findViewById(R.id.seatTakenIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_seat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val seat = seats[position]

        // Set UI based on status
        when (seat.status) {
            SeatStatus.AVAILABLE -> {
                holder.seatBox.setBackgroundResource(R.drawable.available)
                holder.seatTakenIcon.visibility = View.GONE
            }
            SeatStatus.SELECTED -> {
                holder.seatBox.setBackgroundResource(R.drawable.selected)
                holder.seatTakenIcon.visibility = View.GONE
            }
            SeatStatus.TAKEN -> {
                holder.seatBox.setBackgroundResource(R.drawable.taken)
                holder.seatTakenIcon.visibility = View.VISIBLE
            }
        }

        // Handle Click - Delegate logic to Fragment
        holder.itemView.setOnClickListener {
            onSeatClick(seat, position)
        }
    }

    override fun getItemCount(): Int = seats.size
}