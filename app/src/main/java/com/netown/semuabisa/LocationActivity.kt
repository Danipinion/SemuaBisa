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
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Locale
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Color
import android.widget.RadioButton
import android.widget.TextView


class LocationActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private lateinit var etPickup: EditText
    private lateinit var etDropoff: AutoCompleteTextView
    private lateinit var btnOrder: MaterialButton
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var locationSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var driverSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var confirmationSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var paymentSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var successSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var trackingSheetBehavior: BottomSheetBehavior<LinearLayout>
    private var selectedDriver: Driver? = null
    private lateinit var rbCash: RadioButton

    private var startPoint: GeoPoint? = null
    private var endPoint: GeoPoint? = null
    private var startMarker: Marker? = null
    private var endMarker: Marker? = null
    private var cityBoundingBox: BoundingBox? = null
    private var routePolyline: Polyline? = null
    private var driverMarker: Marker? = null
    private var driverLocation: GeoPoint? = null

    private lateinit var rvDrivers: RecyclerView
    private lateinit var adapterDriver: DriverAdapter
    private val driverList = ArrayList<Driver>()

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
        setupSheets()
        setupMap()
        setupBottomSheet()
        getCurrentLocation()
        setupAutocomplete()
        setupSearchListeners()
        setupDriverList()
        setupConfirmationLogic()
        setupPaymentLogic()
        setupSuccessLogic()
        setupTrackingLogic()
    }

    private fun initViews() {
        map = findViewById(R.id.map)
        rvDrivers = findViewById(R.id.rvDrivers)
        etPickup = findViewById(R.id.etPickup)
        etDropoff = findViewById(R.id.etDropoff) as AutoCompleteTextView
        btnOrder = findViewById(R.id.btnOrder)
        rbCash = findViewById(R.id.rbCash)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<MaterialButton>(R.id.btnOrder).setOnClickListener {
            if (etDropoff.text.isNotEmpty()) {
                searchLocation(etDropoff.text.toString())
            }
        }
    }

    private fun setupSheets() {
        val locSheet = findViewById<LinearLayout>(R.id.locationBottomSheet)
        locationSheetBehavior = BottomSheetBehavior.from(locSheet)
        locationSheetBehavior.peekHeight = (90 * resources.displayMetrics.density).toInt()

        val drvSheet = findViewById<LinearLayout>(R.id.driverBottomSheet)
        driverSheetBehavior = BottomSheetBehavior.from(drvSheet)
        driverSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        val confSheet = findViewById<LinearLayout>(R.id.confirmationBottomSheet)
        confirmationSheetBehavior = BottomSheetBehavior.from(confSheet)
        confirmationSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        val paySheet = findViewById<LinearLayout>(R.id.paymentBottomSheet)
        paymentSheetBehavior = BottomSheetBehavior.from(paySheet)
        paymentSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        val successSheet = findViewById<LinearLayout>(R.id.successBottomSheet)
        successSheetBehavior = BottomSheetBehavior.from(successSheet)
        successSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        val trackSheet = findViewById<LinearLayout>(R.id.trackingBottomSheet)
        trackingSheetBehavior = BottomSheetBehavior.from(trackSheet)
        trackingSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun setupDriverList() {
        driverList.add(Driver("Gustavo Franci", 4.5, "Rp. 15.000", "5 min", 1, "Bike"))
        driverList.add(Driver("Globallyaass", 4.7, "Rp. 18.000", "7 min", 1, "Bike"))
        driverList.add(Driver("Madiun Speed", 4.9, "Rp. 14.000", "3 min", 1, "Bike"))

        adapterDriver = DriverAdapter(driverList) { driver ->
            selectedDriver = driver
        }

        rvDrivers.layoutManager = LinearLayoutManager(this)
        rvDrivers.adapter = adapterDriver

        findViewById<MaterialButton>(R.id.btnSelectDriver).setOnClickListener {
            if (selectedDriver != null) {
                showConfirmationSheet(selectedDriver!!)
            } else {
                Toast.makeText(this, "Pilih driver terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showConfirmationSheet(driver: Driver) {
        findViewById<TextView>(R.id.tvConfDriverName).text = driver.name
        findViewById<TextView>(R.id.tvConfRating).text = "⭐ ${driver.rating}"
        findViewById<TextView>(R.id.tvConfPrice).text = driver.price
        findViewById<TextView>(R.id.tvConfTime).text = driver.time
        findViewById<TextView>(R.id.tvConfSeats).text = "${driver.seats}"

        driverSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        confirmationSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun setupConfirmationLogic() {
        findViewById<ImageButton>(R.id.btnCloseConfirmation).setOnClickListener {
            confirmationSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            driverSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        findViewById<MaterialButton>(R.id.btnOrderNow).setOnClickListener {
            if (selectedDriver == null) {
                Toast.makeText(this, "Kesalahan: Driver tidak terpilih.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            confirmationSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            paymentSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            rbCash.isChecked = true
        }
    }

    private fun setupPaymentLogic() {
        findViewById<ImageButton>(R.id.btnClosePayment).setOnClickListener {
            paymentSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            confirmationSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        findViewById<MaterialButton>(R.id.btnContinuePayment).setOnClickListener {
            val rbCash = findViewById<RadioButton>(R.id.rbCash)

            if (rbCash.isChecked) {
                paymentSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                successSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                Toast.makeText(this, "Hanya metode Cash yang tersedia saat ini", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSuccessLogic() {
        findViewById<MaterialButton>(R.id.btnTrackDriver).setOnClickListener {
            successSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            startLiveTracking()
        }
    }

    private fun startLiveTracking() {
        if (startPoint == null) return

        // 1. Bersihkan Peta (Rute lama & marker lama hapus)
        map.overlays.clear()

        // 2. Pasang Marker User (Posisi Jemput)
        addPickupMarker(startPoint!!)

        // 3. Simulasi Posisi Driver (Misal: geser dikit dari posisi user)
        // Kita buat driver seolah-olah ada di koordinat +0.005 derajat dari user
        driverLocation = GeoPoint(startPoint!!.latitude + 0.005, startPoint!!.longitude + 0.005)

        // 4. Pasang Marker Driver
        addDriverMarker(driverLocation!!)

        // 5. Gambar Rute (Driver -> User)
        // Kita set endpoint sementara ke posisi driver untuk drawRoute
        val tempStart = startPoint
        startPoint = driverLocation // Start rute dari driver
        endPoint = tempStart        // End rute ke user

        drawRoute() // Gambar garis

        // Kembalikan variabel startPoint ke posisi user agar konsisten
        startPoint = tempStart

        // 6. Zoom agar kelihatan dua-duanya
        zoomToFitTracking(driverLocation!!, startPoint!!)

        // 7. Buka Sheet Tracking
        trackingSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        // Update Data UI Tracking (Isi dengan data selectedDriver)
        if (selectedDriver != null) {
            findViewById<TextView>(R.id.tvTrackDriverName).text = selectedDriver!!.name
            findViewById<TextView>(R.id.tvTrackRating).text = "⭐ ${selectedDriver!!.rating}"
            findViewById<TextView>(R.id.tvTrackPrice).text = selectedDriver!!.price
            findViewById<TextView>(R.id.tvTrackSeats).text = "${selectedDriver!!.seats} Seats"
        }
    }

    private fun addDriverMarker(point: GeoPoint) {
        // Buat Marker khusus Driver
        driverMarker = Marker(map)
        driverMarker?.position = point
        driverMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        // Ganti icon ini dengan foto/icon mobil jika ada
        driverMarker?.icon = ContextCompat.getDrawable(this, R.drawable.ic_launcher_foreground)
        driverMarker?.title = "Driver sedang menuju lokasi"

        map.overlays.add(driverMarker)
        map.invalidate()
    }

    private fun zoomToFitTracking(driver: GeoPoint, user: GeoPoint) {
        val boundingBox = BoundingBox(
            maxOf(driver.latitude, user.latitude),
            maxOf(driver.longitude, user.longitude),
            minOf(driver.latitude, user.latitude),
            minOf(driver.longitude, user.longitude)
        )
        map.zoomToBoundingBox(boundingBox, true, 150) // Padding lebih besar biar lega
    }

    private fun setupTrackingLogic() {
        // Tombol Cancel
        findViewById<MaterialButton>(R.id.btnCancelTrip).setOnClickListener {
            // Logika Cancel: Reset semua ke awal
            trackingSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            map.overlays.clear()
            map.invalidate()

            // Buka lagi input lokasi (Reset Flow)
            locationSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            Toast.makeText(this, "Perjalanan dibatalkan", Toast.LENGTH_SHORT).show()
        }

        // Tombol Chat & Call (Dummy)
        findViewById<ImageButton>(R.id.btnChat).setOnClickListener {
            Toast.makeText(this, "Membuka Chat...", Toast.LENGTH_SHORT).show()
        }
        findViewById<ImageButton>(R.id.btnCall).setOnClickListener {
            Toast.makeText(this, "Menelpon Driver...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupAutocomplete() {
        suggestionAdapter = ArrayAdapter(this, R.layout.item_suggestion, ArrayList())
        etDropoff.setAdapter(suggestionAdapter)

        etDropoff.setOnItemClickListener { parent, _, position, _ ->
            val selectedPlace = parent.getItemAtPosition(position) as PlaceSuggestion

            hideKeyboard()
            etDropoff.setText(selectedPlace.displayName)
            etDropoff.dismissDropDown()

            endPoint = GeoPoint(selectedPlace.lat, selectedPlace.lon)
            processSelectedLocation()
        }

        etDropoff.addTextChangedListener(object : TextWatcher {
            private var searchJob: Job? = null

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()
            }

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.length < 3) return

                searchJob = CoroutineScope(Dispatchers.IO).launch {
                    delay(400)
                    fetchSuggestions(query)
                }
            }
        })
    }

    private suspend fun fetchSuggestions(query: String) {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        var urlString =
            "https://nominatim.openstreetmap.org/search?q=$encodedQuery&format=json&limit=5&addressdetails=1"

        if (cityBoundingBox != null) {
            val viewBoxStr =
                "${cityBoundingBox!!.lonWest},${cityBoundingBox!!.latNorth},${cityBoundingBox!!.lonEast},${cityBoundingBox!!.latSouth}"
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
                    val name = obj.getString("display_name")
                    val lat = obj.getDouble("lat")
                    val lon = obj.getDouble("lon")

                    newSuggestions.add(PlaceSuggestion(name, lat, lon))
                }

                withContext(Dispatchers.Main) {
                    suggestionAdapter.clear()
                    suggestionAdapter.addAll(newSuggestions)
                    suggestionAdapter.notifyDataSetChanged()

                    etDropoff.showDropDown()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun processSelectedLocation() {
        if (endPoint != null) {
            drawRoute()
            addDropoffMarker(endPoint!!)
            zoomToFitMarkers()

            locationSheetBehavior.isHideable = true
            locationSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            driverSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            showDummyDriversOnMap()
        }
    }

    private fun showDummyDriversOnMap() {
        if (startPoint != null) {
            val driver1Loc = GeoPoint(startPoint!!.latitude + 0.001, startPoint!!.longitude + 0.001)
            val driverMarker = Marker(map)
            driverMarker.position = driver1Loc
            driverMarker.icon = ContextCompat.getDrawable(this, R.drawable.ic_motorpin)
            driverMarker.title = "Gustavo"
            map.overlays.add(driverMarker)
            map.invalidate()
        }
    }

    private fun setupMap() {
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.setZoom(18.0)
    }

    private fun setupBottomSheet() {
        val bottomSheetLayout = findViewById<LinearLayout>(R.id.locationBottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        bottomSheetBehavior.peekHeight = (90 * resources.displayMetrics.density).toInt()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
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
        cityBoundingBox = BoundingBox(
            center.latitude + radius,
            center.longitude + radius,
            center.latitude - radius,
            center.longitude - radius
        )
        map.setScrollableAreaLimitDouble(cityBoundingBox)
        map.setMinZoomLevel(13.0)
        map.invalidate()
    }

    private fun getAddressFromLocation(point: GeoPoint) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val geocoder = Geocoder(this@LocationActivity, Locale.getDefault())
                val addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addressText =
                        addresses[0].thoroughfare ?: addresses[0].subLocality ?: "Lokasi Saya"
                    withContext(Dispatchers.Main) { etPickup.setText(addressText) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@LocationActivity,
                            "Lokasi tidak ditemukan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupSearchListeners() {
        etDropoff.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                val query = etDropoff.text.toString()
                if (query.isNotEmpty()) {
                    searchLocation(query); hideKeyboard()
                }
                true
            } else false
        }
        btnOrder.setOnClickListener {
            val query = etDropoff.text.toString()
            if (query.isNotEmpty()) {
                searchLocation(query); hideKeyboard()
            } else etDropoff.error = "Masukkan tujuan dulu"
        }
    }

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
            startMarker?.icon = ContextCompat.getDrawable(this, R.drawable.avatar)
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
            endMarker?.icon = ContextCompat.getDrawable(this, R.drawable.ic_location)
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
                val routeUrl =
                    "https://router.project-osrm.org/route/v1/driving/${startPoint!!.longitude},${startPoint!!.latitude};${endPoint!!.longitude},${endPoint!!.latitude}?overview=full&geometries=polyline"
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
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun decodePolyline(encoded: String): ArrayList<GeoPoint> {
        val poly = ArrayList<GeoPoint>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int;
            var shift = 0;
            var result = 0
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