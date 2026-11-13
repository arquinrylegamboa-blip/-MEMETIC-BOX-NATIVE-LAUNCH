package com.memeticbox.mobile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private val prefs by lazy { getSharedPreferences("memeticbox", MODE_PRIVATE) }
    private var gameUri: Uri?
        get() = prefs.getString("game_uri", null)?.let(Uri::parse)
        set(value) { prefs.edit().putString("game_uri", value?.toString()).apply() }
    private lateinit var selectedFile: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        buildUi()
    }

    private fun buildUi() {
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 22, 28, 22)
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(0xFF10151F.toInt())
        }
        page.addView(TextView(this).apply {
            text = "MEMETICBOX"
            textSize = 30f
            setTextColor(0xFFFFFFFF.toInt())
            letterSpacing = .14f
        })
        page.addView(TextView(this).apply {
            text = "MOBILE • PC EDITION"
            textSize = 12f
            setTextColor(0xFF8FA7C7.toInt())
            setPadding(0, 2, 0, 20)
        })
        selectedFile = TextView(this).apply {
            textSize = 14f
            setTextColor(0xFFD7E2F1.toInt())
            text = gameUri?.lastPathSegment?.let { "Selected game file: $it" } ?: "No game EXE selected"
            setPadding(0, 8, 0, 16)
        }
        page.addView(selectedFile)
        page.addView(actionButton("SELECT MEMETICBOX.EXE") { chooseExe() })
        page.addView(actionButton("OPEN IN EXTERNAL RUNTIME") { handoffToRuntime() })
        page.addView(actionButton("TOUCH CONTROL LAYOUT") {
            startActivity(Intent(this, HudActivity::class.java))
        })
        page.addView(TextView(this).apply {
            text = "External runtime required. Select your own legally obtained Windows game file, then choose a compatible runtime that can accept the file."
            textSize = 12f
            setTextColor(0xFF91A0B5.toInt())
            setPadding(0, 18, 0, 0)
        })
        setContentView(page)
    }

    private fun actionButton(label: String, click: () -> Unit): Button =
        Button(this).apply {
            text = label
            isAllCaps = false
            textSize = 14f
            setOnClickListener { click() }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 8 }
        }

    private fun chooseExe() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        startActivityForResult(intent, REQUEST_EXE)
    }

    @Deprecated("Use Activity Result APIs in a production release")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EXE && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            try {
                contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) { }
            gameUri = uri
            selectedFile.text = "Selected game file: ${uri.lastPathSegment ?: "Windows EXE"}"
            Toast.makeText(this, "Game file selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handoffToRuntime() {
        val uri = gameUri
        if (uri == null) {
            Toast.makeText(this, "Select your game EXE first", Toast.LENGTH_LONG).show()
            return
        }
        val send = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/octet-stream")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            startActivity(Intent.createChooser(send, "Open game with a compatible PC runtime"))
        } catch (_: Exception) {
            Toast.makeText(this, "No app can accept this EXE. Open your PC runtime and import the file there.", Toast.LENGTH_LONG).show()
        }
    }

    companion object { private const val REQUEST_EXE = 4101 }
}
