package com.example.autoscroller

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Path
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.Button

class AutoScrollService : AccessibilityService() {

    private var windowManager: WindowManager? = null
    private var floatingView: View? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        showFloatingButton()
    }

    private fun showFloatingButton() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        
        layoutParams.gravity = Gravity.BOTTOM or Gravity.END
        layoutParams.x = 50
        layoutParams.y = 150

        floatingView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null)
        
        val btnSwipeUp = floatingView?.findViewById<Button>(R.id.btnSwipeUp)
        btnSwipeUp?.setOnClickListener {
            performSwipeUp()
        }

        windowManager?.addView(floatingView, layoutParams)
    }

    private fun performSwipeUp() {
        val displayMetrics = resources.displayMetrics
        
        val startX = displayMetrics.widthPixels / 2f
        val startY = displayMetrics.heightPixels * 0.8f
        val endY = displayMetrics.heightPixels * 0.2f

        val swipePath = Path()
        swipePath.moveTo(startX, startY)
        swipePath.lineTo(startX, endY)

        val gestureBuilder = GestureDescription.Builder()
        val stroke = GestureDescription.StrokeDescription(swipePath, 0, 300)
        gestureBuilder.addStroke(stroke)

        dispatchGesture(gestureBuilder.build(), object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
            }
            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
            }
        }, null)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Not used
    }

    override fun onInterrupt() {
        // Service interrupted
    }

    override fun onDestroy() {
        super.onDestroy()
        if (floatingView != null) {
            windowManager?.removeView(floatingView)
            floatingView = null
        }
    }
}
