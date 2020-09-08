package com.vt.chatbox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

	CircleImageView userProfile;
	ImageView back;
	TextView email,userName;
	String userNames, userEmail, profile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Window window = this.getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent));
		View decor = getWindow().getDecorView();
		decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

		setContentView(R.layout.activity_user_profile);

		userProfile = findViewById(R.id.circleImageView);
		userName = findViewById(R.id.user_name);
		email = findViewById(R.id.email);
		userProfile =findViewById(R.id.circleImageView);
		back = findViewById(R.id.imageBack);

		Intent it = getIntent();
		userNames = it.getStringExtra("name");
		userEmail = it.getStringExtra("email");
		profile = it.getStringExtra("image");


		userName.setText(userNames);
		email.setText(userEmail);

		if (!profile.isEmpty()) {
					Picasso.get().load(profile).into(userProfile);
				}

		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}