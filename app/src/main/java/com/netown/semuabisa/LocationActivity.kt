package com.netown.semuabisa

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory

import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.Locale
import org.osmdroid.views.overlay.Polyline
import java.net.HttpURLConnection
import java.net.URL
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import kotlinx.coroutines.*
import org.json.JSONArray
import java.net.URLEncoder


class LocationActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private lateinit var etPickup: EditText
    private lateinit var etDropoff: AutoCompleteTextView
    private lateinit var btnOrder: MaterialButton
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private var startPoint: GeoPoint? = null
    private var endPoint: GeoPoint? = null
    private var startMarker: Marker? = null
    private var endMarker: Marker? = null
    private var cityBoundingBox: BoundingBox? = null
    private var routePolyline: Polyline? = null

    data class PlaceSuggestion(val displayName: String, val lat: Double, val lon: Double) {
        override fun toString(): String {
            return displayName
        }
    }

    private lateinit var suggestionAdapter: ArrayAdapter<PlaceSuggestion>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().userAgentValue = "SemuaBisaApp/1.0"
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        setContentView(R.layout.activity_location)
        initViews()
        setupMap()
        setupBottomSheet()
        getCurrentLocation()
        setupAutocomplete() // Setup baru
        setupSearchListeners() // Untuk tombol keyboard enter
    }

    private fun initViews() {
        map = findViewById(R.id.map)
        etPickup = findViewById(R.id.etPickup)
        etDropoff = findViewById(R.id.etDropoff) as AutoCompleteTextView
        btnOrder = findViewById(R.id.btnOrder)
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
    }

    private fun setupAutocomplete() {
        // Gunakan layout custom 'item_suggestion' agar teks terlihat jelas
        suggestionAdapter = ArrayAdapter(this, R.layout.item_suggestion, ArrayList())
        etDropoff.setAdapter(suggestionAdapter)

        // 1. Logic saat Saran DIKLIK (Solusi Tepat Sasaran)
        etDropoff.setOnItemClickListener { parent, _, position, _ ->
            val selectedPlace = parent.getItemAtPosition(position) as PlaceSuggestion

            // Langsung gunakan koordinat dari saran (TIDAK PERLU SEARCH ULANG)
            hideKeyboard()
            etDropoff.setText(selectedPlace.displayName) // Set teks jadi rapi
            etDropoff.dismissDropDown() // Tutup dropdown

            // Set End Point & Gambar Rute
            endPoint = GeoPoint(selectedPlace.lat, selectedPlace.lon)
            processSelectedLocation() // Fungsi baru untuk handle UI map
        }

        // 2. Logic saat Mengetik
        etDropoff.addTextChangedListener(object : TextWatcher {
            private var searchJob: Job? = null

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()
                // Jangan hapus adapter disini agar tidak kedip
            }

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.length < 3) return

                searchJob = CoroutineScope(Dispatchers.IO).launch {
                    delay(400) // Debounce sedikit lebih lama agar hemat request
                    fetchSuggestions(query)
                }
            }
        })
    }

    private suspend fun fetchSuggestions(query: String) {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        var urlString = "https://nominatim.openstreetmap.org/search?q=$encodedQuery&format=json&limit=5&addressdetails=1"

        if (cityBoundingBox != null) {
            val viewBoxStr = "${cityBoundingBox!!.lonWest},${cityBoundingBox!!.latNorth},${cityBoundingBox!!.lonEast},${cityBoundingBox!!.latSouth}"
            urlString += "&viewbox=$viewBoxStr&bounded=1"
        }

        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("User-Agent", "SemuaBisaApp/1.0")
            connection.requestMethod = "GET"

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                if (response.trim().isEmpty() || response == "[]") return

                val jsonArray = JSONArray(response)
                val newSuggestions = ArrayList<PlaceSuggestion>()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    // Ambil data lengkap
                    val name = obj.getString("display_name")
                    val lat = obj.getDouble("lat")
                    val lon = obj.getDouble("lon")

                    newSuggestions.add(PlaceSuggestion(name, lat, lon))
                }

                withContext(Dispatchers.Main) {
                    // Update adapter dengan data baru
                    suggestionAdapter.clear()
                    suggestionAdapter.addAll(newSuggestions)
                    suggestionAdapter.notifyDataSetChanged()

                    // Paksa Dropdown Muncul (Trik Filter)
                    // Kita gunakan filter kosong agar semua data tampil
                    etDropoff.showDropDown()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun processSelectedLocation() {
        if (endPoint != null) {
            // 1. Gambar Rute
            drawRoute()
            // 2. Pasang Marker
            addDropoffMarker(endPoint!!)
            // 3. Zoom Fit
            zoomToFitMarkers()
            // 4. Minimize BottomSheet
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    // --- Setup Map & Location ---
    private fun setupMap() {
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.setZoom(18.0)
    }

    private fun setupBottomSheet() {
        val bottomSheetLayout = findViewById<LinearLayout>(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        bottomSheetBehavior.peekHeight = (90 * resources.displayMetrics.density).toInt()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                startPoint = GeoPoint(location.latitude, location.longitude)
                map.controller.animateTo(startPoint)
                addPickupMarker(startPoint!!)
                getAddressFromLocation(startPoint!!)
                restrictMapToCity(startPoint!!)
            }
        }
    }

    private fun restrictMapToCity(center: GeoPoint) {
        val radius = 0.2
        cityBoundingBox = BoundingBox(center.latitude + radius, center.longitude + radius, center.latitude - radius, center.longitude - radius)
        map.setScrollableAreaLimitDouble(cityBoundingBox)
        map.setMinZoomLevel(13.0)
        map.invalidate()
    }

    // --- Helpers Map & Geocoding ---
    private fun getAddressFromLocation(point: GeoPoint) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val geocoder = Geocoder(this@LocationActivity, Locale.getDefault())
                val addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addressText = addresses[0].thoroughfare ?: addresses[0].subLocality ?: "Lokasi Saya"
                    withContext(Dispatchers.Main) { etPickup.setText(addressText) }
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    private fun searchLocation(query: String) {
        Toast.makeText(this, "Mencari...", Toast.LENGTH_SHORT).show()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val geocoder = Geocoder(this@LocationActivity, Locale.getDefault())
                val addresses = geocoder.getFromLocationName(query, 1)
                if (!addresses.isNullOrEmpty()) {
                    val location = addresses[0]
                    endPoint = GeoPoint(location.latitude, location.longitude)
                    withContext(Dispatchers.Main) { processSelectedLocation() }
                } else {
                    withContext(Dispatchers.Main) { Toast.makeText(this@LocationActivity, "Lokasi tidak ditemukan", Toast.LENGTH_SHORT).show() }
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    private fun setupSearchListeners() {
        etDropoff.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                val query = etDropoff.text.toString()
                if (query.isNotEmpty()) { searchLocation(query); hideKeyboard() }
                true
            } else false
        }
        btnOrder.setOnClickListener {
            val query = etDropoff.text.toString()
            if (query.isNotEmpty()) { searchLocation(query); hideKeyboard() }
            else etDropoff.error = "Masukkan tujuan dulu"
        }
    }

    // --- Helpers Drawing ---
    private fun clearRoute() {
        if (routePolyline != null) {
            map.overlays.remove(routePolyline)
            routePolyline = null
            map.invalidate()
        }
    }

    private fun addPickupMarker(point: GeoPoint) {
        if (startMarker == null) {
            startMarker = Marker(map)
            startMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            startMarker?.setInfoWindow(null)
            startMarker?.icon = ContextCompat.getDrawable(this, R.drawable.ic_launcher_foreground)
            map.overlays.add(startMarker)
        }
        startMarker?.position = point
        map.invalidate()
    }

    private fun addDropoffMarker(point: GeoPoint) {
        if (endMarker == null) {
            endMarker = Marker(map)
            endMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            endMarker?.setInfoWindow(null)
            endMarker?.icon = ContextCompat.getDrawable(this, R.drawable.ic_launcher_foreground)
            map.overlays.add(endMarker)
        }
        endMarker?.position = point
        map.invalidate()
    }

    private fun drawRoute() {
        clearRoute()
        if (startPoint == null || endPoint == null) return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val routeUrl = "https://router.project-osrm.org/route/v1/driving/${startPoint!!.longitude},${startPoint!!.latitude};${endPoint!!.longitude},${endPoint!!.latitude}?overview=full&geometries=polyline"
                val url = URL(routeUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(response)
                    val routes = jsonResponse.getJSONArray("routes")
                    if (routes.length() > 0) {
                        val polylineString = routes.getJSONObject(0).getString("geometry")
                        val routePoints = decodePolyline(polylineString)
                        withContext(Dispatchers.Main) {
                            routePolyline = Polyline(map)
                            routePolyline?.setPoints(routePoints)
                            routePolyline?.outlinePaint?.color = android.graphics.Color.BLUE
                            routePolyline?.outlinePaint?.strokeWidth = 15f
                            map.overlays.add(routePolyline)
                            map.invalidate()
                        }
                    }
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    private fun decodePolyline(encoded: String): ArrayList<GeoPoint> {
        val poly = ArrayList<GeoPoint>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int; var shift = 0; var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0; result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            poly.add(GeoPoint(lat.toDouble() / 1E5, lng.toDouble() / 1E5))
        }
        return poly
    }

    private fun zoomToFitMarkers() {
        if (startPoint != null && endPoint != null) {
            val boundingBox = BoundingBox(
                maxOf(startPoint!!.latitude, endPoint!!.latitude),
                maxOf(startPoint!!.longitude, endPoint!!.longitude),
                minOf(startPoint!!.latitude, endPoint!!.latitude),
                minOf(startPoint!!.longitude, endPoint!!.longitude)
            )
            map.zoomToBoundingBox(boundingBox, true, 100)
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etDropoff.windowToken, 0)
    }
}