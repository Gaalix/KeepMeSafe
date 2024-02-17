package com.example.keepmesafe

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.example.keepmesafe.databinding.ActivityReportBinding
import com.example.libsqlite.Report
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.keepmesafe.databinding.BottomNavagationBarBinding

class ReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReportBinding
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var navbarBinding: BottomNavagationBarBinding

    private val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    private val REQUEST_LOCATION_PERMISSION = 1

    companion object {
        private const val CHANNEL_ID = "danger_location_channel"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("DarkMode", false)

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        setContentView(binding.root)

        val dangerId = intent.getIntExtra("ID", -1)
        val dangerDescription = intent.getStringExtra("DESCRIPTION")
        binding.editTextDangerDescription.setText(dangerDescription)

        databaseHelper = DatabaseHelper(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.buttonSubmit.setOnClickListener {
            if (hasLocationPermission()) {
                if (dangerId != -1) {
                    updateReport(dangerId)
                    Toast.makeText(this, "Danger updated", Toast.LENGTH_SHORT).show()
                } else {
                    getLastKnownLocation()
                }
            } else {
                requestLocationPermission()
            }
        }

        createNotificationChannel()

        navbarBinding = BottomNavagationBarBinding.bind(binding.root as ConstraintLayout)

        val homeButton = navbarBinding.imageButtonHome
        val reportButton = navbarBinding.imageButtonReport
        val mapButton = navbarBinding.imageButtonMap
        val infoButton = navbarBinding.imageButtonInfo

        reportButton.setBackgroundColor(ContextCompat.getColor(this, R.color.md_theme_dark_onPrimary))

        homeButton.setOnClickListener {
            val intent = Intent(this@ReportActivity, HomepageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        mapButton.setOnClickListener() {
            val intent = Intent(this@ReportActivity, ViewMapActivity::class.java)
            startActivity(intent)
            finish()
        }

        infoButton.setOnClickListener() {
            val intent = Intent(this@ReportActivity, InfoActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun hasLocationPermission(): Boolean {
        return permissions.all {
            ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_LOCATION_PERMISSION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                getLastKnownLocation()
            } else {
                Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    val dangerDescription = binding.editTextDangerDescription.text.toString()
                    val latitude = location?.latitude
                    val longitude = location?.longitude

                    if (latitude != null && longitude != null) {
                        val report = Report(id = null, description = dangerDescription, latitude = latitude, longitude = longitude)
                        databaseHelper.addReport(report)
                        val isNotificationsEnabled = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getBoolean("Notifications", true)
                        if (!isNotificationsEnabled) {
                            sendNotification(dangerDescription)
                        }
                    }
                }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(dangerDescription: String) {
        val intent = Intent(this, ViewMapActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.danger_icon)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(dangerDescription)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(this)) {
                notify(0, builder.build())
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Vibrate permission is required for notifications", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateReport(dangerId: Int) {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    val dangerDescription = binding.editTextDangerDescription.text.toString()
                    val latitude = location?.latitude
                    val longitude = location?.longitude

                    if (latitude != null && longitude != null) {
                        val report = Report(id = dangerId, description = dangerDescription, latitude = latitude, longitude = longitude)
                        databaseHelper.updateReport(report)
                    }
                }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
        }
    }
}