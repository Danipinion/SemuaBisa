package com.netown.semuabisa.features.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.netown.semuabisa.R

class HistoryAdapter(private val list: List<HistoryModel>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtFrom = view.findViewById<TextView>(R.id.txtFromAddress)
        val txtTo = view.findViewById<TextView>(R.id.txtToAddress)
        val txtDateTime = view.findViewById<TextView>(R.id.txtDateTime)
        val txtType = view.findViewById<TextView>(R.id.txtType)
        val txtDriver = view.findViewById<TextView>(R.id.txtDriver)
        val txtSeats = view.findViewById<TextView>(R.id.txtSeats)
        val txtPayment = view.findViewById<TextView>(R.id.txtPaymentStatus)
        val imgVehicle = view.findViewById<ImageView>(R.id.imgVehicle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_recent, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.txtFrom.text = item.fromAddress
        holder.txtTo.text = item.toAddress
        holder.txtDateTime.text = "Date & Time: ${item.dateTime}"
        holder.txtType.text = "Type: ${item.type}"
        holder.txtDriver.text = "Driver: ${item.driver}"
        holder.txtSeats.text = "Seats: ${item.seats}"
        holder.txtPayment.text = "Payment Status: ${item.paymentStatus}"
    }
}
