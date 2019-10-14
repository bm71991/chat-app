package com.bm.android.chat.chat_messaging;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bm.android.chat.chat_messaging.ChatMessage;

public class MessageHolder extends RecyclerView.ViewHolder {
    private TextView message;

    public MessageHolder(final View itemView) {
        super(itemView);
        message = (TextView)itemView;
    }

    public void bindData(ChatMessage chat) {
        message.setText(chat.getMessage());
    }
}
