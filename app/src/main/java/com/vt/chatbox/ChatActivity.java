package com.vt.chatbox;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.vt.chatbox.Adapter.ChatAdapter;
import com.vt.chatbox.Adapter.GroupAdapter;
import com.vt.chatbox.Interface.Mention;
import com.vt.chatbox.Model.ChatData;
import com.vt.chatbox.Model.User;
import com.vt.chatbox.Notification.MyFirebaseMessagingService;

import net.nightwhistler.htmlspanner.HtmlSpanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class ChatActivity extends AppCompatActivity {

	EditText textMessage;
	TextView tname;
	ImageView send, userDp, back;
	String message, cur_name, recieverEmail;
	RecyclerView chatRecyclerView, groupRecyclerView;
	FirebaseAuth mauth;
	DatabaseReference r_db;
	String recievername, revieverimg, tokens;
	ChatAdapter chatAdapter;
	GroupAdapter groupAdapter;
	List<ChatData> chat_data = new ArrayList<>();
	List<User> users = new ArrayList<>();
	NestedScrollView scrollView;
	RecyclerView.LayoutManager chatlayoutManager;
	boolean notify = false;
	MyFirebaseMessagingService myFirebaseMessagingService;
	List<String> groupUsers = new ArrayList<>();
	HashSet<String> hashSet = new HashSet<String>();
	RecyclerView.LayoutManager groupLayoutManager;
	View view;
	Mention mentions;
	FirebaseStorage sref;
	StorageReference ref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		textMessage = findViewById(R.id.type_message);
		textMessage.setSelection(textMessage.getText().length());
		userDp = findViewById(R.id.imageView2);
		tname = findViewById(R.id.ChatuserView);
		scrollView = findViewById(R.id.scroll);
		chatRecyclerView = findViewById(R.id.chat_recyclerview);
		view = findViewById(R.id.profileClick);


		groupRecyclerView = findViewById(R.id.group_chat_recyclerview);

		myFirebaseMessagingService = new MyFirebaseMessagingService();


		Intent it = getIntent();
		recievername = it.getStringExtra("name");
		revieverimg = it.getStringExtra("img");
		tokens = it.getStringExtra("token");
		recieverEmail = it.getStringExtra("email");

		Log.d(getClass().getSimpleName(), "TOKENS : " + tokens);

		findViewById(R.id.location).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(ChatActivity.this, LocationActivity.class);
				intent.putExtra("location", recievername);
//				Toast.makeText(ChatActivity.this, "groups"+recievername, Toast.LENGTH_SHORT).show();
				intent.putExtra("currentUser", cur_name);
				intent.putExtra("recieverimg", revieverimg);

				startActivity(intent);

			}
		});

		FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
			@Override
			public void onSuccess(InstanceIdResult instanceIdResult) {
				instanceIdResult.getToken();
				Log.d("dgsdggd", "anujkas : " + instanceIdResult.getToken());
			}
		});

		tname.setText(recievername);
		try {
			Picasso.get().load(revieverimg).into(userDp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
		cur_name = sp.getString("name", "");

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(ChatActivity.this, UserProfileActivity.class);
				intent.putExtra("name", recievername);
				intent.putExtra("email", recieverEmail);
				intent.putExtra("image", revieverimg);
				startActivity(intent);
			}
		});

		chatlayoutManager = new LinearLayoutManager(this);
		chatRecyclerView.setLayoutManager(chatlayoutManager);

		mauth = FirebaseAuth.getInstance();
		r_db = FirebaseDatabase.getInstance().getReference("chatdatabase");

		sref = FirebaseStorage.getInstance();
		ref = sref.getReference("location/" + recievername + "/profileimg");


		back = findViewById(R.id.imageBack);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});
		send = findViewById(R.id.send);

		textMessage.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				Log.d(getClass().getSimpleName(), "bb ki : ");
				String ch = "" + charSequence;
				String last = "";
				try {
					last = ch.substring(charSequence.length() - 1);
				} catch (Exception e) {

				}


				if (last.equals("@")) {
					groupRecyclerView.setVisibility(View.VISIBLE);
				} else {
					groupRecyclerView.setVisibility(View.GONE);
				}

			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});

		groupLayoutManager = new LinearLayoutManager(this);
		groupRecyclerView.setLayoutManager(groupLayoutManager);
		show();

		mentions = new Mention() {
			@Override
			public void itemClicked(int position, String userName) {

				textMessage.setText(String.format("%s%s", textMessage.getText().toString().trim(),
						new HtmlSpanner().fromHtml("<a style=\"color:#ff1333;\">" + userName + "</a>")));
				textMessage.setSelection(textMessage.getText().length());
			}
		};

		if (revieverimg.equals("group")) {
			groupAdapter = new GroupAdapter(ChatActivity.this, groupUsers, mentions);
			groupRecyclerView.setAdapter(groupAdapter);
		}

		send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				notify = true;
				if (!textMessage.getText().toString().trim().equals("")) {
					String id = r_db.push().getKey();
					message = textMessage.getText().toString().trim();
					long t = System.currentTimeMillis();
					Map<String, String> hm = new HashMap<>();

					hm.put("message", message);
					hm.put("sender", cur_name);
					hm.put("time", String.valueOf(t));
					hm.put("type", "text");
					hm.put("images", "null");
					if (revieverimg.equals("group")) {

						hm.put("reciever", "group");
						r_db.child("group").child(recievername + "-chat").child(id).setValue(hm);
					} else {
						hm.put ( "reciever" , recievername );
						
						r_db.child ( cur_name ).child ( cur_name + "-chat-" + recievername ).child ( id ).setValue ( hm );
						r_db.child ( recievername ).child ( recievername + "-chat-" + cur_name ).child ( id ).setValue ( hm );
					}
					textMessage.setText ( "" );
					scrollView.fullScroll ( ScrollView.FOCUS_DOWN );
				}
			}
		} );
		
		
		if ( ContextCompat.checkSelfPermission ( ChatActivity.this ,
		                                         android.Manifest.permission.RECORD_AUDIO ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission ( ChatActivity.this , android.Manifest.permission.READ_PHONE_STATE ) != PackageManager.PERMISSION_GRANTED ) {
			ActivityCompat.requestPermissions (
					ChatActivity.this ,
					new String[] { android.Manifest.permission.RECORD_AUDIO ,
					               Manifest.permission.READ_PHONE_STATE } ,
					1
			                                  );
		}
		
		findViewById ( R.id.video ).setOnClickListener ( new View.OnClickListener ( ) {
			@Override
			public
			void onClick ( View view ) {
				Intent intent = new Intent ( ChatActivity.this , CallActivity.class );
				startActivity ( intent );
				
			}
		} );
		
	}

	public void show() {
		String key = "", key1 = "";
		if (revieverimg.equals("group")) {
			key1 = "group";
			key = recievername + "-chat";
		} else {
			key1 = cur_name;
			key = cur_name + "-chat-" + recievername;
		}
		r_db.child(key1).child(key).addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				chat_data.clear();
				String re_name = null, message = null;
				for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

					chat_data.add(new ChatData(
							dataSnapshot.child("message").getValue().toString(),
							dataSnapshot.child("sender").getValue().toString(),
							dataSnapshot.child("reciever").getValue().toString(),
							dataSnapshot.child("time").getValue().toString(),
							dataSnapshot.child("type").getValue().toString(),
							dataSnapshot.child("images").getValue().toString()));

					if (!cur_name.equals(dataSnapshot.child("sender").getValue().toString())) {
						groupUsers.add(dataSnapshot.child("sender").getValue().toString());
					}
					hashSet.addAll(groupUsers);
					groupUsers.clear();
					groupUsers.addAll(hashSet);
					//	}

					re_name = dataSnapshot.child("sender").getValue().toString();
					message = dataSnapshot.child("message").getValue().toString();
				}
				//if (groupUsers.size()>0) {
				Log.d(getClass().getSimpleName(), "USERS : " + groupUsers.size());
