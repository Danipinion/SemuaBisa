package com.netown.semuabisa.features.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.netown.semuabisa.HomeActivity
import com.netown.semuabisa.R

class HistoryTicketFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history_ticket, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind Views
        val txtTicketId = view.findViewById<TextView>(R.id.ticketId)
        val txtRoute = view.findViewById<TextView>(R.id.route)
        val txtDate = view.findViewById<TextView>(R.id.ticketDate)
        val txtOrigin = view.findViewById<TextView>(R.id.txtOrigin)
        val txtDestination = view.findViewById<TextView>(R.id.txtDestination)
        val txtTrainName = view.findViewById<TextView>(R.id.trainName)
        val txtSeat = view.findViewById<TextView>(R.id.seatNumber)
        val btnBack = view.findViewById<ImageView>(R.id.btnBack)

        // Get Data
        val origin = arguments?.getString("ORIGIN") ?: "MDN"
        val dest = arguments?.getString("DEST") ?: "JKT"
        val date = arguments?.getString("DATE") ?: "-"
        val train = arguments?.getString("TRAIN") ?: "-"
        val seat = arguments?.getString("SEAT") ?: "-"

        // Set Data
        txtTicketId.text = "2-${(1000..9999).random()}"
        txtRoute.text = "$origin - $dest"
        txtDate.text = date
        txtOrigin.text = origin
        txtDestination.text = dest
        txtTrainName.text = train
        txtSeat.text = seat

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? HomeActivity)?.setBottomNavVisibility(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? HomeActivity)?.setBottomNavVisibility(true)
    }
}