package com.netown.semuabisa.features.messages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.netown.semuabisa.R


class CallModeFragment : Fragment() {
    // TODO: Rename and change types of parameters

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_call_mode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSpeaker = view.findViewById<LinearLayout>(R.id.btnSpeaker)
        val btnMute = view.findViewById<LinearLayout>(R.id.btnMute)
        val btnNotes = view.findViewById<LinearLayout>(R.id.btnNotes)
        val btnEndCall = view.findViewById<Button>(R.id.btnEndCall)

    }
}