package com.example.edzy

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class home_nav : Fragment() {
    private var username: String =""
    private var userImage: String=""
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val db= Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_nav, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.btn1).setOnClickListener {
            switchFragment(chat_nav())
        }
        view.findViewById<View>(R.id.btn2).setOnClickListener {
            switchFragment(scanner())
        }
        val name = view.findViewById<TextView>(R.id.profile_name)
        val image = view.findViewById<CircleImageView>(R.id.bot_img)

        getUsername { fetchedUsername, fetchedUserImage ->
            username = fetchedUsername
            userImage = fetchedUserImage
            name.text = username
            Glide.with(requireContext()).load(userImage).into(image)
        }
    }

    private fun getUsername(onUsernameFetched: (String, String) -> Unit) {
        val currentUser = currentUser
        currentUser?.let { user ->
            val userId = user.uid
            val userRef = db.collection("users").document(userId)

            userRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val username = documentSnapshot.getString("username") ?: "Default Username"
                        val userImage = documentSnapshot.getString("userImage") ?: "Default UserImage"
                        onUsernameFetched(username, userImage)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreError", "Error fetching user data", e)
                    Toast.makeText(requireContext(), "Error fetching user data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun switchFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
