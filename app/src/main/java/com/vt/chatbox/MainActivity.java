package com.vt.chatbox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
	TextInputEditText editTextPassword;
	TextInputEditText editTextEmail;
	MaterialButton buttonLogin;
	TextView textViewRegister;
	// ProgressBar progressBar;
	FirebaseAuth fAuth;
	FirebaseDatabase firebaseDatabase;
	DatabaseReference databaseReference;
	ConstraintLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Window window = this.getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent));
		View decor = getWindow().getDecorView();
		decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

		setContentView(R.layout.activity_main);
		SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
		String nam = sp.getString("name", "");
		if (!nam.equals("")) {
			Intent it = new Intent(MainActivity.this, HomeActivity.class);
			startActivity(it);
			finish();
		}
		firebaseDatabase = FirebaseDatabase.getInstance();
		databaseReference = firebaseDatabase.getReference("users");

		editTextEmail = findViewById(R.id.editTextEmail);
		editTextPassword = findViewById(R.id.editTextPassword);
		buttonLogin = findViewById(R.id.buttonLogin);
//        progressBar = findViewById(R.id.progressBar);
		layout = findViewById(R.id.linearLayout);
		layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				InputMethodManager inputMethodManagerManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

				inputMethodManagerManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
			}
		});
		fAuth = FirebaseAuth.getInstance();

//

		textViewRegister = findViewById(R.id.textViewRegister);
		textViewRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, RegisterActivity.class));
			}
		});

		buttonLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String email = editTextEmail.getText().toString().trim();
				final String password = editTextPassword.getText().toString().trim();

				if (TextUtils.isEmpty(email)) {
					editTextEmail.setError("email is Required");
				} else if (TextUtils.isEmpty(password)) {
					editTextPassword.setError("password is Required");
				} else if (editTextPassword.length() < 6) {
					editTextPassword.setError("password must be 6 characters");
				} else {
					fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
						@Override
						public void onComplete(@NonNull Task<AuthResult> task) {
							if (task.isSuccessful()) {
								databaseReference.addValueEventListener(new ValueEventListener() {
									@Override
									public void onDataChange(@NonNull DataSnapshot snapshot) {
										String name1 = null;
										Log.d(getClass().getSimpleName(), "SNAP : " + snapshot.getValue().toString());
										for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
											if (!dataSnapshot.getKey().equals("group")) {

												if (dataSnapshot.child("email").getValue().toString().equals(email)) {
													name1 = dataSnapshot.child("userName").getValue().toString();
												}
											}
										}
										Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
										SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
										sharedPreferences.edit().putString("name", name1).apply();

										Intent intent = new Intent(MainActivity.this, HomeActivity.class);
										startActivity(intent);
										finish();
									}

									@Override
									public void onCancelled(@NonNull DatabaseError error) {

									}
								});
							} else {
								Toast.makeText(MainActivity.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                progressBar.setVisibility(View.GONE);
							}
						}
					});
				}
			}

		});
	}
}
