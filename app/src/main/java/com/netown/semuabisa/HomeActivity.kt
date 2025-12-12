package com.netown.semuabisa

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.netown.semuabisa.features.history.HistoryRecentFragment
import com.netown.semuabisa.features.home.HomeFragment
import com.netown.semuabisa.features.messages.MessagesFragment
import androidx.core.view.updateLayoutParams
import android.view.ViewGroup.MarginLayoutParams
import android.util.TypedValue
import androidx.fragment.app.FragmentContainerView

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

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            updateBottomNav(0)
        }

        setupBottomNavigation()
    }
    fun setBottomNavVisibility(isVisible: Boolean) {
        val bottomAppBar = findViewById<BottomAppBar>(R.id.bottomAppBar)
        val fragmentContainer = findViewById<FragmentContainerView>(R.id.fragmentContainer)
        fun dpToPx(dp: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resources.displayMetrics
            ).toInt()
        }

        if (isVisible) {
            bottomAppBar.visibility = View.VISIBLE
            bottomAppBar.performShow()

            val marginInPx = dpToPx(70f)
            fragmentContainer.updateLayoutParams<MarginLayoutParams> {
                bottomMargin = marginInPx
            }
        } else {
            bottomAppBar.visibility = View.GONE
            bottomAppBar.performHide()

            fragmentContainer.updateLayoutParams<MarginLayoutParams> {
                bottomMargin = 0
            }
        }
    }

    private fun setupBottomNavigation() {
        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navHistory = findViewById<LinearLayout>(R.id.navHistory)
        val navQfd = findViewById<LinearLayout>(R.id.navQfd)
        val navMessage = findViewById<LinearLayout>(R.id.navMessage)
        val navProfile = findViewById<LinearLayout>(R.id.navProfile)

        navHome.setOnClickListener {
            loadFragment(HomeFragment())
            updateBottomNav(0)
        }

        navHistory.setOnClickListener {
            loadFragment(HistoryRecentFragment())
            updateBottomNav(1)
        }

        navMessage.setOnClickListener {
            loadFragment(MessagesFragment())
            updateBottomNav(3)
        }

        navProfile.setOnClickListener {
            loadFragment(HistoryRecentFragment())
            updateBottomNav(4)
        }
    }

    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun updateBottomNav(index: Int) {
        val ivHome = findViewById<ImageView>(R.id.ivNavHome)
        val tvHome = findViewById<TextView>(R.id.tvNavHome)
        val ivHistory = findViewById<ImageView>(R.id.ivNavHistory)
        val tvHistory = findViewById<TextView>(R.id.tvNavHistory)
        val ivMessage = findViewById<ImageView>(R.id.ivNavMessage)
        val tvMessage = findViewById<TextView>(R.id.tvNavMessage)

        val activeColor = ContextCompat.getColor(this, R.color.primary_500)
        val inactiveColor = ContextCompat.getColor(this, R.color.neutral_text_disabled)


        ivHome.setColorFilter(inactiveColor); tvHome.setTextColor(inactiveColor)
        ivHistory.setColorFilter(inactiveColor); tvHistory.setTextColor(inactiveColor)
        ivMessage?.setColorFilter(inactiveColor); tvMessage?.setTextColor(inactiveColor)

        when (index) {
            0 -> { ivHome.setColorFilter(activeColor); tvHome.setTextColor(activeColor) }
            1 -> { ivHistory.setColorFilter(activeColor); tvHistory.setTextColor(activeColor) }
            // 2 is QFD
            3 -> { ivMessage?.setColorFilter(activeColor); tvMessage?.setTextColor(activeColor) }
            // 4 is Profile
        }
    }
}