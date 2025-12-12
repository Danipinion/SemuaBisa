package com.netown.semuabisa.features.train

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.netown.semuabisa.R
import com.netown.semuabisa.features.train.TrainActivity

class TrainPaymentFragment : Fragment(R.layout.fragment_train_payment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageView>(R.id.btnBack)
        val btnPayNow = view.findViewById<Button>(R.id.btnPayNow)
        val rbCash = view.findViewById<RadioButton>(R.id.rbCash)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnPayNow.setOnClickListener {
            // You can add logic here to check which radio button is checked
            // For now, we simulate a successful payment directly

            Toast.makeText(requireContext(), "Payment Successful!", Toast.LENGTH_SHORT).show()

            // Navigate to Ticket (Success Screen)
            (activity as? TrainActivity)?.loadFragment(TicketTrainFragment())
        }
    }
}