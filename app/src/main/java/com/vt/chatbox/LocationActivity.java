package com.vt.chatbox;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vt.chatbox.Service.LocationTrack;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

	SupportMapFragment mapFragment;
	LocationManager locationManager;
	Location location;
	GoogleMap googleMap;
	Marker marker;
	double lat, lon;
	ImageView back, locationBack;
	String latitude, longitude, recieverLocation, currentUser;
	String trackLocation, revieverimg;
	FirebaseStorage sref;
	StorageReference ref;
	DatabaseReference databaseReference;
	FirebaseDatabase firebaseDatabase;
	ProgressBar progressBar;
	ConstraintLayout shareLocation, currentLocation;
	LinearLayout locationTime, locationLive;
	MaterialButton min15, hour1, hour3;
	LocationTrack locationTrack;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_location);

		firebaseDatabase = FirebaseDatabase.getInstance();
		databaseReference = firebaseDatabase.getReference("chatdatabase");
		sref = FirebaseStorage.getInstance();
		ref = sref.getReference("location/" + recieverLocation + "/locationimg" + System.currentTimeMillis());


		progressBar = findViewById(R.id.progress);
		shareLocation = findViewById(R.id.con_share);
		locationTime = findViewById(R.id.location_time);
		locationLive = findViewById(R.id.location_live);
		locationBack = findViewById(R.id.location_back);
		currentLocation = findViewById(R.id.currentLocation);
		min15 = findViewById(R.id.min15);
		hour1 = findViewById(R.id.hour1);
		hour3 = findViewById(R.id.hour8);


		min15.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				takeScreenShot();
				progressBar.setVisibility(View.VISIBLE);
			}
		});

		hour1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				takeScreenShot();
				progressBar.setVisibility(View.VISIBLE);
			}
		});

		hour3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				takeScreenShot();
				progressBar.setVisibility(View.VISIBLE);
			}
		});


		locationBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				locationTime.setVisibility(View.GONE);
				locationLive.setVisibility(View.VISIBLE);
			}
		});

		back = findViewById(R.id.imageBack);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});


		mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.location_fragment);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("location not found")  // GPS not found
					.setMessage("Please enable location") // Want to enable?
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialogInterface, int i) {
							startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
						}
					})
					.setNegativeButton("no", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							onBackPressed();
						}
					});

			AlertDialog alertDialog = builder.create();
			alertDialog.show();
		}

		if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager
				.PERMISSION_GRANTED && ActivityCompat
				.checkSelfPermission
						(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

			return;
		}
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0, this);

		location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		lat = location.getLatitude();
		lon = location.getLongitude();

		latitude = "" + lat;
		longitude = "" + lon;

		trackLocation = latitude + ":" + longitude;

		locationLive.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				locationLive.setVisibility(View.GONE);
				locationTime.setVisibility(View.VISIBLE);
			}
		});

		currentLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				takeScreenShot();
				progressBar.setVisibility(View.VISIBLE);
			}
		});

		mapFragment.getMapAsync(this);

	}

	public void takeScreenShot() {


		GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
			@Override
			public void onSnapshotReady(Bitmap bitmap) {
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
				byte[] bytes = byteArrayOutputStream.toByteArray();

				final UploadTask uploadTask = ref.putBytes(bytes);
				uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
					@Override
					public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

						ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
							@Override
							public void onSuccess(Uri uri) {
								String url = String.valueOf(uri);

								Intent it = getIntent();
								recieverLocation = it.getStringExtra("location");
								currentUser = it.getStringExtra("currentUser");
								revieverimg = it.getStringExtra("recieverimg");

								String id = databaseReference.push().getKey();
								long t = System.currentTimeMillis();
								Map<String, String> hm = new HashMap<>();

								hm.put("message", trackLocation);
								hm.put("sender", currentUser);
								hm.put("time", String.valueOf(t));
								hm.put("type", "userLocation");
								hm.put("images", url);

								if (revieverimg.equals("group")) {

									hm.put("reciever", "group");
									databaseReference.child("group").child(recieverLocation + "-chat").child(id).setValue(hm);
									finish();
								} else {

									hm.put("reciever", recieverLocation);

									databaseReference.child(currentUser).child(currentUser + "-chat-" + recieverLocation).child(id).setValue(hm);
									databaseReference.child(recieverLocation).child(recieverLocation + "-chat-" + currentUser).child(id).setValue(hm);
									progressBar.setVisibility(View.GONE);

									Log.d(getClass().getSimpleName(), "location");
//									Toast.makeText(LocationActivity.this, "Location send Successful", Toast.LENGTH_LONG).show();
									Intent intent = new Intent(LocationActivity.this, LocationTrack.class);
									intent.putExtra("reciever", recieverLocation);
									intent.putExtra("sender", currentUser);
									intent.putExtra("chatId", id);
									startService(intent);
									finish();
								}
							}
						});
					}
				}).addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {

					}
				});
				Log.d(getClass().getSimpleName(), "showBitmap" + bitmap);
			}
		};

		googleMap.snapshot(callback);

	}

	@Override
	public void onMapReady(GoogleMap googleMap) {

		this.googleMap = googleMap;
		LatLng sydney = new LatLng(lat, lon);
		marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("My location"));
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12f));

	}

	@Override
	public void onLocationChanged(@NonNull Location location) {
		lat = location.getLatitude();
		lon = location.getLongitude();
		marker.setPosition(new LatLng(lat, lon));
		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 16f));

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(@NonNull String provider) {

	}

	@Override
	public void onProviderDisabled(@NonNull String provider) {

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}