//				Toast.makeText(ChatActivity.this, "USERS : " + groupUsers, Toast.LENGTH_SHORT).show();
				//}

				volleyFcm(recievername, message);
				chatAdapter = new ChatAdapter(ChatActivity.this, chat_data, cur_name);
				chatRecyclerView.setAdapter(chatAdapter);

				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						scrollView.fullScroll(ScrollView.FOCUS_DOWN);
					}
				};
				scrollView.post(runnable);
				textMessage.requestFocus();
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {

			}
		});
	}

	public void volleyFcm(String name, String message) {
		Log.d("adsnhds", "volley error" + name);

		JSONObject object = new JSONObject();
		try {
			JSONArray ids = new JSONArray();
			ids.put(tokens);
			JSONObject params = new JSONObject();
			params.put("title", name);
			params.put("body", message);
			object.put("notification", params);
			object.put("registration_ids", ids);


		} catch (JSONException e) {
			e.printStackTrace();
		}

		Log.d("object", "parm" + object);
		JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", object,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
//				Toast.makeText(ChatActivity.this, ""+response, Toast.LENGTH_SHORT).show();
					}
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		}) {
			@Override
			public Map<String, String> getHeaders() {
				Map<String, String> header = new HashMap<>();
				header.put("Content-Type", "application/json");
				header.put("Authorization", "key=AAAARbPW1hM:APA91bE8nxTDoHbj1hkgxGzbwRl_l-mopmq5pBCrML6ukn7JEaIFX5zgueftWwjv42MgITU3qZixv3c3Caed9LFMIpguU1NQNsqrpaXVud2vmNA9HjH9u06PHDEk68977cZghe8vNR2M");
				return header;
			}
		};

		Volley.newRequestQueue(this).add(stringRequest);
	}

	@Override
	protected void onPause() {
		super.onPause();
//    	x=0;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}