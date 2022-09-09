package com.mustafaunlu.basicchatapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mustafaunlu.basicchatapp.R;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private ArrayList<String> messageArrayList;

    public ChatAdapter(ArrayList<String> messageArrayList) {
        this.messageArrayList = messageArrayList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row,parent,false);
        return new ChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.messageText.setText(messageArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size()   ;
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder{

        TextView messageText;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText=itemView.findViewById(R.id.messageTextView);
        }
    }
}
