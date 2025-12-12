package com.netown.semuabisa.features.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.netown.semuabisa.HomeActivity
import com.netown.semuabisa.R

class NotificationFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle Back Button (Assuming you have an ID btnBack in fragment_notification.xml)
        view.findViewById<View>(R.id.btnBack)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        recyclerView = view.findViewById(R.id.rvNotification)

        val list = listOf(
            NotificationModel(
                "Success",
                "Order Motor .... Success",
                NotificationStatus.SUCCESS
            ),
            NotificationModel(
                "Waiting",
                "Waiting Motor",
                NotificationStatus.WAITING
            ),
            NotificationModel(
                "Canceled",
                "You Canceled Order",
                NotificationStatus.CANCELED
            )
        )

        adapter = NotificationAdapter(list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        // Hide Bottom Bar
        (activity as? HomeActivity)?.setBottomNavVisibility(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Show Bottom Bar
        (activity as? HomeActivity)?.setBottomNavVisibility(true)
    }
}