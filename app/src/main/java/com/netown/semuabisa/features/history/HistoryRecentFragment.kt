package com.netown.semuabisa.features.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.netown.semuabisa.R

class HistoryRecentFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history_recent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recycler = view.findViewById(R.id.recyclerHistory)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        val dummyData = listOf(
            HistoryModel(
                fromAddress = "Jl. Address",
                toAddress = "Jl. Address",
                dateTime = "27 December 2025, 13:12",
                type = "Motor",
                driver = "Gustave Vanel",
                seats = "2",
                paymentStatus = "Pending"
            )
        )

        adapter = HistoryAdapter(dummyData)
        recycler.adapter = adapter
    }
}
