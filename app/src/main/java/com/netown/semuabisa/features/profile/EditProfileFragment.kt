package com.netown.semuabisa.features.profile

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.netown.semuabisa.HomeActivity
import com.netown.semuabisa.R

class EditProfileFragment : Fragment() {

    private lateinit var etFullname: EditText
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etAddress: EditText
    private lateinit var imgProfile: ImageView

    private lateinit var sharedPreferences: SharedPreferences
    private var imageUri: String? = null

    // Image Picker Launcher
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imgProfile.setImageURI(it)
            imageUri = it.toString() // Store URI as string
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Views
        etFullname = view.findViewById(R.id.etFullname)
        etUsername = view.findViewById(R.id.etUsername)
        etEmail = view.findViewById(R.id.etEmail)
        etPhone = view.findViewById(R.id.etPhone)
        etAddress = view.findViewById(R.id.etAddress)
        imgProfile = view.findViewById(R.id.imgProfile)

        // Initialize Shared Preferences
        sharedPreferences = requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE)

        // Load Saved Data
        loadProfileData()

        // Handle Profile Image Click -> Open Gallery
        imgProfile.setOnClickListener {
            pickImage.launch("image/*")
        }
        // Also allow clicking the container layout if desired
        view.findViewById<View>(R.id.imgProfile).parent.let { parent ->
            (parent as View).setOnClickListener { pickImage.launch("image/*") }
        }

        // Handle Back Button
        view.findViewById<View>(R.id.btnBack).setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Handle Save Button
        view.findViewById<View>(R.id.btnSave).setOnClickListener {
            saveProfileData()
        }
    }

    private fun loadProfileData() {
        val name = sharedPreferences.getString("fullname", "Mohammad Dani T")
        val username = sharedPreferences.getString("username", "@Danipinions")
        val email = sharedPreferences.getString("email", "Danipirions@gmail.com")
        val phone = sharedPreferences.getString("phone", "0895580475151")
        val address = sharedPreferences.getString("address", "Madiun, East Java")
        val imgUriString = sharedPreferences.getString("image_uri", null)

        etFullname.setText(name)
        etUsername.setText(username)
        etEmail.setText(email)
        etPhone.setText(phone)
        etAddress.setText(address)

        if (imgUriString != null) {
            try {
                imgProfile.setImageURI(Uri.parse(imgUriString))
                imageUri = imgUriString
            } catch (e: Exception) {
                // If image cannot be loaded (e.g., deleted), use default
                imgProfile.setImageResource(R.drawable.avatar_profile)
            }
        }
    }

    private fun saveProfileData() {
        val editor = sharedPreferences.edit()

        editor.putString("fullname", etFullname.text.toString())
        editor.putString("username", etUsername.text.toString())
        editor.putString("email", etEmail.text.toString())
        editor.putString("phone", etPhone.text.toString())
        editor.putString("address", etAddress.text.toString())

        if (imageUri != null) {
            editor.putString("image_uri", imageUri)
        }

        editor.apply() // Asynchronous save

        Toast.makeText(context, "Profile Saved Successfully!", Toast.LENGTH_SHORT).show()
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        // Hide Bottom Nav
        (activity as? HomeActivity)?.setBottomNavVisibility(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Show Bottom Nav
        (activity as? HomeActivity)?.setBottomNavVisibility(true)
    }
}