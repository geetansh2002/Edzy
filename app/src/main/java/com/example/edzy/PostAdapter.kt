package com.example.edzy

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import java.text.SimpleDateFormat
import java.util.Locale

class PostAdapter(
    options: FirestoreRecyclerOptions<Post>,
    private val context: Context
) : FirestoreRecyclerAdapter<Post, PostAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itempost, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Post) {
        holder.bind(model)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userImage: ImageView = itemView.findViewById(R.id.userImage)
        private val userName: TextView = itemView.findViewById(R.id.userName)
        private val createdAt: TextView = itemView.findViewById(R.id.createdAt)
        private val post: TextView = itemView.findViewById(R.id.postTitle)

        fun bind(post: Post) {
            Glide.with(itemView.context)
                .load(post.userImage)
                .circleCrop()
                .into(userImage)

            userName.text = post.username
            this.post.text = post.post
            createdAt.text = getTimeAgo(post.currentTime)
        }

        private fun getTimeAgo(timestamp: String?): String {
            if (timestamp == null) {
                return "N/A"
            }
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = dateFormat.parse(timestamp) ?: return "Invalid Timestamp"

            val currentTime = System.currentTimeMillis()
            val timeDiff = currentTime - date.time

            val seconds = timeDiff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24

            return when {
                seconds < 60 -> "just now"
                minutes < 60 -> "$minutes minutes ago"
                hours < 24 -> "$hours hours ago"
                days == 1L -> "yesterday"
                else -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
            }
        }
    }
}
