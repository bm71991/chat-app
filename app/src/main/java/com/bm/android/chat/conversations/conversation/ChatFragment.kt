package com.bm.android.chat.conversations.conversation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bm.android.chat.R
import com.bm.android.chat.conversations.models.ChatMessage
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth

class ChatFragment: Fragment() {
    private lateinit var chatList:RecyclerView
    private var adapter: ChatListAdapter? = null
    private val chatViewModel by lazy {
        ViewModelProviders.of(activity!!).get(ChatViewModel::class.java)
    }
    private val mCallback by lazy {
        context as ChatFragmentInterface
    }

    interface ChatFragmentInterface {
        fun changeActionbarTitle(title:String)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        if (chatViewModel.memberNames.isNotEmpty()) {
            changeActionbarTitle(chatViewModel.getMemberNameArray())
        }

        val v =inflater.inflate(R.layout.fragment_chat, container, false)
        val sendBtn = v.findViewById<Button>(R.id.send_btn)
        val messageInput = v.findViewById<EditText>(R.id.message_input)
        chatList = v.findViewById(R.id.chat_list)

        val layoutManager = LinearLayoutManager(activity)
        chatList.layoutManager = layoutManager

        chatViewModel.getChatInfo()
        chatViewModel.getChatStatus().observe(this, Observer {
            val result = it
            if (result != null) {
                chatViewModel.clearChatStatus()
                if (result == "LOADED") {
                    initializeAdapter()
//                    changeActionbarTitle(chatViewModel.getUsernames() as ArrayList<String>)
//                    adapter = ChatListAdapter(options)
//                    adapter.startListening()
//                    adapter.notifyDataSetChanged()
//                    chatList.adapter = adapter
                }
//                else {
//                    Toast.makeText(activity, result,Toast.LENGTH_LONG).show()
//                }
            }
        })

        sendBtn.setOnClickListener {
            val messageText = messageInput.text.toString()
            if (chatViewModel.chatId.isBlank()) {
                chatViewModel.addChat(messageText)
            } else {
                chatViewModel.addChatMessage(messageText)
            }
            messageInput.text.clear()
        }

        return v
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter?.stopListening()
        chatViewModel.chatData = null
        chatViewModel.chatId = ""
        chatViewModel.memberNames.clear()
    }

    private fun changeActionbarTitle(usernames:ArrayList<String>?) {
        if (!usernames.isNullOrEmpty()) {
            val displayUsernames = ArrayList<String>()
            val currentUsername = FirebaseAuth.getInstance().currentUser?.displayName

            for (username in usernames) {
                if (username != currentUsername) displayUsernames.add(username)
            }

            val title = displayUsernames.joinToString()
            mCallback.changeActionbarTitle(title)
        }
    }

    private fun initializeAdapter() {
        val query = chatViewModel.getChatMessages()

        val options = FirestoreRecyclerOptions.Builder<ChatMessage>()
            .setQuery(query, ChatMessage::class.java)
            .build()
        changeActionbarTitle(chatViewModel.getMemberNameArray())
        adapter = ChatListAdapter(options)
        adapter?.startListening()
        adapter?.notifyDataSetChanged()
        chatList.adapter = adapter
    }
}