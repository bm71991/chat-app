package com.bm.android.chat.chat_messaging;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bm.android.chat.R;
import com.bm.android.chat.chat_messaging.ChatMessage;
import com.bm.android.chat.chat_messaging.MessageHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class StartFragment extends Fragment {
    private String TAG = "mainLog";
    private FirestoreRecyclerAdapter adapter;
    private RecyclerView chatList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)  {
        View v = inflater.inflate(R.layout.fragment_start, parent, false);
        Query query = FirebaseFirestore.getInstance()
                .collection("chats")
                .document("convo1")
                .collection("thread");

        chatList = v.findViewById(R.id.chat_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        chatList.setLayoutManager(layoutManager);

        FirestoreRecyclerOptions<ChatMessage> options =
                new FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<ChatMessage, MessageHolder>(options) {
            @Override
            public void onBindViewHolder(MessageHolder holder, int position, ChatMessage model) {
//                holder.bindData(model);
            }

            @Override
            public MessageHolder onCreateViewHolder(ViewGroup group, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.message, group, false);

                return new MessageHolder(view);
            }

            @Override
            public void onError(@NonNull FirebaseFirestoreException e) {
                super.onError(e);
                Log.d(TAG, e.toString());
            }
        };

        adapter.notifyDataSetChanged();
        chatList.setAdapter(adapter);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
