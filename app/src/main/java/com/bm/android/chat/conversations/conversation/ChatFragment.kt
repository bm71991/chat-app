package com.bm.android.chat.conversations.conversation

import android.os.Bundle
import android.util.Log
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
import java.lang.StringBuilder

class ChatFragment: Fragment() {
    private lateinit var chatList:RecyclerView
    private lateinit var adapter: ChatListAdapter
    private val chatViewModel by lazy {
        ViewModelProviders.of(activity!!).get(ChatViewModel::class.java)
    }
    private val mCallback by lazy {
        context as ChatFragmentInterface
    }

    interface ChatFragmentInterface {
        fun changeActionbarTitle(title:String)
        fun getUidUsernameMap():HashMap<String, String>
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val v =inflater.inflate(R.layout.fragment_chat, container, false)
        val sendBtn = v.findViewById<Button>(R.id.send_btn)
        val messageInput = v.findViewById<EditText>(R.id.message_input)
        chatList = v.findViewById(R.id.chat_list)

        val layoutManager = LinearLayoutManager(activity)
        chatList.layoutManager = layoutManager
        val query = chatViewModel.getChatMessages()

        val options = FirestoreRecyclerOptions.Builder<ChatMessage>()
            .setQuery(query, ChatMessage::class.java)
            .build()

        changeActionbarTitle(mCallback.getUidUsernameMap())
        adapter = ChatListAdapter(options, mCallback.getUidUsernameMap())
        adapter.startListening()
        adapter.notifyDataSetChanged()
        chatList.adapter = adapter

        sendBtn.setOnClickListener {
            val messageText = messageInput.text.toString()
            if (messageText.isNotBlank())   {
                chatViewModel.addChatMessage(messageText)
                messageInput.text.clear()
            }
        }

        return v
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    private fun changeActionbarTitle(uidUsernameMap:HashMap<String, String>) {
        val usernames = uidUsernameMap.values
        val currentUsername = FirebaseAuth.getInstance().currentUser?.displayName
        if (usernames.contains(currentUsername)) usernames.remove(currentUsername)
        val title = usernames.joinToString()
        mCallback.changeActionbarTitle(title)
    }
}