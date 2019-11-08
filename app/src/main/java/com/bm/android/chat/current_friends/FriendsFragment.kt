package com.bm.android.chat.current_friends

import android.net.sip.SipSession
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
import kotlinx.coroutines.Runnable
import com.bm.android.chat.R
import com.google.firebase.firestore.ListenerRegistration


class FriendsFragment : Fragment() {
    interface FriendsInterface  {
        fun changeActionbarTitle(title:String)
    }
    interface FriendsListItem   {
        companion object    {
            val TYPE_LETTER = 0
            val TYPE_FRIEND = 1
        }

        fun getType():Int

        fun getDisplayTitle():String
    }

    abstract class FriendItemViewHolder(itemView:View): RecyclerView.ViewHolder(itemView)  {
        abstract fun bindData(model:FriendsListItem)
    }

    private val friendsListAdapterCallback = object : FriendsListAdapter.FriendsListAdapterInterface  {
        override fun checkIfNewFirstLetter(letter: String) {
            val upperCaseLetter = letter.toUpperCase()
            if (mViewModel.mapContainsLetter(upperCaseLetter))   {
                mViewModel.incrementLetterCount(upperCaseLetter)
            } else {

                mViewModel.startLetterCount(upperCaseLetter)
                val newLetterItem = LetterItem(upperCaseLetter)
                friendsList.post(Runnable {
                    adapter?.dataset?.add(newLetterItem)
                    adapter?.notifyDataSetChanged()
                })
            }
        }
    }

    private val mCallback by lazy {
        context as FriendsInterface
    }
    private val mViewModel by lazy {
        ViewModelProviders.of(activity!!).get(FriendsViewModel::class.java)
    }
    private var adapter:FriendsListAdapter? = null
    private var newFriendListener:ListenerRegistration? = null
    private lateinit var friendsList:RecyclerView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mCallback.changeActionbarTitle(getString(R.string.friends_title))

        val v = inflater.inflate(R.layout.fragment_friends, container, false)
        friendsList = v.findViewById(R.id.friends_list)
        mViewModel.getFriends()

        mViewModel.getFriendLoadingStatus().observe(this, Observer {
            val result = it
            if (result != null) {
                mViewModel.clearFriendLoadingStatus()
                if (result.status == "LOADED") {
                    friendsList.layoutManager = LinearLayoutManager(activity!!)
                    adapter = FriendsListAdapter(it.payload!!, friendsListAdapterCallback)
                    friendsList.adapter = adapter
                } else  {
                    Toast.makeText(activity, it.status, Toast.LENGTH_LONG).show()
                }
            }
        })

        newFriendListener = mViewModel.listenForFriendChanges()
        mViewModel.getNewFriendStatus().observe(this, Observer {
            val result = it
            if (result != null) {
                mViewModel.clearNewFriendStatus()
                adapter?.dataset?.add(result)
                adapter?.notifyDataSetChanged()
            }
        })
        return v
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewModel.firstLetterCountMap.clear()
        newFriendListener?.remove()
    }
}
