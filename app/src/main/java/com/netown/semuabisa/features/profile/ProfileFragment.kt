package com.netown.semuabisa.features.profile

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.netown.semuabisa.HomeActivity
import com.netown.semuabisa.LoginActivity
import com.netown.semuabisa.R

class ProfileFragment : Fragment() {

    private lateinit var txtName: TextView
    private lateinit var txtUsername: TextView
    private lateinit var imgProfile: ImageView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        txtName = view.findViewById(R.id.txtName)
        txtUsername = view.findViewById(R.id.txtUsername)
        imgProfile = view.findViewById(R.id.imgProfile)

        val btnEditProfile = view.findViewById<Button>(R.id.btnEditProfile)

        val btnClearCache = view.findViewById<LinearLayout>(R.id.btnClearCache)

        val btnLogout = view.findViewById<LinearLayout>(R.id.btnLogout)

        sharedPreferences =
            requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE)

        initDefaultProfileData()

        loadProfileData()

        btnEditProfile.setOnClickListener {
            (activity as? HomeActivity)?.loadFragment(EditProfileFragment())
        }


        btnClearCache.setOnClickListener {
            showDynamicDialog(
                title = "Clear Cache?",
                message = "Are you sure you want to clear the application cache? This might free up space.",
                iconRes = R.drawable.cuida_trash_outline
            ) {
                Toast.makeText(requireContext(), "Cache Cleared!", Toast.LENGTH_SHORT).show()
            }
        }

        btnLogout.setOnClickListener {
            showDynamicDialog(
                title = "Logout?",
                message = "Are you sure you want to logout from your account?",
                iconRes = R.drawable.circum_logout
            ) {
                val editor = sharedPreferences.edit()
                editor.clear()
                editor.apply()

                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        loadProfileData()
        (activity as? HomeActivity)?.setBottomNavVisibility(true)
    }

    private fun initDefaultProfileData() {
        if (!sharedPreferences.contains("initialized")) {
            val editor = sharedPreferences.edit()
            editor.putString("fullname", "Unknown")
            if (!sharedPreferences.contains("username")) {
                editor.putString("username", "@UnknownUser")
            }
            editor.putString("email", "unknown@uk.com")
            editor.putString("phone", "-")
            editor.putString("address", "-")
            editor.putBoolean("initialized", true)
            editor.apply()
        }
    }

    private fun loadProfileData() {
        val name = sharedPreferences.getString("fullname", "Unknown")
        val username = sharedPreferences.getString("username", "@UnknownUser")
        val imageUriString = sharedPreferences.getString("image_uri", null)

        txtName.text = name
        txtUsername.text = username

        if (imageUriString != null) {
            try {
                imgProfile.setImageURI(Uri.parse(imageUriString))
            } catch (e: Exception) {
                imgProfile.setImageResource(R.drawable.avatar_profile)
            }
        } else {
            imgProfile.setImageResource(R.drawable.avatar_profile)
        }
    }

    private fun showDynamicDialog(
        title: String,
        message: String,
        iconRes: Int,
        onConfirm: () -> Unit
    ) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_confirmation)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val tvTitle = dialog.findViewById<TextView>(R.id.tvDialogTitle)
        val tvMessage = dialog.findViewById<TextView>(R.id.tvDialogMessage)
        val ivIcon = dialog.findViewById<ImageView>(R.id.ivDialogIcon)
        val btnNo = dialog.findViewById<MaterialButton>(R.id.btnDialogNo)
        val btnYes = dialog.findViewById<MaterialButton>(R.id.btnDialogYes)

        tvTitle.text = title
        tvMessage.text = message
        ivIcon.setImageResource(iconRes)

        btnNo.setOnClickListener { dialog.dismiss() }
        btnYes.setOnClickListener {
            dialog.dismiss()
            onConfirm()
        }

        dialog.show()
    }
}