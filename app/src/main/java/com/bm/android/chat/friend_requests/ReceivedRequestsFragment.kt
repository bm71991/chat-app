package com.bm.android.chat.friend_requests

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
    private val TAG = "mainLog"
    private var adapter: FirestoreRecyclerAdapter<*, *>? = null
    private var requestsList: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_received_requests, parent, false)
        val query = FirebaseFirestore.getInstance()
            .collection("friends")
            .document(FirebaseAuth.getInstance().uid!!)
            .collection("receivedRequests")

        requestsList = v.findViewById(R.id.requests_list)
        val layoutManager = LinearLayoutManager(activity)
        requestsList?.layoutManager = layoutManager

        val options = FirestoreRecyclerOptions.Builder<ReceivedFriendRequest>()
            .setQuery(query, ReceivedFriendRequest::class.java)
            .build()

        adapter = object : FirestoreRecyclerAdapter<ReceivedFriendRequest, RequestHolder>(options) {
            public override fun onBindViewHolder(holder: RequestHolder, position: Int,
                model: ReceivedFriendRequest) {
                holder.bindData(model)
            }

            override fun onCreateViewHolder(group: ViewGroup, i: Int): RequestHolder {
                val view = LayoutInflater.from(group.context)
                    .inflate(R.layout.received_request, group, false)

                return RequestHolder(view)
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
}
