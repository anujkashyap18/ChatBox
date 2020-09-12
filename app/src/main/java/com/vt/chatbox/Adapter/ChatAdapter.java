package com.vt.chatbox.Adapter;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.vt.chatbox.Model.ChatData;
import com.vt.chatbox.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {

	Context context;
	List<ChatData> chatData;
	String currentuser, type;
	String lat, lon, imgurl;

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

		type = chatData.get(position).getType();


		if (currentuser.equals(chatData.get(position).getSender())) {
			holder.sender.setText(chatData.get(position).getMessage());
			holder.senderTime.setText(currenttime);
			holder.showReciever.setVisibility(View.GONE);
			holder.recieverTime.setVisibility(View.GONE);
			holder.recieverlayout.setVisibility(View.GONE);
			holder.locationView.setVisibility(View.GONE);

			if (type.equals("userLocation")) {
				holder.sender.setVisibility(View.GONE);
				holder.locationView.setVisibility(View.VISIBLE);
				String[] split = chatData.get(position).getMessage().split(":");
				String mapImg = chatData.get(position).getImage();
//				holder.latitude.setText(split[0]);
//				holder.longitude.setText(split[1]);

				lat = split[0];
				lon = split[1];

				Picasso.get().load(mapImg).fit().centerCrop().into(holder.mapView);

				holder.locationView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Uri uri = Uri.parse("google.navigation:q=" + lat + "," + lon);
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						intent.setPackage("com.google.android.apps.maps");
						try {
							context.startActivity(intent);
						} catch (ActivityNotFoundException ex) {
							try {
								Intent it = new Intent(Intent.ACTION_VIEW, uri);
								context.startActivity(it);
							} catch (ActivityNotFoundException innerEx) {

								Toast.makeText(context, "please install a map application", Toast.LENGTH_SHORT).show();
							}
						}

					}
				});
			}

		} else if (chatData.get(position).getReciever().equals("group")) {
			holder.reciever.setText(chatData.get(position).getMessage());
			holder.recieverTime.setText(currenttime);
			holder.showReciever.setVisibility(View.VISIBLE);
			holder.showReciever.setText(chatData.get(position).getSender());
			holder.senderlayout.setVisibility(View.GONE);
			holder.senderTime.setVisibility(View.GONE);
			holder.locationView.setVisibility(View.GONE);
		} else {
			String[] split = chatData.get(position).getMessage().split(":");
			String mapImg = chatData.get(position).getImage();
//				holder.latitude.setText(split[0]);
//				holder.longitude.setText(split[1]);

			lat = split[0];
			lon = split[1];


			holder.reciever.setText(chatData.get(position).getMessage());
			holder.recieverTime.setText(currenttime);
			Picasso.get().load(mapImg).fit().centerCrop().into(holder.recieverMap);

			//holder.showReciever.setText(chatData.get(position).getSender());
			holder.senderlayout.setVisibility(View.VISIBLE);
			holder.sender.setVisibility(View.GONE);
			holder.senderTime.setVisibility(View.GONE);

			holder.locationView1.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {

					Uri uri = Uri.parse("google.navigation:q=" + lat + "," + lon);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					intent.setPackage("com.google.android.apps.maps");
					try {
						context.startActivity(intent);
					} catch (ActivityNotFoundException ex) {
						try {
							Intent it = new Intent(Intent.ACTION_VIEW, uri);
							context.startActivity(it);
						} catch (ActivityNotFoundException innerEx) {

							Toast.makeText(context, "please install a map application", Toast.LENGTH_SHORT).show();
						}
					}
				}
			});
		}
	}

	@Override
	public int getItemCount() {
		return chatData.size();
	}

	class ChatHolder extends RecyclerView.ViewHolder {
		TextView sender, senderTime, reciever, recieverTime, showReciever, latitude, longitude;
		ConstraintLayout locationView, locationView1;
		LinearLayout senderlayout, recieverlayout;
		ImageView mapView, recieverMap;

		public ChatHolder(@NonNull View itemView) {
			super(itemView);
			sender = itemView.findViewById(R.id.sender);
			senderTime = itemView.findViewById(R.id.sender_time);
			reciever = itemView.findViewById(R.id.reciever);
			recieverTime = itemView.findViewById(R.id.reciever_time);
			senderlayout = itemView.findViewById(R.id.senderlayout);
			recieverlayout = itemView.findViewById(R.id.recieverlayout);
			showReciever = itemView.findViewById(R.id.show_reciever);
			locationView = itemView.findViewById(R.id.location_view);
			locationView1 = itemView.findViewById(R.id.location_view1);
			mapView = itemView.findViewById(R.id.map_view);
			recieverMap = itemView.findViewById(R.id.reciever_map_view);
//			latitude = itemView.findViewById(R.id.text_latitude);
//			longitude = itemView.findViewById(R.id.text_longitude);
		}
	}
}
