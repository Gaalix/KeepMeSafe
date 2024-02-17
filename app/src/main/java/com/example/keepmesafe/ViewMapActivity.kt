package com.example.keepmesafe

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.keepmesafe.databinding.ActivityViewMapBinding
import com.example.keepmesafe.databinding.BottomNavagationBarBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions



class ViewMapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityViewMapBinding
    private lateinit var mMap: GoogleMap
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var navbarBinding: BottomNavagationBarBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewMapBinding.inflate(layoutInflater)
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("DarkMode", false)

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        navbarBinding = BottomNavagationBarBinding.bind(binding.root as ConstraintLayout)

        val homeButton = navbarBinding.imageButtonHome
        val reportButton = navbarBinding.imageButtonReport
        val mapButton = navbarBinding.imageButtonMap
        val infoButton = navbarBinding.imageButtonInfo

        mapButton.setBackgroundColor(ContextCompat.getColor(this, R.color.md_theme_dark_onPrimary))

        homeButton.setOnClickListener {
            val intent = Intent(this@ViewMapActivity, HomepageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        reportButton.setOnClickListener() {
            val intent = Intent(this@ViewMapActivity, ReportActivity::class.java)
            startActivity(intent)
            finish()
        }

        infoButton.setOnClickListener() {
            val intent = Intent(this@ViewMapActivity, InfoActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val latitude = intent.getDoubleExtra("LATITUDE", 0.0)
        val longitude = intent.getDoubleExtra("LONGITUDE", 0.0)

        if (latitude != 0.0 && longitude != 0.0) {
            val location = LatLng(latitude, longitude)
            mMap.addMarker(MarkerOptions().position(location).title("Danger Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }

        val reports = databaseHelper.getAllReports()
        for (report in reports) {
            val location = LatLng(report.latitude, report.longitude)
            mMap.addMarker(MarkerOptions().position(location).title(report.description))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLocation = LatLng(it.latitude, it.longitude)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(currentLocation)
                            .title("You are here")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    )
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
                }
            }
    }
}