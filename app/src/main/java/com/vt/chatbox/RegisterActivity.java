package com.vt.chatbox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.vt.chatbox.Model.User;

public class RegisterActivity extends AppCompatActivity {

	TextInputEditText editTextUsername, editTextEmail, editTextPassword, editTextCnfPassword;
	MaterialButton buttonRegister;
	TextView textViewLogin;
	FirebaseAuth fAuth;
	ProgressBar progressBar;
	DatabaseReference databaseReference;
	FirebaseDatabase firebaseDatabase;
	String userName, email, password, confirmPassword, token;
	ConstraintLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_register);
		editTextUsername = findViewById(R.id.editTextUsername);
		editTextEmail = findViewById(R.id.editTextEmail);
		editTextPassword = findViewById(R.id.editTextPassword);
		editTextCnfPassword = findViewById(R.id.editTextCnfPassword);
		buttonRegister = findViewById(R.id.buttonRegister);
		layout = findViewById(R.id.linearLayout);
		layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick( View view ) {
				InputMethodManager inputMethodManagerManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				
				inputMethodManagerManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
			}
		});
		
		fAuth = FirebaseAuth.getInstance();
		//	progressBar = findViewById(R.id.progressBar);
		
		firebaseDatabase = FirebaseDatabase.getInstance();
		databaseReference = firebaseDatabase.getReference();
		textViewLogin = findViewById(R.id.textViewLogin);
		textViewLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick( View v ) {
				startActivity(new Intent(RegisterActivity.this, MainActivity.class));
			}
		});
		
		buttonRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick( View v ) {
				userName = editTextUsername.getText().toString().trim();
				email = editTextEmail.getText().toString().trim();
				password = editTextPassword.getText().toString().trim();
				confirmPassword = editTextCnfPassword.getText().toString().trim();
				
				FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
					@Override
					public void onSuccess( InstanceIdResult instanceIdResult ) {
						instanceIdResult.getToken();
						token = instanceIdResult.getToken();
						Log.d("dgsdggd", "anujkas1 : " + instanceIdResult.getToken());
					}
				});
				
				if ( TextUtils.isEmpty(userName) ) {
					editTextUsername.setError("Empty");
				}
				else if ( TextUtils.isEmpty(email) ) {
					editTextEmail.setError("Empty");
				}
				else if ( TextUtils.isEmpty(password) ) {
					editTextPassword.setError("Empty");
				}
				else if ( editTextPassword.length() < 6 ) {
					editTextPassword.setError("Password must be 6 characters");
				}
				else {
					
					
					fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
						@Override
						public void onComplete( @NonNull Task<AuthResult> task ) {
							if ( task.isSuccessful() ) {
								User user = new User(databaseReference.push().getKey(), userName, password, email, "", token);
								databaseReference.child("users").child(userName).setValue(user);
								Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
								startActivity(new Intent(getApplicationContext(), MainActivity.class));
							}
							else {
								Toast.makeText(RegisterActivity.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
								progressBar.setVisibility(View.GONE);
							}
						}
					});
				}
				//	progressBar.setVisibility(View.VISIBLE);
			}
		});
	}
}