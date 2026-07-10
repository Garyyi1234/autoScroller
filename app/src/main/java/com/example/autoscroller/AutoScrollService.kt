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
import android.widget.LinearLayout

class AutoScrollService : AccessibilityService() {

    private var windowManager: WindowManager? = null
    private var floatingView: View? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        showFloatingMenu()
    }

    private fun showFloatingMenu() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        
        // Pin to the middle of the right edge
        layoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        layoutParams.x = 0
        layoutParams.y = 0

        floatingView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null)
        
        val btnTabHandle = floatingView?.findViewById<Button>(R.id.btnTabHandle)
        val menuSuite = floatingView?.findViewById<LinearLayout>(R.id.menuSuite)
        val btnScrollUp = floatingView?.findViewById<Button>(R.id.btnScrollUp)
        val btnScrollDown = floatingView?.findViewById<Button>(R.id.btnScrollDown)
        val btnDoubleTap = floatingView?.findViewById<Button>(R.id.btnDoubleTap)

        btnTabHandle?.setOnClickListener {
            if (menuSuite?.visibility == View.GONE) {
                menuSuite.visibility = View.VISIBLE
                btnTabHandle.text = ">"
            } else {
                menuSuite?.visibility = View.GONE
                btnTabHandle.text = "<"
            }
        }

        btnScrollUp?.setOnClickListener {
            performSwipeUp()
        }

        btnScrollDown?.setOnClickListener {
            performSwipeDown()
        }

        btnDoubleTap?.setOnClickListener {
            performDoubleTap()
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

        dispatchGesture(gestureBuilder.build(), null, null)
    }

    private fun performSwipeDown() {
        val displayMetrics = resources.displayMetrics
        
        val startX = displayMetrics.widthPixels / 2f
        // Start high, go low
        val startY = displayMetrics.heightPixels * 0.2f
        val endY = displayMetrics.heightPixels * 0.8f

        val swipePath = Path()
        swipePath.moveTo(startX, startY)
        swipePath.lineTo(startX, endY)

        val gestureBuilder = GestureDescription.Builder()
        val stroke = GestureDescription.StrokeDescription(swipePath, 0, 300)
        gestureBuilder.addStroke(stroke)

        dispatchGesture(gestureBuilder.build(), null, null)
    }

    private fun performDoubleTap() {
        val displayMetrics = resources.displayMetrics
        
        // Tap exactly in the center of the screen
        val tapX = displayMetrics.widthPixels / 2f
        val tapY = displayMetrics.heightPixels / 2f

        val tapPath = Path()
        tapPath.moveTo(tapX, tapY)

        val gestureBuilder = GestureDescription.Builder()
        
        // First tap starts at 0ms and lasts 100ms
        val tap1 = GestureDescription.StrokeDescription(tapPath, 0, 100)
        
        // Second tap starts at 150ms and lasts 100ms
        val tap2 = GestureDescription.StrokeDescription(tapPath, 150, 100)
        
        gestureBuilder.addStroke(tap1)
        gestureBuilder.addStroke(tap2)

        dispatchGesture(gestureBuilder.build(), null, null)
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
