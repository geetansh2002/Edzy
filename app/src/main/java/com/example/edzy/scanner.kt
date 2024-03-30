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
    private lateinit var uri1:Uri
    private lateinit var pickImage: ImageButton
    private var imageUri2: Uri? = null
    @RequiresApi(Build.VERSION_CODES.P)
    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                val selectedImage: Uri? = data.data
                selectedImage?.let {
                    imageUri2 = selectedImage
                    Glide.with(this)
                        .load(selectedImage)
                        .into(pickImage)

                    imageUri2?.let { uri ->
                        uri1= imageUri2 as Uri
                    }
                }
            }
        }
    }
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scanner, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            scanner().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pickImage=view.findViewById(R.id.scan_img_btn)
        val summarizeBut=view.findViewById<Button>(R.id.summarize_btn)
        val typebox=view.findViewById<EditText>(R.id.language_selection)
        val translateBut=view.findViewById<Button>(R.id.translate_btn)

        pickImage.setOnClickListener {
            imagePicker()
        }
        summarizeBut.setOnClickListener {
            val intent = Intent(requireContext(), Summarize::class.java)
            uri1?.let { uri ->
                intent.putExtra("uri", uri.toString())
                startActivity(intent)
            } ?: run {
                Toast.makeText(requireContext(), "Please select an image first", Toast.LENGTH_SHORT).show()
            }
        }

        val language=typebox.text.toString()
        translateBut.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                } catch (e: IOException) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.P)
    private suspend fun getBitmapFromUri(uri: Uri): Bitmap = withContext(Dispatchers.IO) {
        val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
        return@withContext ImageDecoder.decodeBitmap(source)
    }
    private suspend fun translate(language:String,imageBitmap: Bitmap) {
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro-vision",
            apiKey = getString(R.string.ai_api_key)
        )

        val inputContent = content {
            image(imageBitmap)
            text("translate the text in the image in $language")
        }

        val response = generativeModel.generateContent(inputContent)
    }
    @RequiresApi(Build.VERSION_CODES.P)
    private fun imagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getContent.launch(intent)
    }
}