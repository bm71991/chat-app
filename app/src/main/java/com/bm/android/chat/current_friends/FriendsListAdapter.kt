package com.bm.android.chat.current_friends

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import androidx.recyclerview.widget.SortedListAdapterCallback
import com.bm.android.chat.R
import com.bm.android.chat.current_friends.FriendsFragment.FriendsListItem

class FriendsListAdapter(initialInput:List<FriendsListItem>,
                         val mCallback:FriendsListAdapterInterface)
    :RecyclerView.Adapter<FriendsFragment.FriendItemViewHolder>() {
    interface FriendsListAdapterInterface    {
        fun checkIfNewFirstLetter(letter:String)
    }
    val dataset:SortedList<FriendsListItem>

    init {
        dataset = SortedList(FriendsListItem::class.java,
            object: SortedListAdapterCallback<FriendsListItem>(this)   {
                override fun areContentsTheSame(oldItem: FriendsListItem,
                    newItem: FriendsListItem): Boolean {
                    return oldItem.getDisplayTitle().toLowerCase() == newItem.getDisplayTitle().toLowerCase()
                }

                override fun areItemsTheSame(item1: FriendsListItem, item2: FriendsListItem)
                        : Boolean {
                    return item1 == item2
                }

                override fun compare(o1: FriendsListItem, o2: FriendsListItem): Int {
                    val itemsInOrder = listOf(o1, o2).sortedBy { it.getDisplayTitle().toLowerCase() }

                   return if (areContentsTheSame(o1,o2) || areItemsTheSame(o1,o2))  {
                       0
                   } else if (itemsInOrder.first().getDisplayTitle()
                       == o1.getDisplayTitle()) {
                       -1
                    } else {
                       1
                    }
                }
        })
        dataset.addAll(initialInput)
    }

    override fun getItemViewType(position: Int): Int {
        return dataset[position].getType()
    }

    override fun getItemCount(): Int {
        return dataset.size()
    }

    override fun onBindViewHolder(holder: FriendsFragment.FriendItemViewHolder, position: Int) {
        val data = dataset[position]
        val firstLetter = data.getDisplayTitle().first().toString()
        mCallback.checkIfNewFirstLetter(firstLetter)
        holder.bindData(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : FriendsFragment.FriendItemViewHolder {
        return if (viewType == FriendsListItem.TYPE_FRIEND) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.friend_item, parent, false)
                FriendViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.letter_item, parent, false)
                LetterViewHolder(view)
        }
    }
}