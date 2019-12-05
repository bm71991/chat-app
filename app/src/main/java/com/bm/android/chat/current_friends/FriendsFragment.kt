package com.bm.android.chat.current_friends

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
import androidx.recyclerview.widget.SortedList
import kotlinx.coroutines.Runnable
import com.bm.android.chat.R
import com.bm.android.chat.conversations.conversation.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration


class FriendsFragment : Fragment() {
    private val mCallback by lazy {
        context as FriendsInterface
    }
    private val mViewModel by lazy {
        ViewModelProviders.of(activity!!).get(FriendsViewModel::class.java)
    }

    interface FriendsInterface  {
        fun changeActionbarTitle(title:String)
        fun onStartChatFragment()
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

    private val friendClickCallback = object : FriendViewHolder.FriendClickCallback {
        override fun onClickFriendItem(friendUsername: String) {
            val currentUsername = FirebaseAuth.getInstance().currentUser!!.displayName!!
            val chatViewModel = ViewModelProviders.of(activity!!).get(ChatViewModel::class.java)
            val memberNames = arrayListOf(currentUsername, friendUsername)
            mViewModel.getChatId(memberNames)
            mViewModel.getChatIdStatus().observe(this@FriendsFragment, Observer {
                val result = it
                if (result != null) {
                    mViewModel.clearChatIdStatus()
                    if (result.status == "ERROR")    {
                        Log.d("errorTag", result.payload!!)
                    } else {
                        chatViewModel.chatId = result.payload!!
                        chatViewModel.memberNames.addAll(memberNames)
                        mCallback.onStartChatFragment()
                    }
                }
            })
        }
    }

    private var adapter:FriendsListAdapter? = null
    private var newFriendListener:ListenerRegistration? = null
    private lateinit var friendsList:RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.friendsFragmentIsVisible = true
        Log.d("friendsCountListener", " in onViewCreated FRIENDS FRAGMENT IS VISIBLE: ${mViewModel.friendsFragmentIsVisible}")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mCallback.changeActionbarTitle(getString(R.string.friends_title))

        val v = inflater.inflate(R.layout.fragment_friends, container, false)
        friendsList = v.findViewById(R.id.friends_list)
        mViewModel.getFriends()

        mViewModel.clearNewFriendsCount()

        mViewModel.getFriendLoadingStatus().observe(this, Observer {
            val result = it
            if (result != null) {
                mViewModel.clearFriendLoadingStatus()
                if (result.status == "LOADED") {
                    Log.d("friendsDebug", "IN LOADED FOR INITIAL: payload == ${it.payload!!}")
                    friendsList.layoutManager = LinearLayoutManager(activity!!)
                    adapter = FriendsListAdapter(it.payload!!, friendsListAdapterCallback,
                        friendClickCallback)
                    friendsList.adapter = adapter
                    setNewFriendListener()
                } else  {
                    Toast.makeText(activity, it.status, Toast.LENGTH_LONG).show()
                }
            }
        })


        return v
    }

    private fun setNewFriendListener() {
        newFriendListener = mViewModel.listenForFriendChanges()
        mViewModel.getNewFriendStatus().observe(this, Observer {
            val result = it
            if (result != null) {
                mViewModel.clearNewFriendStatus()
                Log.d("friendsDebug", " New Friend: Attempting to add ${result.getDisplayTitle()}, contains that name =  ${containsName(adapter?.dataset, result.getDisplayTitle())}")
                if (adapter != null && !containsName(adapter?.dataset, result.getDisplayTitle())) {
                    Log.d("friendsDebug", "does not contain name,  adding ${result.getDisplayTitle()}")
                    adapter?.dataset?.add(result)
                    adapter?.notifyDataSetChanged()
                }
            }
        })
    }
    override fun onDestroy() {
        super.onDestroy()
        mViewModel.firstLetterCountMap.clear()
        newFriendListener?.remove()
        mViewModel.friendsFragmentIsVisible = false
        Log.d("friendsCountListener", " in onDestroy FRIENDS FRAGMENT IS VISIBLE: ${mViewModel.friendsFragmentIsVisible}")
    }

    private fun containsName(list:SortedList<FriendsListItem>?, name:String):Boolean {
        var containsName = false
        var i = 0

        if (list != null)   {
            while (i < list.size() && !containsName) {
                Log.d("friendsDebug", "${list[i].getDisplayTitle()} and $name")

                if (list[i].getDisplayTitle() == name)  {
                    containsName = true
                }
                i++
            }
        }
        return containsName
    }

}
