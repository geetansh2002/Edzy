package com.example.edzy

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class scanner : Fragment() {
    private var uri1: Uri? = null
    private lateinit var pickImage: ImageButton
    private var imageUri2: Uri? = null

    @RequiresApi(Build.VERSION_CODES.P)
    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val selectedImage: Uri? = data.data
                    selectedImage?.let {
                        imageUri2 = selectedImage
                        Glide.with(this)
                            .load(selectedImage)
                            .into(pickImage)
                        uri1 = selectedImage
                    }
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scanner, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pickImage = view.findViewById(R.id.scan_img_btn)
        val summarizeBut = view.findViewById<Button>(R.id.summarize_btn)
        val typebox = view.findViewById<EditText>(R.id.language_selection)
        val translateBut = view.findViewById<Button>(R.id.translate_btn)

        pickImage.setOnClickListener {
            imagePicker()
        }

        summarizeBut.setOnClickListener {
            uri1?.let { uri ->
                val intent = Intent(requireActivity(), Summarize::class.java)
                intent.putExtra("uri", uri.toString())
                startActivity(intent)
            }
        }

        translateBut.setOnClickListener {
            val language = typebox.text.toString()
            uri1?.let { uri ->
                val intent = Intent(requireActivity(), Translator::class.java)
                intent.putExtra("uri", uri.toString())
                intent.putExtra("language", language)
                startActivity(intent)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.P)
    private  fun imagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getContent.launch(intent)
    }
}