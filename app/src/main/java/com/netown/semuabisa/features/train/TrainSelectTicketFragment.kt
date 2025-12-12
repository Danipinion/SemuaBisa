package com.netown.semuabisa.features.train

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.netown.semuabisa.R
import com.netown.semuabisa.features.train.TrainActivity

class TrainSelectTicketFragment : Fragment(R.layout.fragment_train_select_ticket) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val totalPassengers = arguments?.getInt("TOTAL_PASSENGER") ?: 1

        // Get Vehicle Type from Activity
        val vehicleType = (activity as? TrainActivity)?.vehicleType ?: "Train"

        view.findViewById<ImageButton>(R.id.btnBack)?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvTrainList)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitleSelectTicket)

        // Generate Dynamic Data & Header based on Type
        val dummyData = when (vehicleType) {
            "Bus" -> {
                tvTitle.text = "Select Bus"
                listOf(
                    TrainTicket("Sugeng Rahayu", "MDN", "SBY", "3h 30m"),
                    TrainTicket("Eka Cepat", "MDN", "SOLO", "2h 15m"),
                    TrainTicket("Rosalia Indah", "MDN", "JKT", "10h 00m")
                )
            }
            "Plane" -> {
                tvTitle.text = "Select Flight"
                listOf(
                    TrainTicket("Garuda Indonesia", "SUB", "CGK", "1h 30m"),
                    TrainTicket("Lion Air", "SUB", "DPS", "50m"),
                    TrainTicket("Citilink", "SUB", "YIA", "1h 00m")
                )
            }
            else -> { // Default Train
                tvTitle.text = "Select Train"
                listOf(
                    TrainTicket("Argo Parahyangan", "BDG", "GMR", "3h 00m"),
                    TrainTicket("Turangga", "SBY", "BDG", "10h 30m"),
                    TrainTicket("Lodaya", "SLO", "BDG", "8h 00m")
                )
            }
        }

        val adapter = TrainTicketAdapter(dummyData, vehicleType) { ticket ->
            Toast.makeText(requireContext(), "Selected: ${ticket.name}", Toast.LENGTH_SHORT).show()

            val fragment = TrainSelectSeatFragment()
            val bundle = Bundle()
            bundle.putInt("TOTAL_PASSENGER", totalPassengers)
            fragment.arguments = bundle

            (activity as? TrainActivity)?.loadFragment(fragment)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }
}