package com.example.edzy

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class Summarize : AppCompatActivity() {
    private lateinit var textView:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_summarize2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        textView=findViewById(R.id.left_chat_text)
        val byteArray = intent.getByteArrayExtra("bitmap")
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray?.size ?: 0)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                summarize(bitmap)
            } catch (e: IOException) {
                Toast.makeText(this@Summarize, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private suspend fun summarize(imageBitmap: Bitmap) {
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro-vision",
            apiKey = getString(R.string.ai_api_key)
        )

        val inputContent = content {
            image(imageBitmap)
            text("Summarize the text in the image in simple words")
        }

        val response = generativeModel.generateContent(inputContent)
        textView.text=response.text
    }
}