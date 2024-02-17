package com.example.keepmesafe

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.keepmesafe.databinding.ActivityHomepageBinding
import com.example.keepmesafe.databinding.BottomNavagationBarBinding

class HomepageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomepageBinding
    private lateinit var navbarBinding: BottomNavagationBarBinding
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var recyclerViewDangers: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("DarkMode", false)

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        setContentView(binding.root)

        recyclerViewDangers = findViewById<RecyclerView>(R.id.recyclerViewDangers)
        recyclerViewDangers.layoutManager = LinearLayoutManager(this)
        databaseHelper = DatabaseHelper(this)
        val dangers = databaseHelper.getAllReports().toMutableList()
        val dangerAdapter = DangerAdapter(dangers, this, databaseHelper)
        recyclerViewDangers.adapter = dangerAdapter

        navbarBinding = BottomNavagationBarBinding.bind(binding.root)

        val homeButton = navbarBinding.imageButtonHome
        val reportButton = navbarBinding.imageButtonReport
        val mapButton = navbarBinding.imageButtonMap
        val infoButton = navbarBinding.imageButtonInfo

        homeButton.setBackgroundColor(ContextCompat.getColor(this, R.color.md_theme_dark_onPrimary))

        mapButton.setOnClickListener {
            val intent = Intent(this@HomepageActivity, ViewMapActivity::class.java)
            startActivity(intent)
        }

        reportButton.setOnClickListener() {
            val intent = Intent(this@HomepageActivity, ReportActivity::class.java)
            startActivity(intent)
        }

        infoButton.setOnClickListener() {
            val intent = Intent(this@HomepageActivity, InfoActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val dangers = databaseHelper.getAllReports().toMutableList()
        val dangerAdapter = DangerAdapter(dangers, this, databaseHelper)
        recyclerViewDangers.adapter = dangerAdapter
    }
}