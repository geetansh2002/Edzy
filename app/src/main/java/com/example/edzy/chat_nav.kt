package com.example.edzy

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.PromptBlockedException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class chat_nav : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageList: MutableList<Message>
    private lateinit var messageAdapter: MessageAdapter
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messageList = mutableListOf()
        recyclerView = view.findViewById(R.id.chat_recycler_view)
        messageAdapter = MessageAdapter(messageList,recyclerView)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            (layoutManager as LinearLayoutManager).stackFromEnd = true
            recyclerView.layoutManager = layoutManager
            adapter = messageAdapter
        }
        val textbox=view.findViewById<EditText>(R.id.input_text)
        val sendButton=view.findViewById<ImageButton>(R.id.ryt_btn)
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            view.getWindowVisibleDisplayFrame(r)
            val screenHeight = view.height
            val keypadHeight = screenHeight - r.bottom
            if (keypadHeight > screenHeight * 0.15) {
                // Keyboard is open
                recyclerView.scrollToPosition(messageList.size - 1)
            } else {
            }
        }
        sendButton.setOnClickListener {
            val question = textbox.text.toString().trim()
            if (question.isNotEmpty()) {
                messageAdapter.addMessage(Message(question, Message.SENT_BY_ME))
                recyclerView.scrollToPosition(messageAdapter.itemCount)
                textbox.text.clear()

                // Launching a coroutine to call the suspend function
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        callAI(question)
                    } catch (e: IOException) {
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_nav, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            chat_nav().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private suspend fun callAI(question: String) {
        messageAdapter.addMessage(Message("typing....", Message.SENT_BY_BOT))
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = getString(R.string.ai_api_key)
        )
        try {
            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(question)
            }
            response.text?.let {
                Message(it, Message.SENT_BY_BOT)
            }?.let {
                messageAdapter.addMessage(it)
            }
        } catch (e: PromptBlockedException) {
            // Handle the blocked prompt exception
            // Here, you can return a warning message or take appropriate action
            messageAdapter.addMessage(Message("The prompt is inappropriate. Please try again with a different prompt.", Message.SENT_BY_BOT))
        } catch (e: IOException) {
            // Handle other IO exceptions
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            recyclerView.scrollToPosition(messageAdapter.itemCount)
            messageAdapter.removeTypingIndicator()
        }
    }

}