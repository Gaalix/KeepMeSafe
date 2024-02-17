package com.example.keepmesafe

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Switch
import com.example.keepmesafe.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.imageButtonBack.setOnClickListener() {
            finish()
        }

        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val darkModeSwitch = findViewById<Switch>(R.id.settingSwitchDarkMode)
        val isDarkMode = sharedPref.getBoolean("DarkMode", false)
        darkModeSwitch.isChecked = isDarkMode
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPref.edit()
            editor.putBoolean("DarkMode", isChecked)
            editor.apply()
        }

        val notificationsSwitch = findViewById<Switch>(R.id.settingSwitchNotifications)
        val isNotificationsEnabled = sharedPref.getBoolean("Notifications", true)
        notificationsSwitch.isChecked = isNotificationsEnabled
        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPref.edit()
            editor.putBoolean("Notifications", isChecked)
            editor.apply()
        }
    }
}