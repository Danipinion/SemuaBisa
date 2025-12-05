package com.netown.semuabisa.features.train

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.netown.semuabisa.R
import com.netown.semuabisa.TrainActivity

class TicketTrainFragment : Fragment(R.layout.fragment_ticket_train) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageButton>(R.id.btnBack)?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        view.findViewById<Button>(R.id.btnHome)?.setOnClickListener {
            Toast.makeText(context, "Terima kasih, pesanan selesai!", Toast.LENGTH_SHORT).show()
            (activity as? TrainActivity)?.goBackToHome()
        }
    }
}