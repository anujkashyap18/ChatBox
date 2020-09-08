package com.vt.chatbox.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.vt.chatbox.ChatActivity;
import com.vt.chatbox.Model.ChatListData;
import com.vt.chatbox.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListHolder>{

    Context context;
    String currentuser;
    List<ChatListData> chat;

    public ChatListAdapter(Context context, List<ChatListData> chat,String curUser) {
        this.context = context;
        this.chat = chat;
        currentuser=curUser;
    }

    @NonNull
    @Override
    public ChatListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_chatlist_item,parent,false);
        return new ChatListHolder(view);
    }

    @Override
    public void onBindViewHolder( @NonNull ChatListHolder holder, final int position) {
    
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        String currenttime;
        Date date = new Date();
        date.setTime(Long.parseLong(chat.get(position).getTime()));
        currenttime = simpleDateFormat.format(date.getTime());
        
            holder.name.setText(chat.get(position).getName());
            holder.time.setText(currenttime);
            if(chat.get(position).getImgurl().equals(""))
            {
            
            }else {
                Picasso.get().load(chat.get(position).getImgurl()).into(holder.image);
            }
        holder.mylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {

                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("name", chat.get(position).getName());
                intent.putExtra("img", chat.get(position).getImgurl());
                intent.putExtra("token", chat.get(position).getTokens());
                intent.putExtra("email", chat.get(position).getEmail());
                context.startActivity(intent);
            }
        });
        
    }

    @Override
    public int getItemCount() {
        return chat.size();
    }

    public class ChatListHolder extends RecyclerView.ViewHolder {
    
        CircleImageView image;
        TextView name, time;
        ConstraintLayout mylayout;
        public ChatListHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.user_img);
            name = itemView.findViewById(R.id.user_name);
            time = itemView.findViewById(R.id.user_time);
            mylayout = itemView.findViewById(R.id.main_layout);
        }
    }
}
