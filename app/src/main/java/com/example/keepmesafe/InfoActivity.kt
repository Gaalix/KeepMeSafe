package com.example.keepmesafe

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.keepmesafe.databinding.ActivityInfoBinding
import com.example.keepmesafe.databinding.BottomNavagationBarBinding

class InfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoBinding
    private lateinit var navbarBinding: BottomNavagationBarBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("DarkMode", false)

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        setContentView(binding.root)

        navbarBinding = BottomNavagationBarBinding.bind(binding.root as ConstraintLayout)

        val homeButton = navbarBinding.imageButtonHome
        val reportButton = navbarBinding.imageButtonReport
        val mapButton = navbarBinding.imageButtonMap
        val infoButton = navbarBinding.imageButtonInfo

        infoButton.setBackgroundColor(ContextCompat.getColor(this, R.color.md_theme_dark_onPrimary))

        homeButton.setOnClickListener {
            val intent = Intent(this@InfoActivity, HomepageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        reportButton.setOnClickListener() {
            val intent = Intent(this@InfoActivity, ReportActivity::class.java)
            startActivity(intent)
            finish()
        }

        mapButton.setOnClickListener() {
            val intent = Intent(this@InfoActivity, ViewMapActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.imageButtonSettings.setOnClickListener() {
            val intent = Intent(this@InfoActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}