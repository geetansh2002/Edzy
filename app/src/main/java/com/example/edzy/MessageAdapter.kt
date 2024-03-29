package com.example.edzy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(private val messageList: MutableList<Message>,private val recyclerView: RecyclerView): RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.chat_message_recycle_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messageList[position]
        holder.bind(message)
    }
    fun addMessage(message: Message) {
        messageList.add(message)
        notifyItemInserted(messageList.size - 1)
        recyclerView.scrollToPosition(messageList.size - 1)
    }
    fun removeTypingIndicator() {
        val iterator = messageList.iterator()
        while (iterator.hasNext()) {
            val message = iterator.next()
            if (message.message == "typing....") {
                val position = messageList.indexOf(message)
                iterator.remove()
                notifyItemRemoved(position)
                return
            }
        }
    }
}
class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    private val leftChatTextView: TextView = itemView.findViewById(R.id.left_chat_text)
    private val rightChatTextView: TextView = itemView.findViewById(R.id.right_chat_text)
    private val leftChatTextView1: LinearLayout = itemView.findViewById(R.id.left_chat_layout)
    private val rightChatTextView1: LinearLayout = itemView.findViewById(R.id.right_chat_layout)

    fun bind(message: Message) {
        if (message.sentBy == Message.SENT_BY_ME) {
            rightChatTextView.text = message.message
            rightChatTextView1.visibility = View.VISIBLE
            leftChatTextView1.visibility = View.GONE
        } else if (message.sentBy == Message.SENT_BY_BOT) {
            leftChatTextView.text = message.message
            leftChatTextView1.visibility = View.VISIBLE
            rightChatTextView1.visibility = View.GONE
        }
    }
}