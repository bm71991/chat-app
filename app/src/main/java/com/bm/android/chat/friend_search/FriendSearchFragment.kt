package com.bm.android.chat.friend_search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bm.android.chat.R
import android.R.menu




class FriendSearchFragment : Fragment() {
    private val mViewModel by lazy {
        ViewModelProviders.of(activity!!).get(FriendSearchViewModel::class.java)
    }
    interface FriendSearchInterface {
        fun changeActionbarTitle(title:String)
    }
    private lateinit var mSearchResultLayout:LinearLayout
    private lateinit var mProgressBar:ProgressBar
    private val mCallback by lazy {
        context as FriendSearchInterface
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mCallback.changeActionbarTitle(getString(R.string.friend_search_title))
        val v = inflater.inflate(R.layout.fragment_friend_search, container, false)
        val textInput = v.findViewById<EditText>(R.id.friend_search_input)
        val searchBtn = v.findViewById<Button>(R.id.friend_search_btn)
        val progressBar = v.findViewById<ProgressBar>(R.id.progress_bar)
        val usernameSearchStatus = mViewModel.getUsernameSearchStatus()
        var searchedName = ""
        mSearchResultLayout = v.findViewById(R.id.search_result_layout)
        mProgressBar = v.findViewById(R.id.progress_bar)

        searchBtn.setOnClickListener {
            showProgressBar()
            searchedName = textInput.text.toString()
            searchBtn.isClickable = false
            mViewModel.checkIfUserExists(searchedName)
        }

        usernameSearchStatus.observe(this, Observer {
            val result = it
            if (result != null) {
                mViewModel.clearUsernameSearchStatus()
                if (result == FriendSearchViewModel.OK_TO_DISPLAY)  {
                    val searchResultUsername = v.findViewById<TextView>(R.id.search_result_username)
                    searchResultUsername.text = searchedName
                    mSearchResultLayout.visibility = View.VISIBLE
                    setFriendRequestBtnListener(v)
                } else {
                    Toast.makeText(activity, result.toString(), Toast.LENGTH_LONG).show()
                }
                progressBar.visibility = ProgressBar.INVISIBLE
                searchBtn.isClickable = true
            }
        })
        return v
    }

    private fun setFriendRequestBtnListener(v: View)    {
        val friendRequestBtn = v.findViewById<Button>(R.id.friend_request_btn)
        friendRequestBtn.setOnClickListener {
            showProgressBar()
            mViewModel.sendFriendRequest()
        }

        val sendFriendRequestStatus = mViewModel.getSendFriendRequestStatus()
        sendFriendRequestStatus.observe(this, Observer {
            val result = it
            if (result != null) {
                mViewModel.clearSendFriendRequestStatus()
                /*If sending the friend request was successful*/
                if (result == FriendSearchViewModel.REQUEST_SENT)  {
                    hideProgressBarAndSearchResult()
                } else {
                    /* If sending the friend request was not successful, make mSearchResultLayout
                     * is visible so that the user can try to send the request again.
                     */
                    hideProgressBar()
                }
                Toast.makeText(activity, result.toString(), Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showProgressBar()   {
            mProgressBar.visibility = View.VISIBLE
            mSearchResultLayout.visibility = View.INVISIBLE
    }
    private fun hideProgressBarAndSearchResult()    {
        mProgressBar.visibility = View.INVISIBLE
        mSearchResultLayout.visibility = View.INVISIBLE
    }

    private fun hideProgressBar()   {
        mProgressBar.visibility = View.INVISIBLE
        mSearchResultLayout.visibility = View.VISIBLE
    }

//    fun showMenu(showMenu: Boolean) {
//        menu.setGroupVisible(R.id.main_menu_group, showMenu)
//    }
}