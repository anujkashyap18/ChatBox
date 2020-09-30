package com.vt.chatbox.Adapter;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;
import com.vt.chatbox.Model.ChatData;
import com.vt.chatbox.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter < ChatAdapter.ChatHolder > {

	Context context;
	List < ChatData > chatData;
	String currentuser, type;
	String lat, lon;

	public ChatAdapter ( Context context , List < ChatData > chatData , String currentuser ) {
		this.context = context;
		this.chatData = chatData;
		this.currentuser = currentuser;
	}

	@NonNull
	@Override
	public ChatHolder onCreateViewHolder ( @NonNull ViewGroup parent , int viewType ) {
		LayoutInflater inflater = LayoutInflater.from ( context );
		View view = inflater.inflate ( R.layout.custom_chat , parent , false );
		return new ChatHolder ( view );
	}

	@Override
	public void onBindViewHolder ( @NonNull ChatHolder holder , int position ) {

		@SuppressLint ( "SimpleDateFormat" )
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat ( "hh:mm a" );
		String currenttime;
		Date date = new Date ( );
		date.setTime ( Long.parseLong ( chatData.get ( position ).getTime ( ) ) );
		currenttime = simpleDateFormat.format ( date.getTime ( ) );
		type = chatData.get ( position ).getType ( );

		if ( currentuser.equals ( chatData.get ( position ).getSender ( ) ) ) {

			if ( type.equals ( "userLocation" ) ) {
				holder.sender.setVisibility ( View.GONE );
				holder.locationView.setVisibility ( View.VISIBLE );
				holder.reciever.setText ( chatData.get ( position ).getReciever ( ) );
				holder.reciever.setVisibility ( View.GONE );
				holder.senderTime.setText ( currenttime );
				String[] split = chatData.get ( position ).getMessage ( ).split ( ":" );
				String mapImg = chatData.get ( position ).getImage ( );
				lat = split[ 0 ];
				lon = split[ 1 ];
				holder.reciever.setVisibility ( View.GONE );
				Picasso.get ( ).load ( mapImg ).fit ( ).centerCrop ( ).into ( holder.mapView );
				holder.locationView.setOnClickListener ( new View.OnClickListener ( ) {
					@Override
					public void onClick ( View view ) {
						Uri uri = Uri.parse ( "google.navigation:q=" + lat + "," + lon );
						Intent intent = new Intent ( Intent.ACTION_VIEW , uri );
						intent.setPackage ( "com.google.android.apps.maps" );
						try {
							context.startActivity ( intent );
						} catch ( ActivityNotFoundException ex ) {
							try {
								Intent it = new Intent ( Intent.ACTION_VIEW , uri );
								context.startActivity ( it );
							} catch ( ActivityNotFoundException innerEx ) {

								Toast.makeText ( context , "please install a map application" , Toast.LENGTH_SHORT ).show ( );
							}
						}
					}
				} );
			}
			else if ( type.equals ( "image" ) ) {
				holder.showReciever.setVisibility ( View.GONE );
				holder.sender.setVisibility ( View.GONE );
				holder.recieverTime.setVisibility ( View.GONE );
				holder.locationView.setVisibility ( View.VISIBLE );
				holder.recieverlayout.setVisibility ( View.GONE );
				holder.mapView.setVisibility ( View.VISIBLE );
				holder.senderTime.setText ( currenttime );

				if ( chatData.get ( position ).getMessage ( ).equals ( "image/jpg" ) || chatData.get ( position ).getMessage ( ).equals ( "image/jpeg" ) || chatData.get ( position ).getMessage ( ).equals ( "image/png" ) ) {
					Picasso.get ( ).load ( chatData.get ( position ).getImage ( ) ).error ( R.drawable.ic_baseline_call_24 ).into ( holder.mapView );
					holder.mapView.setScaleType ( ImageView.ScaleType.CENTER_CROP );
					Log.d ( getClass ( ).getSimpleName ( ) , "IMAGE SELECTED" );
				}
				else {
					String videoFile = chatData.get ( position ).getImage ( );
					Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail ( videoFile , MediaStore.Images.Thumbnails.MINI_KIND );
					holder.mapView.setImageBitmap ( thumbnail );
				}
			}
			else {
				holder.sender.setText ( chatData.get ( position ).getMessage ( ) );
				holder.senderTime.setText ( currenttime );
				holder.showReciever.setVisibility ( View.GONE );
				holder.recieverTime.setVisibility ( View.GONE );
				holder.recieverlayout.setVisibility ( View.GONE );
				holder.locationView.setVisibility ( View.GONE );
			}

		}

		else if ( chatData.get ( position ).getReciever ( ).equals ( "group" ) ) {
			holder.reciever.setText ( chatData.get ( position ).getMessage ( ) );
			holder.recieverTime.setText ( currenttime );
			holder.showReciever.setVisibility ( View.VISIBLE );
			holder.showReciever.setText ( chatData.get ( position ).getSender ( ) );
			holder.senderlayout.setVisibility ( View.GONE );
			holder.senderTime.setVisibility ( View.GONE );
			holder.locationView.setVisibility ( View.GONE );
		}

		else {

			if ( type.equals ( "userLocation" ) ) {


				String[] split = chatData.get ( position ).getMessage ( ).split ( ":" );
				String mapImgs = chatData.get ( position ).getImage ( );

				lat = split[ 0 ];
				lon = split[ 1 ];


//				holder.reciever.setText ( chatData.get ( position ).getMessage ( ) );
				holder.recieverTime.setText ( currenttime );
				holder.locationView1.setVisibility ( View.VISIBLE );
				Picasso.get ( ).load ( mapImgs ).fit ( ).centerCrop ( ).into ( holder.recieverMap );

				holder.senderlayout.setVisibility ( View.VISIBLE );
				holder.sender.setVisibility ( View.GONE );
				holder.senderTime.setVisibility ( View.GONE );

				holder.locationView1.setOnClickListener ( new View.OnClickListener ( ) {
					@Override
					public void onClick ( View view ) {

						Uri uri = Uri.parse ( "google.navigation:q=" + lat + "," + lon );
						Intent intent = new Intent ( Intent.ACTION_VIEW , uri );
						intent.setPackage ( "com.google.android.apps.maps" );
						try {
							context.startActivity ( intent );
						} catch ( ActivityNotFoundException ex ) {
							try {
								Intent it = new Intent ( Intent.ACTION_VIEW , uri );
								context.startActivity ( it );
							} catch ( ActivityNotFoundException innerEx ) {

								Toast.makeText ( context , "please install a map application" , Toast.LENGTH_SHORT ).show ( );
							}
						}
					}
				} );
			}
			else if ( type.equals ( "image" ) ) {
				holder.showReciever.setVisibility ( View.VISIBLE );
				holder.sender.setVisibility ( View.GONE );
				holder.showReciever.setText ( chatData.get ( position ).getSender ( ) );
				holder.recieverTime.setVisibility ( View.VISIBLE );
				holder.locationView1.setVisibility ( View.VISIBLE );
				holder.recieverlayout.setVisibility ( View.VISIBLE );
				holder.recieverMap.setVisibility ( View.VISIBLE );
				holder.recieverTime.setText ( currenttime );

				if ( chatData.get ( position ).getMessage ( ).equals ( "image/jpg" ) || chatData.get ( position ).getMessage ( ).equals ( "image/jpeg" ) || chatData.get ( position ).getMessage ( ).equals ( "image/png" ) ) {
					Picasso.get ( ).load ( chatData.get ( position ).getImage ( ) ).error ( R.drawable.ic_baseline_call_24 ).into ( holder.recieverMap );
					holder.recieverMap.setScaleType ( ImageView.ScaleType.CENTER_CROP );
					Log.d ( getClass ( ).getSimpleName ( ) , "IMAGE SELECTED" );
				}
				else {
					String videoFile = chatData.get ( position ).getImage ( );
					Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail ( videoFile , MediaStore.Images.Thumbnails.MINI_KIND );
					holder.mapView.setImageBitmap ( thumbnail );
				}
			}
			else {
				holder.reciever.setText ( chatData.get ( position ).getMessage ( ) );
				holder.showReciever.setVisibility ( View.VISIBLE );
				holder.showReciever.setText ( chatData.get ( position ).getSender ( ) );
				holder.recieverTime.setVisibility ( View.VISIBLE );
				holder.recieverlayout.setVisibility ( View.VISIBLE );
				holder.locationView1.setVisibility ( View.GONE );
				holder.senderlayout.setVisibility ( View.GONE );
			}
		}
	}

	@Override
	public int getItemCount ( ) {
		return chatData.size ( );
	}

	static class ChatHolder extends RecyclerView.ViewHolder {
		TextView sender, senderTime, reciever, recieverTime, showReciever;
		MaterialCardView locationView, locationView1;
		ConstraintLayout recieverlayout, senderlayout;
		ImageView mapView, recieverMap;

		public ChatHolder ( @NonNull View itemView ) {
			super ( itemView );
			sender = itemView.findViewById ( R.id.sender );
			senderTime = itemView.findViewById ( R.id.sender_time );
			reciever = itemView.findViewById ( R.id.reciever );
			recieverTime = itemView.findViewById ( R.id.reciever_time );
			senderlayout = itemView.findViewById ( R.id.senderlayout );
			recieverlayout = itemView.findViewById ( R.id.recieverlayout );
			showReciever = itemView.findViewById ( R.id.show_reciever );
			locationView = itemView.findViewById ( R.id.location_view );
			locationView1 = itemView.findViewById ( R.id.location_view1 );
			mapView = itemView.findViewById ( R.id.map_view );
			recieverMap = itemView.findViewById ( R.id.reciever_map_view );
		}
	}
}
