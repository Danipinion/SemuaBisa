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
import android.widget.RatingBar
import android.widget.TextView
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.view.Window


class LocationActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private lateinit var etPickup: EditText
    private lateinit var etDropoff: AutoCompleteTextView
    private lateinit var btnOrder: MaterialButton

    // Sheets
    private lateinit var locationSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var driverSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var confirmationSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var paymentSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var successSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var trackingSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var arrivedSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var reviewSheetBehavior: BottomSheetBehavior<LinearLayout>

    private val allSheets = ArrayList<BottomSheetBehavior<LinearLayout>>()

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
    private lateinit var suggestionAdapter: ArrayAdapter<PlaceSuggestion>

    data class PlaceSuggestion(val displayName: String, val lat: Double, val lon: Double) {
        override fun toString(): String = displayName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().userAgentValue = "SemuaBisaApp/1.0"
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        setContentView(R.layout.activity_location)
        initViews()
        setupSheets()
        setupMap()
        getCurrentLocation()
        setupAutocomplete()
        setupSearchListeners()
        setupDriverList()
        setupConfirmationLogic()
        setupPaymentLogic()
        setupSuccessLogic()
        setupTrackingLogic()
        setupArrivedLogic()
        setupReviewLogic()
    }

    private fun initViews() {
        map = findViewById(R.id.map)
        rvDrivers = findViewById(R.id.rvDrivers)
        etPickup = findViewById(R.id.etPickup)
        etDropoff = findViewById(R.id.etDropoff) as AutoCompleteTextView
        btnOrder = findViewById(R.id.btnOrder)
        rbCash = findViewById(R.id.rbCash)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
    }

    private fun setupSheets() {
        locationSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.locationBottomSheet))
        driverSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.driverBottomSheet))
        confirmationSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.confirmationBottomSheet))
        paymentSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.paymentBottomSheet))
        successSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.successBottomSheet))
        trackingSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.trackingBottomSheet))
        reviewSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.reviewBottomSheet))

        arrivedSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.arrivedBottomSheet))

        allSheets.apply {
            add(locationSheetBehavior)
            add(driverSheetBehavior)
            add(confirmationSheetBehavior)
            add(paymentSheetBehavior)
            add(successSheetBehavior)
            add(trackingSheetBehavior)
            add(arrivedSheetBehavior)
            add(reviewSheetBehavior)
        }

        transitionToSheet(locationSheetBehavior)
    }

    private fun transitionToSheet(target: BottomSheetBehavior<LinearLayout>) {
        allSheets.forEach { sheet ->
            if (sheet == target) {
                sheet.isHideable = false
                sheet.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                sheet.isHideable = true
                sheet.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }

    private fun setupDriverList() {
        driverList.add(Driver("Gustavo Franci", 4.5, "Rp. 15.000", "5 min", 1, "Bike", "B 1234 GFR"))
        driverList.add(Driver("Globallyaass", 4.7, "Rp. 18.000", "7 min", 1, "Bike", "D 4567 KYT"))
        driverList.add(Driver("Madiun Speed", 4.9, "Rp. 14.000", "3 min", 1, "Bike", "AE 9988 XX"))

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
        transitionToSheet(confirmationSheetBehavior)
    }

    private fun setupConfirmationLogic() {
        findViewById<ImageButton>(R.id.btnCloseConfirmation).setOnClickListener {
            transitionToSheet(driverSheetBehavior)
        }
        findViewById<MaterialButton>(R.id.btnOrderNow).setOnClickListener {
            if (selectedDriver == null) {
                Toast.makeText(this, "Kesalahan: Driver tidak terpilih.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            rbCash.isChecked = true
            transitionToSheet(paymentSheetBehavior)
        }
    }

    private fun setupPaymentLogic() {
        findViewById<ImageButton>(R.id.btnClosePayment).setOnClickListener {
            transitionToSheet(confirmationSheetBehavior)
        }
        findViewById<MaterialButton>(R.id.btnContinuePayment).setOnClickListener {
            if (findViewById<RadioButton>(R.id.rbCash).isChecked) {
                transitionToSheet(successSheetBehavior)
            } else {
                Toast.makeText(this, "Hanya metode Cash yang tersedia saat ini", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSuccessLogic() {
        findViewById<MaterialButton>(R.id.btnTrackDriver).setOnClickListener {
            startLiveTracking()
        }
    }

    private fun setupArrivedLogic() {
        findViewById<MaterialButton>(R.id.btnFinishTrip).setOnClickListener {
            finish()
        }
        findViewById<MaterialButton>(R.id.btnReviewTrip).setOnClickListener {
            if(selectedDriver != null) {
                findViewById<TextView>(R.id.tvReviewDriverName).text = selectedDriver!!.name
                findViewById<TextView>(R.id.tvReviewPlate).text = selectedDriver!!.plateNumber
            }
            transitionToSheet(reviewSheetBehavior)
        }
    }

    private fun setupReviewLogic() {
        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        val etComment = findViewById<EditText>(R.id.etReviewComment)

        findViewById<MaterialButton>(R.id.btnSubmitReview).setOnClickListener {
            val rating = ratingBar.rating
            val comment = etComment.text.toString()

            if (rating == 0f) {
                Toast.makeText(this, "Please give a rating first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Here you would send data to API
            Toast.makeText(this, "Thank you for your review!", Toast.LENGTH_SHORT).show()

            // Finish activity and return to Home
            finish()
        }
    }

    private fun startLiveTracking() {
        if (startPoint == null || selectedDriver == null) return

        // Set Tracking Sheet Data
        findViewById<TextView>(R.id.tvTrackDriverName).text = selectedDriver!!.name
        findViewById<TextView>(R.id.tvTrackRating).text = "⭐ ${selectedDriver!!.rating}"
        findViewById<TextView>(R.id.tvTrackPrice).text = selectedDriver!!.price
        findViewById<TextView>(R.id.tvTrackSeats).text = "${selectedDriver!!.seats} Seats"
        findViewById<TextView>(R.id.tvTrackVehicle).text = selectedDriver!!.vehicleType
        findViewById<TextView>(R.id.tvTrackTime).text = selectedDriver!!.time
        findViewById<TextView>(R.id.tvTrackPlateNumber).text = selectedDriver!!.plateNumber

        map.overlays.clear()
        addPickupMarker(startPoint!!)
        if (endPoint != null) addDropoffMarker(endPoint!!)

        // Driver starts slightly away
        driverLocation = GeoPoint(startPoint!!.latitude + 0.003, startPoint!!.longitude + 0.003)
        addDriverMarker(driverLocation!!)
        zoomToFitTracking(driverLocation!!, startPoint!!)

        transitionToSheet(trackingSheetBehavior)

        CoroutineScope(Dispatchers.Main).launch {
            findViewById<TextView>(R.id.tvTrackStatus).text = "Driver Arriving"

            // Phase 1: Driver to Pickup
            val routeToPickup = fetchRoutePoints(driverLocation!!, startPoint!!)
            if (routeToPickup.isNotEmpty()) {
                drawPolyline(routeToPickup, Color.BLUE)
                animateMarker(driverMarker!!, routeToPickup, 4000) // Faster for demo
            }

            delay(1000)

            if (endPoint != null) {
                // Phase 2: Pickup to Destination
                findViewById<TextView>(R.id.tvTrackStatus).text = "On the way"
                findViewById<TextView>(R.id.tvTrackTime).text = "Arriving soon"

                // Clear previous route
                if(routePolyline != null) map.overlays.remove(routePolyline)

                val routeToDest = fetchRoutePoints(startPoint!!, endPoint!!)
                if (routeToDest.isNotEmpty()) {
                    drawPolyline(routeToDest, Color.GREEN)
                    zoomToFitTracking(startPoint!!, endPoint!!)
                    animateMarker(driverMarker!!, routeToDest, 4000) // Faster for demo
                }

                // Phase 3: Arrived
                findViewById<TextView>(R.id.tvTrackStatus).text = "Arrived"

                // Fill data for Arrived Sheet
                findViewById<TextView>(R.id.tvArrivedPrice).text = selectedDriver!!.price

                // Transition to Arrived Sheet
                transitionToSheet(arrivedSheetBehavior)
            }
        }
    }

    // ... Rest of the existing methods (animateMarker, fetchRoutePoints, etc) remain unchanged ...

    private suspend fun animateMarker(marker: Marker, points: ArrayList<GeoPoint>, durationMs: Long) {
        val interval = 20L
        val steps = (durationMs / interval).toInt()
        val pointsCount = points.size
        for (i in 0..steps) {
            val progress = i.toFloat() / steps
            val currentIndex = (progress * (pointsCount - 1)).toInt()
            if (currentIndex < points.size) {
                marker.position = points[currentIndex]
                map.invalidate()
            }
            delay(interval)
        }
        marker.position = points.last()
        map.invalidate()
    }

    private suspend fun fetchRoutePoints(start: GeoPoint, end: GeoPoint): ArrayList<GeoPoint> {
        return withContext(Dispatchers.IO) {
            val poly = ArrayList<GeoPoint>()
            try {
                val routeUrl = "https://router.project-osrm.org/route/v1/driving/${start.longitude},${start.latitude};${end.longitude},${end.latitude}?overview=full&geometries=polyline"
                val url = URL(routeUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(response)
                    val routes = jsonResponse.getJSONArray("routes")
                    if (routes.length() > 0) {
                        val polylineString = routes.getJSONObject(0).getString("geometry")
                        poly.addAll(decodePolyline(polylineString))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                poly.add(start)
                poly.add(end)
            }
            poly
        }
    }

    private fun drawPolyline(points: ArrayList<GeoPoint>, color: Int) {
        if (routePolyline != null) map.overlays.remove(routePolyline)
        routePolyline = Polyline(map)
        routePolyline?.setPoints(points)
        routePolyline?.outlinePaint?.color = color
        routePolyline?.outlinePaint?.strokeWidth = 15f
        map.overlays.add(0, routePolyline)
        map.invalidate()
    }

    private fun addDriverMarker(point: GeoPoint) {
        driverMarker = Marker(map)
        driverMarker?.position = point
        driverMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        driverMarker?.icon = ContextCompat.getDrawable(this, R.drawable.ic_motorpin)
        driverMarker?.title = "Driver"
        map.overlays.add(driverMarker)
        map.invalidate()
    }

    private fun zoomToFitTracking(p1: GeoPoint, p2: GeoPoint) {
        val boundingBox = BoundingBox(
            maxOf(p1.latitude, p2.latitude),
            maxOf(p1.longitude, p2.longitude),
            minOf(p1.latitude, p2.latitude),
            minOf(p1.longitude, p2.longitude)
        )
        map.zoomToBoundingBox(boundingBox, true, 150)
    }

    private fun setupTrackingLogic() {
        findViewById<MaterialButton>(R.id.btnCancelTrip).setOnClickListener {
            showCancelConfirmationDialog()
        }
        findViewById<ImageButton>(R.id.btnChat).setOnClickListener {
            Toast.makeText(this, "Membuka Chat...", Toast.LENGTH_SHORT).show()
        }
        findViewById<ImageButton>(R.id.btnCall).setOnClickListener {
            Toast.makeText(this, "Menelpon Driver...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showCancelConfirmationDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_cancel_trip)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val btnKeep = dialog.findViewById<MaterialButton>(R.id.btnDialogKeep)
        val btnCancel = dialog.findViewById<MaterialButton>(R.id.btnDialogCancel)

        btnKeep.setOnClickListener {
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
            performCancellation()
        }

        dialog.show()
    }

    private fun performCancellation() {
        map.overlays.clear()
        map.invalidate()

        transitionToSheet(locationSheetBehavior)

        Toast.makeText(this, "Trip cancelled", Toast.LENGTH_SHORT).show()
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
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { searchJob?.cancel() }
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
                    newSuggestions.add(PlaceSuggestion(obj.getString("display_name"), obj.getDouble("lat"), obj.getDouble("lon")))
                }
                withContext(Dispatchers.Main) {
                    suggestionAdapter.clear()
                    suggestionAdapter.addAll(newSuggestions)
                    suggestionAdapter.notifyDataSetChanged()
                    etDropoff.showDropDown()
                }
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun processSelectedLocation() {
        if (endPoint != null) {
            drawRoute()
            addDropoffMarker(endPoint!!)
            zoomToFitMarkers()
            showDummyDriversOnMap()
            transitionToSheet(driverSheetBehavior)
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
            if (query.isNotEmpty()) { searchLocation(query); hideKeyboard() } else etDropoff.error = "Masukkan tujuan dulu"
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
        CoroutineScope(Dispatchers.Main).launch {
            val routePoints = fetchRoutePoints(startPoint!!, endPoint!!)
            if (routePoints.isNotEmpty()) { drawPolyline(routePoints, Color.BLUE) }
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