package com.vt.chatbox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
	
	TextView textViewUserName, textViewName, textViewPassword, textViewEmail;
	CircleImageView circleImageView;
	ImageView imageView,back;
	Uri imageUri;
	String names, imgurl;
	Button logoutProfile;
	FirebaseStorage srefs;
	StorageReference refs;
	FirebaseDatabase databases;
	DatabaseReference databaseReferences;
	
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
		Window window = this.getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent));
		View decor = getWindow().getDecorView();
		decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
		
		setContentView(R.layout.activity_profile);
		
		textViewUserName = findViewById(R.id.textViewUserName);
		textViewName = findViewById(R.id.textViewName_profile);
		textViewPassword = findViewById(R.id.textViewPassword_profile);
		textViewEmail = findViewById(R.id.textViewEmail_profile);
		circleImageView = findViewById(R.id.profile_upload_image);
		imageView = findViewById(R.id.load_image_profile);
		
		back = findViewById(R.id.imageBack);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick( View view ) {
				onBackPressed();
			}
		});

//		logoutProfile = findViewById(R.id.logout_profile);
//		logoutProfile.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
//				intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
//				startActivity(intent);
//				finish();
//			}
//		});

		
		SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
		names = sp.getString("name", "");
		
		srefs = FirebaseStorage.getInstance();
		refs = srefs.getReference("users/" + names + "/profileimg");
		databases = FirebaseDatabase.getInstance();
		databaseReferences = databases.getReference("users");
		
		if ( ! names.equals("") ) {
			databaseReferences.child(names).addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange( @NonNull DataSnapshot snapshot ) {
					textViewName.setText(snapshot.child("userName").getValue().toString());
					textViewEmail.setText(snapshot.child("email").getValue().toString());
					textViewPassword.setText(snapshot.child("password").getValue().toString());
					imgurl = snapshot.child("image").getValue().toString();
					if ( ! imgurl.equals("") ) {
						Picasso.get().load(imgurl).into(circleImageView);
					}
				}

				@Override
				public void onCancelled( @NonNull DatabaseError error ) {

				}
			});
		}
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick( View view ) {
				Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent1, 1000);
			}
		});
		
		
	}
	
	@Override
	protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
		super.onActivityResult(requestCode, resultCode, data);
		if ( requestCode == 1000 ) {
			try {
				if ( data.getData() != null ) {
					imageUri = data.getData();
					upload();
				}
			} catch ( Exception e ) {
				Log.d(getClass().getSimpleName(), "Exception : " + e.getMessage());
			}
		}
	}
	
	public void upload() {
		UploadTask furef = refs.putFile(imageUri);
		furef.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
			@Override
			public void onSuccess( UploadTask.TaskSnapshot taskSnapshot ) {
				refs.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
					@Override
					public void onSuccess( Uri uri ) {
						String url = String.valueOf(uri);
						databaseReferences.child(names).child("image").setValue(url);
						Toast.makeText(ProfileActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
						Log.d("kjhdj", "jkhjk" + url);
					}
				});
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure( @NonNull Exception e ) {

			}
		});

	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}