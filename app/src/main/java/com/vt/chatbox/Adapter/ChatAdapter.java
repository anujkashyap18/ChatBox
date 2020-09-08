package com.vt.chatbox.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.vt.chatbox.Model.ChatData;
import com.vt.chatbox.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {

	Context context;
	List<ChatData> chatData;
	String currentuser;

	public ChatAdapter(Context context, List<ChatData> chatData, String currentuser) {
		this.context = context;
		this.chatData = chatData;
		this.currentuser = currentuser;
	}

	@NonNull
	@Override
	public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.custom_chat, parent, false);
		return new ChatHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ChatHolder holder, int position) {

		@SuppressLint("SimpleDateFormat")
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
		String currenttime;
		Date date = new Date();
		date.setTime(Long.parseLong(chatData.get(position).getTime()));
		currenttime = simpleDateFormat.format(date.getTime());
//		Toast.makeText(context, ""+chatData.get(position).getSender(), Toast.LENGTH_SHORT).show();
		if (currentuser.equals(chatData.get(position).getSender())) {
			holder.sender.setText(chatData.get(position).getMessage());
			holder.senderTime.setText(currenttime);
			holder.showReciever.setVisibility(View.GONE);
			holder.recieverTime.setVisibility(View.GONE);
			holder.recieverlayout.setVisibility(View.GONE);
		} else if (chatData.get(position).getReciever().equals("group")) {
			holder.reciever.setText(chatData.get(position).getMessage());
			holder.recieverTime.setText(currenttime);
			holder.showReciever.setVisibility(View.VISIBLE);
			holder.showReciever.setText(chatData.get(position).getSender());
			holder.senderlayout.setVisibility(View.GONE);
			holder.senderTime.setVisibility(View.GONE);
		} else {
			holder.reciever.setText(chatData.get(position).getMessage());
			holder.recieverTime.setText(currenttime);
			//holder.showReciever.setText(chatData.get(position).getSender());
			holder.senderlayout.setVisibility(View.GONE);
			holder.senderTime.setVisibility(View.GONE);
		}
	}

	@Override
	public int getItemCount() {
		return chatData.size();
	}

	class ChatHolder extends RecyclerView.ViewHolder {
		TextView sender, senderTime, reciever, recieverTime, showReciever;
		ConstraintLayout senderlayout, recieverlayout;

		public ChatHolder(@NonNull View itemView) {
			super(itemView);
			sender = itemView.findViewById(R.id.sender);
			senderTime = itemView.findViewById(R.id.sender_time);
			reciever = itemView.findViewById(R.id.reciever);
			recieverTime = itemView.findViewById(R.id.reciever_time);
			senderlayout = itemView.findViewById(R.id.senderlayout);
			recieverlayout = itemView.findViewById(R.id.recieverlayout);
			showReciever = itemView.findViewById(R.id.show_reciever);
		}
	}
}
