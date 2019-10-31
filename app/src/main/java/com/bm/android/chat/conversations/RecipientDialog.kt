package com.bm.android.chat.conversations

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bm.android.chat.R
import com.bm.android.chat.friend_requests.models.Friend

class RecipientDialog: DialogFragment() {
    private val mViewModel by lazy {
        ViewModelProviders.of(activity!!).get(ConvoViewModel::class.java)
    }

    interface RecipientDialogInterface {
        fun notifyRecipientListChanged()
    }
    private lateinit var mRecyclerView:RecyclerView
    private lateinit var mAdapter:ProspectiveRecipientAdapter
    private lateinit var progressBar: ProgressBar
    private val checkBoxListener = object :
        ProspectiveRecipientViewHolder.ProspectiveRecipientInterface    {
        override fun onClickCheckbox(isChecked:Boolean, friend:Friend?) {
            if (isChecked)  {
                mViewModel.namesChecked.add(friend!!)
            } else {
                mViewModel.namesChecked.remove(friend)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val v = LayoutInflater.from(activity)
            .inflate(R.layout.dialog_recipients, null)
        progressBar = v.findViewById(R.id.progress_bar)

        mRecyclerView = v.findViewById(R.id.friend_selection_list)
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        mViewModel.getProspectiveRecipients()
        showProgressBar()
        mViewModel.getProspectiveRecipsStatus().observe(this, Observer {
            val result = it
            if (result != null) {
                mViewModel.clearProspectiveRecipsStatus()
                if (result == "LOADED") {
                    mAdapter = ProspectiveRecipientAdapter(mViewModel.prospectiveRecipients,
                        checkBoxListener)
                    mRecyclerView.adapter = mAdapter
                } else {
                    Log.d("recips", "ERROR: $it")
                }
                hideProgressBar()
            }
        })

        return AlertDialog.Builder(activity)
            .setView(v)
            .setTitle(getString(R.string.recipients_dialog_title))
            .setPositiveButton(android.R.string.ok) { dialog, whichButton ->
                //clear prospective recipients list
                mViewModel.prospectiveRecipients.clear()
                //add the friends whose names were checked to the recipient list
                mViewModel.recipientList.addAll(mViewModel.namesChecked)
                val newConvoCallback = targetFragment as RecipientDialogInterface
                newConvoCallback.notifyRecipientListChanged()
            }
            .create()
    }

    private fun showProgressBar()   {
        progressBar.visibility = View.VISIBLE
        mRecyclerView.visibility = View.GONE
    }

    private fun hideProgressBar()   {
        progressBar.visibility = View.GONE
        mRecyclerView.visibility = View.VISIBLE
    }
}