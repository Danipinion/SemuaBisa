package com.netown.semuabisa.features.history

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.netown.semuabisa.R

class HistoryRecentFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: HistoryAdapter

    // Tabs
    private lateinit var tabSchedule: TextView
    private lateinit var tabRecent: TextView
    private lateinit var tabCompleted: TextView
    private lateinit var tabCanceled: TextView

    // Colors
    private val activeColor = Color.parseColor("#0066FF") // Blue
    private val inactiveColor = Color.parseColor("#999999") // Gray

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history_recent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Views
        recycler = view.findViewById(R.id.recyclerHistory)
        tabSchedule = view.findViewById(R.id.tabSchedule)
        tabRecent = view.findViewById(R.id.tabRecent)
        tabCompleted = view.findViewById(R.id.tabCompleted)
        tabCanceled = view.findViewById(R.id.tabCanceled)

        // Setup Recycler
        recycler.layoutManager = LinearLayoutManager(requireContext())
        // Disable nested scrolling to ensure smooth scrolling inside NestedScrollView
        recycler.isNestedScrollingEnabled = false

        // Setup Tab Click Listeners
        setupTabs()

        // Load Default Tab (Recent)
        selectTab(tabRecent)
        loadData("Recent")
    }

    private fun setupTabs() {
        tabSchedule.setOnClickListener {
            selectTab(tabSchedule)
            loadData("Schedule")
        }
        tabRecent.setOnClickListener {
            selectTab(tabRecent)
            loadData("Recent")
        }
        tabCompleted.setOnClickListener {
            selectTab(tabCompleted)
            loadData("Completed")
        }
        tabCanceled.setOnClickListener {
            selectTab(tabCanceled)
            loadData("Canceled")
        }
    }

    private fun selectTab(selected: TextView) {
        val tabs = listOf(tabSchedule, tabRecent, tabCompleted, tabCanceled)

        tabs.forEach { tab ->
            if (tab == selected) {
                tab.setTextColor(activeColor)
                tab.setTypeface(null, Typeface.BOLD)
            } else {
                tab.setTextColor(inactiveColor)
                tab.setTypeface(null, Typeface.NORMAL)
            }
        }
    }

    private fun loadData(type: String) {
        val data = when (type) {
            "Schedule" -> getScheduleData()
            "Recent" -> getRecentData()
            "Completed" -> getCompletedData()
            "Canceled" -> getCanceledData()
            else -> emptyList()
        }

        adapter = HistoryAdapter(data)
        recycler.adapter = adapter
    }

    // --- DUMMY DATA GENERATORS ---

    // Schedule: All excluded Motor/Car (e.g., Train, Bus, Plane)
    private fun getScheduleData(): List<HistoryModel> {
        return listOf(
            HistoryModel(
                fromAddress = "Stasiun Gambir",
                toAddress = "Stasiun Madiun",
                dateTime = "30 Dec 2025, 08:00",
                type = "Train",
                driver = "KAI Argo Wilis",
                seats = "Seat 4A",
                paymentStatus = "Paid"
            ),
            HistoryModel(
                fromAddress = "Terminal Bungurasih",
                toAddress = "Terminal Purboyo",
                dateTime = "31 Dec 2025, 14:00",
                type = "Bus",
                driver = "Sugeng Rahayu",
                seats = "Seat 12",
                paymentStatus = "Paid"
            )
        )
    }

    private fun getRecentData(): List<HistoryModel> {
        return listOf(
            HistoryModel(
                fromAddress = "Jl. Sudirman No 4",
                toAddress = "Politeknik Negeri Madiun",
                dateTime = "27 Dec 2025, 13:12",
                type = "Motor",
                driver = "Gustave Vanel",
                seats = "1",
                paymentStatus = "Pending"
            ),
            HistoryModel(
                fromAddress = "Pasar Besar",
                toAddress = "Aston Hotel",
                dateTime = "26 Dec 2025, 09:00",
                type = "Car",
                driver = "Budi Santoso",
                seats = "4",
                paymentStatus = "Ongoing"
            ),
            HistoryModel(
                fromAddress = "Stasiun Madiun",
                toAddress = "Rumah",
                dateTime = "25 Dec 2025, 18:30",
                type = "Motor",
                driver = "Agus",
                seats = "1",
                paymentStatus = "Ongoing"
            ),
            HistoryModel(
                fromAddress = "Bandara Juanda (SUB)",
                toAddress = "Bandara Soekarno-Hatta (CGK)",
                dateTime = "28 Dec 2025, 06:45",
                type = "Plane",
                driver = "Garuda Indonesia GA-305",
                seats = "1",
                paymentStatus = "Ongoing"
            ),
            HistoryModel(
                fromAddress = "Terminal Purboyo Madiun",
                toAddress = "Terminal Bungurasih SBY",
                dateTime = "29 Dec 2025, 15:30",
                type = "Bus",
                driver = "Sugeng Rahayu",
                seats = "1",
                paymentStatus = "Pending"
            ),
        )
    }

    // Completed: Only completed trips
    private fun getCompletedData(): List<HistoryModel> {
        return listOf(
            HistoryModel(
                fromAddress = "Madiun Plaza",
                toAddress = "Suncity Mall",
                dateTime = "20 Dec 2025, 10:00",
                type = "Car",
                driver = "Siti Aminah",
                seats = "4",
                paymentStatus = "Completed"
            ),
            HistoryModel(
                fromAddress = "Kampus 1",
                toAddress = "Kampus 2",
                dateTime = "19 Dec 2025, 07:45",
                type = "Motor",
                driver = "Rudi",
                seats = "1",
                paymentStatus = "Completed"
            )
        )
    }

    // Canceled: Only canceled trips
    private fun getCanceledData(): List<HistoryModel> {
        return listOf(
            HistoryModel(
                fromAddress = "Rumah",
                toAddress = "Bandara",
                dateTime = "15 Dec 2025, 05:00",
                type = "Car",
                driver = "-",
                seats = "-",
                paymentStatus = "Canceled"
            )
        )
    }
}