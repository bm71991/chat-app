package com.bm.android.chat.conversations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bm.android.chat.R
import com.bm.android.chat.friend_requests.models.Friend

class NewConvoFragment : Fragment(), RecipientDialog.RecipientDialogInterface {
    interface NewConvoFragmentInterface {
        fun showProspectiveRecipientDialog()
    }

    private val mViewModel by lazy {
        ViewModelProviders.of(activity!!).get(ConvoViewModel::class.java)
    }
    private val mCallback by lazy {
        context as NewConvoFragmentInterface
    }
    private val mViewHolderCallback = object :RecipientViewHolder.RecipientViewHolderInterface  {
        override fun onClickDeleteBtn(friend:Friend) {
            mViewModel.recipientList.remove(friend)
            recipientList.adapter?.notifyDataSetChanged()
        }
    }
    private lateinit var recipientList:RecyclerView
    private lateinit var addRecipientBtn:Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val v = inflater.inflate(R.layout.fragment_new_convo, container, false)
        recipientList = v.findViewById(R.id.recipient_list)
        addRecipientBtn = v.findViewById(R.id.add_recipient_btn)
        val sendMessageBtn = v.findViewById<Button>(R.id.send_message_btn)

        addRecipientBtn.setOnClickListener {
            mCallback.showProspectiveRecipientDialog()
        }

        recipientList.layoutManager = LinearLayoutManager(activity)
        recipientList.adapter = RecipientAdapter(mViewModel.recipientList, mViewHolderCallback)

        sendMessageBtn.setOnClickListener {
            val messageInput = v.findViewById<EditText>(R.id.message_input)
            mViewModel.checkIfChatExists(messageInput.text.toString())
        }
        return v
    }

    //used in RecipientDialog
    override fun notifyRecipientListChanged() {
        recipientList.adapter?.notifyDataSetChanged()
        mViewModel.namesChecked.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewModel.recipientList.clear()
    }
}