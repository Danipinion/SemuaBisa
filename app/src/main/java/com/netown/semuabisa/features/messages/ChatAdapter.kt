package com.netown.semuabisa.features.messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.netown.semuabisa.R

class ChatAdapter(private val list: MutableList<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val LEFT = 0
    private val RIGHT = 1

    override fun getItemViewType(position: Int): Int {
        return if (list[position].isMe) RIGHT else LEFT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == LEFT) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_left, parent, false)
            LeftHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_right, parent, false)
            RightHolder(view)
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]

        if (holder is LeftHolder) {
            holder.msg.text = item.message
        } else if (holder is RightHolder) {
            holder.msg.text = item.message
        }
    }

    class LeftHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val msg: TextView = itemView.findViewById(R.id.txtMessage)
    }

    class RightHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val msg: TextView = itemView.findViewById(R.id.txtMessage)
    }
}
