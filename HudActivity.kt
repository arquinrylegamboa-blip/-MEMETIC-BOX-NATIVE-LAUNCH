package com.memeticbox.mobile

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import kotlin.math.hypot

class HudActivity : Activity() {
    private lateinit var root: FrameLayout
    private lateinit var stick: JoystickView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        root = FrameLayout(this).apply { setBackgroundColor(0xCC151B25.toInt()) }
        val title = TextView(this).apply {
            text = "TOUCH HUD PREVIEW  •  LANDSCAPE"
            textSize = 15f; setTextColor(0xFFFFFFFF.toInt())
        }
        root.addView(title, FrameLayout.LayoutParams(-2, -2, Gravity.TOP or Gravity.CENTER_HORIZONTAL).apply {
            topMargin = 18
        })
        stick = JoystickView(this)
        root.addView(stick, FrameLayout.LayoutParams(190, 190, Gravity.BOTTOM or Gravity.LEFT).apply {
            leftMargin = 28; bottomMargin = 28
        })
        addControl("CAMERA", Gravity.RIGHT or Gravity.CENTER_VERTICAL, 132, 0)
        addControl("JUMP", Gravity.RIGHT or Gravity.TOP, 30, 210)
        addControl("CROUCH", Gravity.RIGHT or Gravity.TOP, 30, 125)
        addControl("RELOAD", Gravity.RIGHT or Gravity.BOTTOM, 30, 145)
        addControl("INTERACT", Gravity.RIGHT or Gravity.BOTTOM, 30, 65)
        addControl("FIRE", Gravity.RIGHT or Gravity.BOTTOM, 160, 30, true)
        addControl("BACK", Gravity.LEFT or Gravity.TOP, 28, 24)
        setContentView(root)
    }

    private fun addControl(label: String, gravity: Int, x: Int, y: Int, primary: Boolean = false) {
        val control = TextView(this).apply {
            text = label
            textSize = if (primary) 15f else 11f
            gravity = Gravity.CENTER
            setTextColor(0xFFFFFFFF.toInt())
            background = getDrawable(android.R.drawable.btn_default)
            alpha = if (primary) .88f else .72f
            setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) v.alpha = 1f
                if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) v.alpha = .72f
                true
            }
            if (label == "BACK") setOnClickListener { finish() }
        }
        val size = if (primary) 112 else 68
        root.addView(control, FrameLayout.LayoutParams(size, size, gravity).apply {
            if (gravity and Gravity.RIGHT == Gravity.RIGHT) rightMargin = x
            if (gravity and Gravity.LEFT == Gravity.LEFT) leftMargin = x
            if (gravity and Gravity.TOP == Gravity.TOP) topMargin = y
            if (gravity and Gravity.BOTTOM == Gravity.BOTTOM) bottomMargin = y
        })
    }

    private class JoystickView(activity: Activity) : View(activity) {
        private val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
        private var dx = 0f; private var dy = 0f
        override fun onDraw(canvas: android.graphics.Canvas) {
            super.onDraw(canvas)
            val cx = width / 2f; val cy = height / 2f
            paint.color = 0x558A9BAD
            canvas.drawCircle(cx, cy, width * .48f, paint)
            paint.color = 0x99D9E2EC.toInt()
            canvas.drawCircle(cx + dx, cy + dy, width * .22f, paint)
        }
        override fun onTouchEvent(event: MotionEvent): Boolean {
            val cx = width / 2f; val cy = height / 2f
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    var x = event.x - cx; var y = event.y - cy
                    val max = width * .25f
                    val d = hypot(x, y)
                    if (d > max) { x *= max / d; y *= max / d }
                    dx = x; dy = y; invalidate(); return true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    dx = 0f; dy = 0f; invalidate(); return true
                }
            }
            return true
        }
    }
}
