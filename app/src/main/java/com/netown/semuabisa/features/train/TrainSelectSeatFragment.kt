package com.netown.semuabisa.features.train

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.netown.semuabisa.R
import com.netown.semuabisa.features.train.TrainActivity

class TrainSelectSeatFragment : Fragment(R.layout.fragment_train_select_seat) {

    private var totalPassengers = 1
    private var selectedCount = 0
    private lateinit var txtSelectedSeat: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Get the limit passed from previous screens
        totalPassengers = arguments?.getInt("TOTAL_PASSENGER") ?: 1

        view.findViewById<ImageButton>(R.id.btnBack)?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val rvSeat = view.findViewById<RecyclerView>(R.id.rvSeatGrid)
        txtSelectedSeat = view.findViewById<TextView>(R.id.txtSelectedSeat)
        val btnContinue = view.findViewById<Button>(R.id.btnPay)

        // Initial Text
        updateSelectedText()

        // Dummy Seats
        val seatList = List(32) { i ->
            Seat(i, if (i % 5 == 0) SeatStatus.TAKEN else SeatStatus.AVAILABLE)
        }

        val adapter = TrainSeatAdapter(seatList) { seat, position ->
            // Logic for Selecting / Deselecting
            if (seat.status == SeatStatus.AVAILABLE) {
                if (selectedCount < totalPassengers) {
                    seat.status = SeatStatus.SELECTED
                    selectedCount++
                    rvSeat.adapter?.notifyItemChanged(position)
                } else {
                    Toast.makeText(context, "Maksimal $totalPassengers kursi!", Toast.LENGTH_SHORT).show()
                }
            } else if (seat.status == SeatStatus.SELECTED) {
                seat.status = SeatStatus.AVAILABLE
                selectedCount--
                rvSeat.adapter?.notifyItemChanged(position)
            }

            updateSelectedText()
        }

        rvSeat.layoutManager = GridLayoutManager(requireContext(), 4)
        rvSeat.adapter = adapter

        btnContinue?.setOnClickListener {
            if (selectedCount == totalPassengers) {
                (activity as? TrainActivity)?.loadFragment(TrainPaymentFragment())
            } else {
                Toast.makeText(context, "Harap pilih $totalPassengers kursi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateSelectedText() {
        txtSelectedSeat.text = "Selected Seat $selectedCount of $totalPassengers"
    }
}