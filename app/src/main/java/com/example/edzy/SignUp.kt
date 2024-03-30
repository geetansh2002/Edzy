package com.example.edzy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUp : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()
        val button1 = findViewById<Button>(R.id.sign_up)
        val emailEditText = findViewById<EditText>(R.id.email)
        val passwordEditText = findViewById<EditText>(R.id.password)
        button1.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and Password is required", Toast.LENGTH_SHORT)
                    .show()
            } else {
                signUp(email, password)
            }
        }
        val button2 = findViewById<TextView>(R.id.signin)
        button2.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }
    }
        private fun signUp(email: String, password: String) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        updateUI(user, email)
                    } else {
                        val exception = task.exception
                        Toast.makeText(
                            this,
                            "Authentication failed: ${exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateUI(null, null)
                    }
                }
        }

        private fun updateUI(user: FirebaseUser?, email: String?) {
            if (user != null) {
                val intent = Intent(this, ProfileSetup::class.java)
                intent.putExtra("userEmail", email)
                startActivity(intent)
            }
        }
}