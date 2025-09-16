package com.example.pizzamaniaapp.app.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.pizzamaniaapp.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pizzamania.data.repo.BranchRepo
import kotlinx.coroutines.launch

class BranchMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var hasCenteredOnUser = false

    // Runtime permission launcher
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
            val granted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (granted) {
                enableMyLocation()
                startLocationUpdates()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val frag = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        frag.getMapAsync(this)

        // 🔙 Back button
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Setup options
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true

        // Fetch branches from Firestore
        lifecycleScope.launch {
            val branches = BranchRepo(Firebase.firestore).getBranches()
            branches.forEach {
                val pos = LatLng(it.lat, it.lng)
                map.addMarker(
                    MarkerOptions()
                        .position(pos)
                        .title(it.name)
                        .snippet(it.address)
                )
            }
            // Default camera → first branch, if user hasn’t granted location
            branches.firstOrNull()?.let {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.lat, it.lng), 10f))
            }
        }

        if (hasLocationPermission()) {
            enableMyLocation()
            startLocationUpdates()
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    /** Helper: check if user granted location */
    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    /** Enable blue dot safely (avoid SecurityException) */
    private fun enableMyLocation() {
        if (hasLocationPermission()) {
            try {
                map.isMyLocationEnabled = true  // ✅ safe now
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    /** Start receiving user GPS updates */
    private fun startLocationUpdates() {
        if (!hasLocationPermission()) return

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L
        ).setMinUpdateIntervalMillis(2000L).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let {
                    val userLatLng = LatLng(it.latitude, it.longitude)
                    if (!hasCenteredOnUser) {
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14f))
                        hasCenteredOnUser = true
                    }
                }
            }
        }

        fusedLocationClient?.requestLocationUpdates(
            request,
            locationCallback as LocationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onStop() {
        super.onStop()
        locationCallback?.let { fusedLocationClient?.removeLocationUpdates(it) }
    }
}