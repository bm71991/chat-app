package com.bm.android.chat.conversations.conversation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bm.android.chat.R
import com.bm.android.chat.conversations.models.ChatMessage
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import android.widget.LinearLayout

class ChatFragment: Fragment() {
    private lateinit var chatList:RecyclerView
    private var adapter: ChatListAdapter? = null
    private val chatViewModel by lazy {
        ViewModelProviders.of(activity!!).get(ChatViewModel::class.java)
    }
    private val mCallback by lazy {
        context as ChatFragmentInterface
    }
    private var newChatListener:ListenerRegistration? = null

    interface ChatFragmentInterface {
        fun changeActionbarTitle(title:String)
        fun toMessageChangeDialog()
    }

    interface CurrentUserMessageCallback   {
        //navigate to MessageChangeDialog - passed to CurrentUserViewHolder in ChatListAdapter
        fun changeMessage(messageToChangeText:String, messageId:String)
    }

    private val currentUserMessageCallback = object : CurrentUserMessageCallback    {
        override fun changeMessage(messageToChangeText:String, messageId:String) {
            chatViewModel.messageToChangeText = messageToChangeText
            chatViewModel.messageToChangeId = messageId
            mCallback.toMessageChangeDialog()
        }
    }
    private lateinit var fragmentRootView:LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        if (chatViewModel.memberNames.isNotEmpty()) {
            changeActionbarTitle(chatViewModel.getMemberNameArray())
        }

        /***********************************************************
         * if the chatId is blank, set a listener which notifies the
         * application whether a new chat is created with the
         * member combination contained in chatViewModel.memberNames
         */
        if (chatViewModel.chatId.isBlank()) {
            newChatListener = chatViewModel.setNewChatListener()
        } else {
            chatViewModel.clearNewMessageCount()
        }

        val v =inflater.inflate(R.layout.fragment_chat, container, false)
        val sendBtn = v.findViewById<Button>(R.id.send_btn)
        val messageInput = v.findViewById<EditText>(R.id.message_input)
        fragmentRootView = v.findViewById<LinearLayout>(R.id.chat_fragment)
        chatList = v.findViewById(R.id.chat_list)


        chatList.layoutManager = getChatLayoutManager()
        setScrollToBottomListener()

        chatViewModel.getChatInfo()
        chatViewModel.getChatStatus().observe(this, Observer {
            val result = it
            if (result != null) {
                chatViewModel.clearChatStatus()
                if (result == "LOADED") {
                    initializeAdapter()
                    newChatListener?.remove()
                }
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
        adapter = ChatListAdapter(options, currentUserMessageCallback)
        adapter?.startListening()
        adapter?.notifyDataSetChanged()
        chatList.adapter = adapter
    }

    private fun getChatLayoutManager():LinearLayoutManager  {
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL)
        return layoutManager
    }

    private fun setScrollToBottomListener()    {
        /************************************************************
         * solution to have recyclerView scroll to bottom when item is
         * added was obtained from:
         * https://stackoverflow.com/questions/27016547/how-to-keep-recyclerview-always-scroll-bottom/36060470
         */
        fragmentRootView.viewTreeObserver
            .addOnGlobalLayoutListener  {
                if (adapter != null)    {
                    val heightDiff =
                        fragmentRootView.rootView.height - fragmentRootView.height
                    if (heightDiff > 100 && adapter!!.itemCount > 0) {
                        chatList.smoothScrollToPosition(adapter!!.itemCount - 1)
                    }
                }
            }
    }
}