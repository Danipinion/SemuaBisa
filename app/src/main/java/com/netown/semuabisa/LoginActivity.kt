package com.netown.semuabisa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val inputUsername = findViewById<TextInputLayout>(R.id.tilUsername)
        val tvRegisterLink = findViewById<TextView>(R.id.tvSignUp)

        // 1. Get SharedPreferences
        val sharedPreferences = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)

        btnLogin.setOnClickListener {
            val username = inputUsername.editText?.text.toString()
            if (username.isNotEmpty()) {

                // 2. Save Username from Login to SharedPreferences
                val editor = sharedPreferences.edit()
                editor.putString("username", "@$username")
                editor.apply()

                Toast.makeText(this, "Welcome back, $username!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish() // Close login so user can't back to it
            } else {
                inputUsername.error = "Username tidak boleh kosong"
            }
        }

        tvRegisterLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}