package com.netown.semuabisa.features.messages

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.netown.semuabisa.R

class MessageAdapter(private val list: List<MessageModel>) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    private var selectedPosition = -1
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container = view.findViewById<RelativeLayout>(R.id.itemContainer)
        val img = view.findViewById<ImageView>(R.id.imgProfile)
        val name = view.findViewById<TextView>(R.id.txtName)
        val lastMessage = view.findViewById<TextView>(R.id.txtLastMessage)
        val time = view.findViewById<TextView>(R.id.txtTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.name.text = item.name
        holder.lastMessage.text = item.lastMessage
        holder.time.text = item.time
        holder.img.setImageResource(item.image)

        // Apply highlight berdasarkan selectedPosition
        holder.container.setBackgroundColor(
            if (position == selectedPosition) Color.parseColor("#E5F0FF")
            else Color.WHITE
        )

        // OnClick: update selected item
        holder.itemView.setOnClickListener {
            val previous = selectedPosition
            selectedPosition = position

            // Refresh: non-highlight → highlight
            notifyItemChanged(previous)
            notifyItemChanged(selectedPosition)
        }
    }
}
