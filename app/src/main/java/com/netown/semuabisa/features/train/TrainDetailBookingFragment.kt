package com.netown.semuabisa.features.train

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.netown.semuabisa.R
import com.netown.semuabisa.features.train.TrainActivity
import java.util.Calendar

class TrainDetailBookingFragment : Fragment() {

    private var adultCount = 1
    private var childCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_train_detail_booking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val edtFrom = view.findViewById<EditText>(R.id.edtFrom)
        val edtTo = view.findViewById<EditText>(R.id.edtTo)
        val edtDate = view.findViewById<EditText>(R.id.edtDate)

        val txtAdult = view.findViewById<TextView>(R.id.txtAdultCount)
        val txtChild = view.findViewById<TextView>(R.id.txtChildCount)

        edtDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val dateString = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    edtDate.setText(dateString)
                },
                year, month, day
            )
            datePickerDialog.datePicker.minDate = calendar.timeInMillis
            datePickerDialog.show()
        }

        view.findViewById<ImageView>(R.id.btnPlusAdult).setOnClickListener {
            if (adultCount < 10) {
                adultCount++
                txtAdult.text = adultCount.toString()
            } else {
                Toast.makeText(context, "Maksimal 10 Dewasa", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<ImageView>(R.id.btnMinusAdult).setOnClickListener {
            if (adultCount > 1) {
                adultCount--
                txtAdult.text = adultCount.toString()
            } else {
                Toast.makeText(context, "Minimal 1 Dewasa", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<ImageView>(R.id.btnPlusChild).setOnClickListener {
            if (childCount < 5) {
                childCount++
                txtChild.text = childCount.toString()
            }
        }

        view.findViewById<ImageView>(R.id.btnMinusChild).setOnClickListener {
            if (childCount > 0) {
                childCount--
                txtChild.text = childCount.toString()
            }
        }

        view.findViewById<Button>(R.id.btnBack)?.setOnClickListener {
            (activity as? TrainActivity)?.goBackToHome()
        }

        view.findViewById<Button>(R.id.btnContinue)?.setOnClickListener {
            val fromLoc = edtFrom.text.toString().trim()
            val toLoc = edtTo.text.toString().trim()
            val dateLoc = edtDate.text.toString().trim()

            if (fromLoc.isEmpty()) {
                edtFrom.error = "Asal tidak boleh kosong"
                return@setOnClickListener
            }
            if (toLoc.isEmpty()) {
                edtTo.error = "Tujuan tidak boleh kosong"
                return@setOnClickListener
            }
            if (dateLoc.isEmpty()) {
                Toast.makeText(context, "Pilih tanggal keberangkatan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Calculate Total Seats
            val totalPassengers = adultCount + childCount

            // Pass data to next fragment
            val fragment = TrainSelectTicketFragment()
            val bundle = Bundle()
            bundle.putInt("TOTAL_PASSENGER", totalPassengers)
            fragment.arguments = bundle

            (activity as? TrainActivity)?.loadFragment(fragment)
        }
    }
}