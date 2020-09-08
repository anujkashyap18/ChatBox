package com.vt.chatbox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vt.chatbox.Adapter.ChatListAdapter;
import com.vt.chatbox.Model.ChatListData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

	TextView textViewUserName;
	Uri imageUri;
	Toolbar logout_tool;
	String name, imgurl;
	FirebaseStorage sref;
	StorageReference ref;
	FirebaseDatabase database;
	DatabaseReference databaseReference;
	ChatListAdapter chatListAdapter;
	RecyclerView recyclerView;
	ProgressBar progressBar;
	List<ChatListData> data = new ArrayList<>();

	private AlertDialog.Builder alertDialogBuilder;
	private AlertDialog dialog;
	private LayoutInflater inflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Window window = this.getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent));
		View decor = getWindow().getDecorView();
		decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

		setContentView(R.layout.activity_home);

		textViewUserName = findViewById(R.id.textViewUserName);

		logout_tool = findViewById(R.id.logout_tool);
		recyclerView = findViewById(R.id.chat_recyclerview);
		progressBar = findViewById(R.id.progress);

		setSupportActionBar(logout_tool);
		Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

		SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
		name = sp.getString("name", "");
		textViewUserName.setText("Welcome   \t\t\t\t\t\t\t" + name);

		sref = FirebaseStorage.getInstance();
		ref = sref.getReference("users/" + name + "/profileimg");
		database = FirebaseDatabase.getInstance();
		databaseReference = database.getReference("users");

		getAllUser();


	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.log_out:

				SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
				sp.edit().remove("name").apply();

				startActivity(new Intent(HomeActivity.this, MainActivity.class));
				finish();
				break;

			case R.id.showall:
				createGroup();
				break;

			case R.id.profile:
				startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
				break;
//
//			case R.id.Edit_password:
//				changePassword();
//				break;
		}
		return super.onOptionsItemSelected(item);
	}


	private void changePassword() {

		alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
		inflater = LayoutInflater.from(HomeActivity.this);
		final View view = inflater.inflate(R.layout.popup_password, null);
		final EditText editTextPasswordPopup = view.findViewById(R.id.editTextPasswordPopup);
		final EditText editTextConfPasswordPopup = view.findViewById(R.id.editTextConfPasswordPopup);

		alertDialogBuilder.setView(view);
		dialog = alertDialogBuilder.create();
		dialog.show();
	}

	private void editInfo() {
		alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
		inflater = LayoutInflater.from(HomeActivity.this);
		final View view = inflater.inflate(R.layout.popup, null);
		final EditText editTextUsername = view.findViewById(R.id.editTextUsername);
		final EditText editTextEmail = view.findViewById(R.id.editTextEmail);

		alertDialogBuilder.setView(view);
		dialog = alertDialogBuilder.create();
		dialog.show();
	}

	public void createGroup() {

		alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
		inflater = LayoutInflater.from(HomeActivity.this);
		final View view = inflater.inflate(R.layout.popup_groupchat, null);

		alertDialogBuilder.setView(view);
		dialog = alertDialogBuilder.create();
		dialog.show();

		final EditText createGroup = view.findViewById(R.id.editTextPasswordPopup);

		final Button saveButton = view.findViewById(R.id.saveButtonPassword);

		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Map<String, String> hm = new HashMap<String, String>();
				hm.put("email", name);
				hm.put("id", "");
				hm.put("image", "group");
				hm.put("password", "");
				hm.put("token", "");
				hm.put("userName", createGroup.getText().toString());
				databaseReference.child("group").child(createGroup.getText().toString()).setValue(hm);
				dialog.dismiss();
			}
		});
	}

	public void getAllUser() {

		databaseReference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				data.clear();
				for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
					String nam, imgurl, fToken, email;
					long t = System.currentTimeMillis();
					Log.d(getClass().getSimpleName(), "time : " + +t);
					String key = dataSnapshot.getKey();
					Log.d(getClass().getSimpleName(), "mohit bhai ko : " + key);
					if (!key.equals("group")) {
						nam = dataSnapshot.child("userName").getValue().toString();
						fToken = dataSnapshot.child("token").getValue().toString();
						imgurl = dataSnapshot.child("image").getValue().toString();
						email = dataSnapshot.child("email").getValue().toString();
						if (!name.equals(nam)) {
							data.add(new ChatListData(nam, imgurl, String.valueOf(t), fToken, email));
						}
					} else {
						for (DataSnapshot dd : dataSnapshot.getChildren()) {
							data.add(new ChatListData(dd.child("userName").getValue().toString(),
									dd.child("image").getValue().toString(),
									String.valueOf(t), " ",
									dd.child("email").getValue().toString()));
						}
					}
				}
				chatListAdapter = new ChatListAdapter(HomeActivity.this, data, name);
				recyclerView.setAdapter(chatListAdapter);
				progressBar.setVisibility(View.GONE);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {

			}
		});
	}
}