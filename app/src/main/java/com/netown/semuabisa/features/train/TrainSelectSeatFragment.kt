package com.netown.semuabisa.features.train

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.netown.semuabisa.R

class TrainSelectSeatFragment : Fragment() {


    private lateinit var txtSelectedSeat: TextView
    private lateinit var seatAdapter: TrainSeatAdapter
    private lateinit var rvSeat: RecyclerView
    private var selectedCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_train_select_seat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        rvSeat = view.findViewById(R.id.rvSeatGrid)
        txtSelectedSeat = view.findViewById(R.id.txtSelectedSeat)


        // ⬇️ Ambil data kursi dari fungsi dummy
        val seatList = generateSeats()

        seatAdapter = TrainSeatAdapter(seatList) { seat ->
            updateSelectedSeatCount(seatList)
        }

        rvSeat.layoutManager = GridLayoutManager(requireContext(), 4)
        rvSeat.adapter = seatAdapter
    }

    // ⬇️  ⬇️  ⬇️ TARUH FUNGSI INI DI DALAM TrainSelectSeatFragment
    private fun generateSeats(): List<Seat> {
        val seats = mutableListOf<Seat>()

        for (i in 0 until 32) {
            val type = when {
                i % 4 == 3 -> SeatStatus.TAKEN     // kolom ke-4 adalah X
                else -> SeatStatus.AVAILABLE
            }
            seats.add(Seat(i, type))
        }
        return seats
    }

    @SuppressLint("SetTextI18n")
    private fun updateSelectedSeatCount(list: List<Seat>) {
        selectedCount = list.count { it.status == SeatStatus.SELECTED }
        txtSelectedSeat.text = "Selected Seat $selectedCount of 3"
    }
}
