package com.netown.semuabisa.features.profile

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.netown.semuabisa.R

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       val view =  inflater.inflate(R.layout.fragment_profile, container, false)

        val btnEditProfile = view.findViewById<Button>(R.id.btnEditProfile)
        val btnDriverFav = view.findViewById<LinearLayout>(R.id.btnDriverFav)
        val btnClearCache = view.findViewById<LinearLayout>(R.id.btnClearCache)
        val btnClearHistory = view.findViewById<LinearLayout>(R.id.btnClearHistory)
        val btnLogout = view.findViewById<LinearLayout>(R.id.btnLogout)


        btnClearCache.setOnClickListener {
            showCustomDialog(R.layout.dialog_clear_cache)
        }

        btnClearHistory.setOnClickListener {
            showCustomDialog(R.layout.dialog_clear_history)
        }

        btnLogout.setOnClickListener {
            showCustomDialog(R.layout.dialog_logout)
        }

        return view
    }
    private fun showCustomDialog(layoutId: Int){
        val dialog = Dialog(requireContext())
        dialog.setContentView(layoutId)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnCancel = dialog.findViewById<View>(R.id.btnCancel)
        val btnYes = dialog.findViewById<View>(R.id.btnYes)

        btnCancel?.setOnClickListener { dialog.dismiss() }
        btnYes?.setOnClickListener {
            Toast.makeText(requireContext(),"Confirmed!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }
}