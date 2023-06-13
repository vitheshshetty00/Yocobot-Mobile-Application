package com.example.aichatbot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends  RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    List<Message> messageList;
    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item,null);
        return new MyViewHolder(chatView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Message message = messageList.get(position);
        if(message.getSentBy().equals(Message.SENT_BY_ME)) {
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightTextView.setText(message.getMessage());
        } else {
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftTextView.setText(message.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public  class  MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout  leftChatLayout, rightChatLayout;
        TextView leftTextView, rightTextView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatLayout = itemView.findViewById(R.id.llLeftChat);
            rightChatLayout = itemView.findViewById(R.id.llRightChat);
            leftTextView = itemView.findViewById(R.id.tvLeftChat);
            rightTextView = itemView.findViewById(R.id.tvRightChat);
        }
    }
}
