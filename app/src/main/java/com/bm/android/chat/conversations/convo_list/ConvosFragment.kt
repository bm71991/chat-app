package com.bm.android.chat.conversations.convo_list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.bm.android.chat.R
import com.bm.android.chat.conversations.conversation.ChatViewModel
import com.bm.android.chat.conversations.models.Chat
import com.bm.android.chat.current_friends.FriendsViewModel
import com.bm.android.chat.friend_requests.FriendRequestsViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.fragment_convos.*
import java.util.ArrayList

class ConvosFragment : Fragment() {
    interface ConvosFragmentInterface {
        /********************************************
         * Since it is the entry point of the app,
         * this fragment enables the NavDrawer
         */
        fun showNavDrawer()
        fun setUsernameInNavDrawer()
        fun onStartChatFragment()
        fun changeActionbarTitle(title:String)
        fun setNavDrawerItemCount(itemId:Int, newCount:Int?)
        fun isFragmentVisible(tag:String):Boolean
    }

    private val mCallback by lazy {
        context as ConvosFragmentInterface
    }
    private val convosViewModel by lazy {
        ViewModelProviders.of(activity!!).get(ConvosViewModel::class.java)
    }
    private val chatViewModel by lazy {
        ViewModelProviders.of(activity!!).get(ChatViewModel::class.java)
    }
    private val friendRequestsViewModel by lazy {
        ViewModelProviders.of(activity!!).get(FriendRequestsViewModel::class.java)
    }
    private val friendsViewModel by lazy {
        ViewModelProviders.of(activity!!).get(FriendsViewModel::class.java)
    }
    private lateinit var adapter: ConvosAdapter
    private val chatItemCallback = object: ConvosAdapter.ConvoAdapterInterface   {
        override fun onClickConvo(members: ArrayList<String>, position:Int) {
            showProgressBar()
            val chatId = adapter.snapshots.getSnapshot(position).id
            val chatViewModel = ViewModelProviders.of(activity!!).get(ChatViewModel::class.java)
            chatViewModel.chatId = chatId
            mCallback.onStartChatFragment()
            hideProgressBar()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val v = inflater.inflate(R.layout.fragment_convos, container, false)
        mCallback.isFragmentVisible("RECEIVED_REQUESTS")
        mCallback.showNavDrawer()
        mCallback.setUsernameInNavDrawer()
        mCallback.changeActionbarTitle(getString(R.string.convos_title))

        if (chatViewModel.getCurrentUsername() != null) {
            if (chatViewModel.newMessageListener == null)   {
                Log.d("newMessageListener", "newMessageListener ${chatViewModel}  is being set")
                chatViewModel.setNewMessageListener()
                Log.d("newMessageListener", "listener = ${chatViewModel.newMessageListener}")
            } else {
                Log.d("newMessageListener", "newMessageListener is not being set")
            }

            if (friendRequestsViewModel.receivedRequestsListener == null) {
                friendRequestsViewModel.setReceivedRequestsListener()
                Log.d("friendRequestsListener", "friendRequestsListener is being set")
            }

            if (friendsViewModel.newFriendsCountListener == null)   {
                friendsViewModel.setNewFriendsCountListener()
                Log.d("friendsCountListener", "friendsCountListener is being set")
            }
        }

        setHasOptionsMenu(true)

        val query = convosViewModel.getChats()
        val options = FirestoreRecyclerOptions.Builder<Chat>()
            .setQuery(query, Chat::class.java)
            .build()

        val convoList = v.findViewById<RecyclerView>(R.id.convo_list)
        convoList.layoutManager = LinearLayoutManager(activity)
        adapter =
            ConvosAdapter(options, chatItemCallback)
        adapter.notifyDataSetChanged()
        convoList.adapter = adapter

        return v
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.stopListening()
    }

    fun showProgressBar()   {
        progress_bar.visibility = ProgressBar.VISIBLE
        convo_list.visibility = View.GONE
    }

    fun hideProgressBar()   {
        progress_bar.visibility = ProgressBar.GONE
        convo_list.visibility = View.VISIBLE
    }
}
