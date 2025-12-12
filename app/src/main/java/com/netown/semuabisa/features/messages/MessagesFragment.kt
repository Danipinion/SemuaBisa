package com.netown.semuabisa.features.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.netown.semuabisa.HomeActivity // Import HomeActivity
import com.netown.semuabisa.R

class MessagesFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recycler = view.findViewById(R.id.recyclerMessages)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        val data = mutableListOf(
            MessageModel("Gostavo Franci", "Lorem Ipsum dolar sip..", "10.30", R.drawable.avatar_profile, true),
            MessageModel("Gostavo Franci", "Lorem Ipsum dolar sip..", "10.30", R.drawable.avatar_profile),
            MessageModel("Gostavo Franci", "Lorem Ipsum dolar sip..", "10.30", R.drawable.avatar_profile)
        )

        adapter = MessageAdapter(data) { selectedMessage ->
            val activity = requireActivity() as HomeActivity
            activity.loadFragment(ActiveMessagesFragment())
        }

        recycler.adapter = adapter
    }
}