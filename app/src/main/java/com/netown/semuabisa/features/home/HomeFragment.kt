package com.netown.semuabisa.features.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.netown.semuabisa.HomeActivity
import com.netown.semuabisa.LocationActivity
import com.netown.semuabisa.R
import com.netown.semuabisa.features.notification.NotificationFragment
import com.netown.semuabisa.features.train.TrainActivity

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuMotor = view.findViewById<ImageView>(R.id.menuMotor)
        menuMotor.setOnClickListener {
            val intent = Intent(requireContext(), LocationActivity::class.java)
            intent.putExtra("VEHICLE_TYPE", "Motor")
            startActivity(intent)
        }

        val menuCar = view.findViewById<ImageView>(R.id.menuCar)
        menuCar.setOnClickListener {
            val intent = Intent(requireContext(), LocationActivity::class.java)
            intent.putExtra("VEHICLE_TYPE", "Car")
            startActivity(intent)
        }

        val menuTrain = view.findViewById<ImageView>(R.id.menuTrain)
        menuTrain.setOnClickListener {
            val intent = Intent(requireContext(), TrainActivity::class.java)
            intent.putExtra("VEHICLE_TYPE", "Train")
            startActivity(intent)
        }

        val menuBus = view.findViewById<ImageView>(R.id.menuBus)
        menuBus.setOnClickListener {
            val intent = Intent(requireContext(), TrainActivity::class.java)
            intent.putExtra("VEHICLE_TYPE", "Bus")
            startActivity(intent)
        }

        val menuPlane = view.findViewById<ImageView>(R.id.menuPlane)
        menuPlane.setOnClickListener {
            val intent = Intent(requireContext(), TrainActivity::class.java)
            intent.putExtra("VEHICLE_TYPE", "Plane")
            startActivity(intent)
        }
        val btnNotification = view.findViewById<ImageButton>(R.id.btnNotification)
        btnNotification.setOnClickListener {
            (activity as? HomeActivity)?.loadFragment(NotificationFragment())
        }
    }
}