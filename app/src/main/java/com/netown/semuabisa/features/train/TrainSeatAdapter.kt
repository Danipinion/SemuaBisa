package com.netown.semuabisa.features.train

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.netown.semuabisa.R

class TrainSeatAdapter(
    private val seats: List<Seat>,
    private val onSeatSelected: (Seat) -> Unit
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

        // 🔵 SET UI SESUAI STATUS
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

        // 🟦 Klik seat
        holder.itemView.setOnClickListener {
            if (seat.status == SeatStatus.AVAILABLE) {
                seat.status = SeatStatus.SELECTED
                onSeatSelected(seat)
                notifyItemChanged(position)
            } else if (seat.status == SeatStatus.SELECTED) {
                seat.status = SeatStatus.AVAILABLE
                onSeatSelected(seat)
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int = seats.size
}
