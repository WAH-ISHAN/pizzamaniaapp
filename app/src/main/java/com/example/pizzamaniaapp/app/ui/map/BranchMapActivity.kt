package com.example.pizzamaniaapp.app.ui.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pizzamania.R
import com.pizzamania.data.repo.BranchRepo
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class BranchMapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val frag = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        frag.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        lifecycleScope.launch {
            val branches = BranchRepo(Firebase.firestore).getBranches()
            branches.forEach {
                val pos = LatLng(it.lat, it.lng)
                map.addMarker(MarkerOptions().position(pos).title(it.name).snippet(it.address))
            }
            branches.firstOrNull()?.let {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.lat, it.lng), 10f))
            }
        }
    }
}