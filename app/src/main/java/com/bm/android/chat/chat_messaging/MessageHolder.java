package com.bm.android.chat.chat_messaging;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bm.android.chat.R;
import com.bm.android.chat.chat_messaging.ChatMessage;
import com.bm.android.chat.user_access.models.ReceivedFriendRequest;

public class MessageHolder extends RecyclerView.ViewHolder {
//    private TextView message;
    private TextView senderText;
    private TextView timeSentText;

    public MessageHolder(final View itemView) {
        super(itemView);
//        message = (TextView)itemView;
        senderText = itemView.findViewById(R.id.senderName);
        timeSentText = itemView.findViewById(R.id.time_received);
    }

    public void bindData(ReceivedFriendRequest chat) {
        senderText.setText(chat.getSenderUsername());
        timeSentText.setText(chat.getTimeRequestSent().toString());
    }
}
