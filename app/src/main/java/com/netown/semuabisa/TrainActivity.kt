package com.netown.semuabisa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.netown.semuabisa.features.train.*

class TrainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_train)

        if (savedInstanceState == null) {
            loadFragment(TrainDetailBookingFragment(), false)
        }
    }

    fun loadFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.train_container, fragment)
        if (addToBackStack) transaction.addToBackStack(null)
        transaction.commit()
    }

    fun goBackToHome() {
        finish()
    }
}