package com.netown.semuabisa

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.netown.semuabisa.features.history.HistoryRecentFragment
import com.netown.semuabisa.features.home.HomeFragment

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        // Initialize with Home Fragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            updateBottomNav(isHome = true)
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navHistory = findViewById<LinearLayout>(R.id.navHistory)
        val navQfd = findViewById<LinearLayout>(R.id.navQfd)
        val navMessage = findViewById<LinearLayout>(R.id.navMessage)
        val navProfile = findViewById<LinearLayout>(R.id.navProfile)

        navHome.setOnClickListener {
            loadFragment(HomeFragment())
            updateBottomNav(isHome = true)
        }

        navHistory.setOnClickListener {
            loadFragment(HistoryRecentFragment())
            updateBottomNav(isHome = false)
        }

        navMessage.setOnClickListener {
            loadFragment(HistoryRecentFragment())
        }

        navProfile.setOnClickListener {
            loadFragment(HistoryRecentFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun updateBottomNav(isHome: Boolean) {
        val ivHome = findViewById<ImageView>(R.id.ivNavHome)
        val tvHome = findViewById<TextView>(R.id.tvNavHome)
        val ivHistory = findViewById<ImageView>(R.id.ivNavHistory)
        val tvHistory = findViewById<TextView>(R.id.tvNavHistory)

        val activeColor = ContextCompat.getColor(this, R.color.primary_500)
        val inactiveColor = ContextCompat.getColor(this, R.color.neutral_text_disabled)

        if (isHome) {
            ivHome.setColorFilter(activeColor)
            tvHome.setTextColor(activeColor)

            ivHistory.setColorFilter(inactiveColor)
            tvHistory.setTextColor(inactiveColor)
        } else {
            ivHome.setColorFilter(inactiveColor)
            tvHome.setTextColor(inactiveColor)

            ivHistory.setColorFilter(activeColor)
            tvHistory.setTextColor(activeColor)
        }
    }
}