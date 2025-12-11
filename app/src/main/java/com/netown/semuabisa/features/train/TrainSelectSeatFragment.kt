package com.netown.semuabisa.features.train

import android.annotation.SuppressLint
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

    private var selectedCount = 0
    private lateinit var txtSelectedSeat: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view.findViewById<ImageButton>(R.id.btnBack)?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val rvSeat = view.findViewById<RecyclerView>(R.id.rvSeatGrid)
        txtSelectedSeat = view.findViewById<TextView>(R.id.txtSelectedSeat)
        val btnContinue = view.findViewById<Button>(R.id.btnPay)

        val seatList = List(32) { i ->
            Seat(i, if (i % 5 == 0) SeatStatus.TAKEN else SeatStatus.AVAILABLE)
        }

        val adapter = TrainSeatAdapter(seatList) { _ ->
            selectedCount = seatList.count { it.status == SeatStatus.SELECTED }
            txtSelectedSeat.text = "Selected: $selectedCount"
        }

        rvSeat.layoutManager = GridLayoutManager(requireContext(), 4)
        rvSeat.adapter = adapter

        btnContinue?.setOnClickListener {
            if (selectedCount > 0) {
                (activity as? TrainActivity)?.loadFragment(TicketTrainFragment())
            } else {
                Toast.makeText(context, "Pilih kursi dulu!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}