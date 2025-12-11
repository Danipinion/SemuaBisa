package com.netown.semuabisa.features.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.netown.semuabisa.R

class NotificationAdapter(
    private val items: List<NotificationModel>
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgStatus: ImageView = view.findViewById(R.id.imgStatus)
        val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        val txtMessage: TextView = view.findViewById(R.id.txtMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.txtTitle.text = item.title
        holder.txtMessage.text = item.message

        when (item.status) {
            NotificationStatus.SUCCESS ->
                holder.imgStatus.setImageResource(R.drawable.notification_success)

            NotificationStatus.WAITING ->
                holder.imgStatus.setImageResource(R.drawable.notification_waiting)

            NotificationStatus.CANCELED ->
                holder.imgStatus.setImageResource(R.drawable.notification_canceled)
        }
    }

    override fun getItemCount(): Int = items.size
}
