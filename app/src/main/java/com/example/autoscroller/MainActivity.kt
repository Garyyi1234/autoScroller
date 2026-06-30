package com.example.autoscroller

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvOverlayStatus: TextView
    private lateinit var btnRequestOverlay: Button
    private lateinit var tvAccessibilityStatus: TextView
    private lateinit var btnOpenAccessibility: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvOverlayStatus = findViewById(R.id.tvOverlayStatus)
        btnRequestOverlay = findViewById(R.id.btnRequestOverlay)
        tvAccessibilityStatus = findViewById(R.id.tvAccessibilityStatus)
        btnOpenAccessibility = findViewById(R.id.btnOpenAccessibility)

        btnRequestOverlay.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            }
        }

        btnOpenAccessibility.setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    private fun updateStatus() {
        // Overlay Status
        if (Settings.canDrawOverlays(this)) {
            tvOverlayStatus.text = "Overlay Permission: Granted"
            btnRequestOverlay.isEnabled = false
        } else {
            tvOverlayStatus.text = "Overlay Permission: Missing"
            btnRequestOverlay.isEnabled = true
        }

        // Accessibility Status
        if (isAccessibilityServiceEnabled(this, AutoScrollService::class.java)) {
            tvAccessibilityStatus.text = "Accessibility Service: Enabled"
            btnOpenAccessibility.isEnabled = false
        } else {
            tvAccessibilityStatus.text = "Accessibility Service: Disabled"
            btnOpenAccessibility.isEnabled = true
        }
    }

    private fun isAccessibilityServiceEnabled(context: Context, accessibilityService: Class<*>): Boolean {
        var accessibilityEnabled = 0
        val service = context.packageName + "/" + accessibilityService.canonicalName
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                context.applicationContext.contentResolver,
                android.provider.Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (e: SettingNotFoundException) {
            // Error handling
        }
        val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(
                context.applicationContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityServiceStr = mStringColonSplitter.next()
                    if (accessibilityServiceStr.equals(service, ignoreCase = true)) {
                        return true
                    }
                }
            }
        }
        return false
    }
}
