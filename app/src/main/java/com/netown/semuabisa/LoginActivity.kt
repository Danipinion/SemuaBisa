package com.netown.semuabisa

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

        btnLogin.setOnClickListener {
            val username = inputUsername.editText?.text.toString()
            if (username.isNotEmpty()) {
                Toast.makeText(this, "Login clicked for $username", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
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