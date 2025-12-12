package com.netown.semuabisa.features.messages

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.netown.semuabisa.HomeActivity
import com.netown.semuabisa.R

class CallModeFragment : Fragment() {

    private var seconds = 0
    private var isRunning = true
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var tvTimer: TextView

    private var isMuted = false
    private var isSpeakerOn = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_call_mode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvTimer = view.findViewById(R.id.tvTimer)
        val btnEndCall = view.findViewById<MaterialButton>(R.id.btnEndCall)
        val btnMute = view.findViewById<LinearLayout>(R.id.btnMute)
        val btnSpeaker = view.findViewById<LinearLayout>(R.id.btnSpeaker)
        val ivMute = view.findViewById<ImageView>(R.id.ivMute)
        val ivSpeaker = view.findViewById<ImageView>(R.id.ivSpeaker)

        startTimer()

        btnEndCall.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        btnMute.setOnClickListener {
            isMuted = !isMuted
            toggleButtonState(ivMute, isMuted)
        }

        btnSpeaker.setOnClickListener {
            isSpeakerOn = !isSpeakerOn
            toggleButtonState(ivSpeaker, isSpeakerOn)
        }
    }

    private fun toggleButtonState(imageView: ImageView, isActive: Boolean) {
        if (isActive) {
            imageView.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.primary_500))
            imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white))
        } else {
            imageView.backgroundTintList = ColorStateList.valueOf(0xFFF3F4F6.toInt()) // Light Grey
            imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.neutral_text_primary))
        }
    }

    private fun startTimer() {
        handler.post(object : Runnable {
            override fun run() {
                if (!isRunning) return

                val minutes = seconds / 60
                val secs = seconds % 60
                tvTimer.text = String.format("%02d:%02d", minutes, secs)

                seconds++
                handler.postDelayed(this, 1000)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        (activity as? HomeActivity)?.setBottomNavVisibility(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isRunning = false
        handler.removeCallbacksAndMessages(null)
        (activity as? HomeActivity)?.setBottomNavVisibility(true)
    }
}