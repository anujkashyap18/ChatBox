package com.vt.chatbox;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
	
	ImageView splash;
	
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		splash = findViewById(R.id.splash);

//		FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
//
//		Job myJob = dispatcher.newJobBuilder()
//				.setService(MyJobService.class)
//				.setTag("my-unique-tag")
//				.build();
//
//		dispatcher.mustSchedule(myJob);
//
//		FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
//			@Override
//			public void onSuccess( InstanceIdResult instanceIdResult ) {
//				instanceIdResult.getToken();
//				Log.d("dgsdggd", "anujkas : " + instanceIdResult.getToken());
//			}
//		});
		
		splash.animate().alpha(1f).setDuration(3000);
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(SplashActivity.this, MainActivity.class));
				finish();
			}
		}, 5000);
	}
}