package com.netown.semuabisa.features.messages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.netown.semuabisa.R


class ActiveMessagesFragment : Fragment() {

    private lateinit var adapter: ChatAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var edt: EditText

    private val messages = mutableListOf(
        ChatMessage("lorem ipsum", false),
        ChatMessage("lorem ipsum dolar", false),
        ChatMessage("lorem ipsum dolar", true),
        ChatMessage(
            "lorem ipsum dolar si amet\n...\ndolarr moneyy",
            true
        ),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_active_messages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        recycler = view.findViewById(R.id.recyclerChat)
        edt = view.findViewById(R.id.edtMessage)

        adapter = ChatAdapter(messages)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(requireContext())

        // Auto scroll to bottom
        recycler.scrollToPosition(messages.size - 1)

        view.findViewById<ImageView>(R.id.btnSend).setOnClickListener {
            val text = edt.text.toString()
            if (text.isNotEmpty()) {
                messages.add(ChatMessage(text, true))
                adapter.notifyItemInserted(messages.size - 1)
                recycler.scrollToPosition(messages.size - 1)
                edt.text.clear()
            }
        }

        // Quick Reply 1
        view.findViewById<TextView>(R.id.btnQuick1).setOnClickListener {
            sendQuick(it as TextView)
        }

        // Quick Reply 2
        view.findViewById<TextView>(R.id.btnQuick2).setOnClickListener {
            sendQuick(it as TextView)
        }
    }

    private fun sendQuick(tv: TextView) {
        val text = tv.text.toString()
        messages.add(ChatMessage(text, true))
        adapter.notifyItemInserted(messages.size - 1)
        recycler.scrollToPosition(messages.size - 1)
    }
}

