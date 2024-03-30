package com.example.edzy

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class ProfileSetup : AppCompatActivity() {
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private lateinit var pickImage: CircleImageView
    private var imageUri2: Uri? = null
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
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_setup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val email = intent.getStringExtra("userEmail")
        pickImage = findViewById(R.id.profilepic)
        pickImage.setOnClickListener {
            imagePicker()
        }
        val createButton = findViewById<Button>(R.id.submit)
        createButton.setOnClickListener {
            val userName = findViewById<EditText>(R.id.name)
            val userName2 = userName.text.toString()

            if (imageUri2 != null) {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                val imageFileName = "profile_images/$userId.jpg"
                val imageRef = storageRef.child(imageFileName)
                imageRef.putFile(imageUri2!!)
                    .addOnSuccessListener {
                        val downloadUrlTask = imageRef.downloadUrl
                        downloadUrlTask.addOnSuccessListener { uri ->
                            addUser(email, userName2, uri)
                            val intent=Intent(this,MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirebaseStorageError", "Image upload failed", e)
                        Toast.makeText(
                            this,
                            "Image upload failed: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun imagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getContent.launch(intent)
    }

    private fun addUser(email: String?, userName: String?, imageUri: Uri?) {
        val db = Firebase.firestore
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val user = hashMapOf(
                "email" to email,
                "username" to userName,
                "userImage" to imageUri.toString()
            )

            db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener {
                    val message = "Profile Created with UID: $userId"
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreError", "Firestore operation failed", e)
                    Toast.makeText(
                        this,
                        "Firestore operation failed: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            Toast.makeText(this, "User is not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
}