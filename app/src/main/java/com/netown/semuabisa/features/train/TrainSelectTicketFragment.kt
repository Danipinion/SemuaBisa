package com.netown.semuabisa.features.train

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.netown.semuabisa.R
import com.netown.semuabisa.features.train.TrainActivity

class TrainSelectTicketFragment : Fragment(R.layout.fragment_train_select_ticket) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view.findViewById<ImageButton>(R.id.btnBack)?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvTrainList)

        val dummyData = listOf(
            TrainTicket("Argo Parahyangan", "BDG", "GMR", "3h 00m"),
            TrainTicket("Turangga", "SBY", "BDG", "10h 30m"),
            TrainTicket("Lodaya", "SLO", "BDG", "8h 00m")
        )

        val adapter = TrainTicketAdapter(dummyData) { ticket ->
            Toast.makeText(requireContext(), "Selected: ${ticket.name}", Toast.LENGTH_SHORT).show()

            (activity as? TrainActivity)?.loadFragment(TrainSelectSeatFragment())
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }
}