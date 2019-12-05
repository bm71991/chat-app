package com.bm.android.chat.friend_requests

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.bm.android.chat.R
import com.bm.android.chat.user_access.models.ReceivedFriendRequest
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException

class ReceivedRequestsFragment : Fragment() {
    interface ReceivedRequestsFragmentInterface {
        fun isFragmentVisible(tag:String):Boolean
    }
    private val mViewModel by lazy {
        ViewModelProviders.of(activity!!).get(FriendRequestsViewModel::class.java)
    }
    private val mCallback by lazy {
        context as ReceivedRequestsFragmentInterface
    }
    private val TAG = "mainLog"
    private var adapter: FirestoreRecyclerAdapter<*, *>? = null
    private var requestsList: RecyclerView? = null
    private val onSendRequestHandler = object : RequestHolder.RequestHolderClick  {
        override fun onClickAcceptBtn(senderId: String, senderUsername: String) {
            mViewModel.addToCurrentFriends(senderId, senderUsername)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (this.isVisible)
            mViewModel.receivedRequestsIsVisible = true

        Log.d("friendsListener", "ON VIEW CREATED receivedRequestsIsVisible = ${mViewModel.receivedRequestsIsVisible}")
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.fragment_received_requests, parent, false)
        val query = mViewModel.getReceivedRequests()

        requestsList = v.findViewById(R.id.requests_list)
        val layoutManager = LinearLayoutManager(activity)
        requestsList?.layoutManager = layoutManager

        mViewModel.removeReceivedRequestCount()

        val options = FirestoreRecyclerOptions.Builder<ReceivedFriendRequest>()
            .setQuery(query, ReceivedFriendRequest::class.java)
            .build()

        adapter = object : FirestoreRecyclerAdapter<ReceivedFriendRequest, RequestHolder>(options)
        {
            public override fun onBindViewHolder(holder: RequestHolder, position: Int,
                model: ReceivedFriendRequest) {
                holder.bindData(model)
            }

            override fun onCreateViewHolder(group: ViewGroup, i: Int): RequestHolder {
                val view = LayoutInflater.from(group.context)
                    .inflate(R.layout.received_request, group, false)

                return RequestHolder(view, onSendRequestHandler)
            }

            override fun onError(e: FirebaseFirestoreException) {
                super.onError(e)
                Log.d(TAG, e.toString())
            }

        }
        adapter?.notifyDataSetChanged()
        requestsList?.adapter = adapter

        return v
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewModel.receivedRequestsIsVisible = false
        Log.d("friendsListener", " ON DESTROY: receivedRequestsIsVisible = ${mViewModel.receivedRequestsIsVisible}")
    }
}
