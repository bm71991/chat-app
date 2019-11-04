package com.bm.android.chat.conversations.conversation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private lateinit var adapter: ChatListAdapter
    private val chatViewModel by lazy {
        ViewModelProviders.of(activity!!).get(ChatViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val v =inflater.inflate(R.layout.fragment_chat, container, false)
        chatList = v.findViewById(R.id.chat_list)

        val layoutManager = LinearLayoutManager(activity)
        chatList.layoutManager = layoutManager
        val query = chatViewModel.getChatMessages()

        val options = FirestoreRecyclerOptions.Builder<ChatMessage>()
            .setQuery(query, ChatMessage::class.java)
            .build()

        chatViewModel.getChatInfo()
        chatViewModel.getUidMappingStatus().observe(this, Observer {
            val result = it
            if (result != null) {
                chatViewModel.clearUidMappingStatus()
                if (result.status == "LOADED") {
                    val uidUsernameMap = result.payload as HashMap<String, String>
                    //list users in taskbar
                    adapter = ChatListAdapter(options, uidUsernameMap)
                    adapter.startListening()
                    adapter.notifyDataSetChanged()
                    chatList.adapter = adapter
                } else {
                    Toast.makeText(activity, result.status,Toast.LENGTH_LONG).show()
                }
            }
        })
        return v
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}