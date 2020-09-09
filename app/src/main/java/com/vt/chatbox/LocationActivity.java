package com.vt.chatbox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vt.chatbox.Service.LocationTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

	private final static int ALL_PERMISSIONS_RESULT = 101;
	SupportMapFragment mapFragment;
	LocationManager locationManager;
	Location location;
	GoogleMap googleMap;
	Marker marker;
	double lat, lon;
	DatabaseReference databaseReference;
	FirebaseDatabase firebaseDatabase;
	ImageView sendLocation, back;
	String latitude, longitude, recieverLocation, currentUser;
	String trackLocation, revieverimg;
	ArrayList permissionsToRequest;
	ArrayList permissionsRejected = new ArrayList();
	ArrayList permissions = new ArrayList();
	LocationTrack locationTrack;


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_location);

		sendLocation = findViewById(R.id.send_location);

		back = findViewById(R.id.imageBack);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});

		firebaseDatabase = FirebaseDatabase.getInstance();
		databaseReference = firebaseDatabase.getReference("chatdatabase");


		mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.location_fragment);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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

//		 latLng =new LatLng(location1.getLatitude(),location1.getLonitude());

		latitude = "" + lat;
		longitude = "" + lon;


		trackLocation = latitude + ":" + longitude;

		sendLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

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

				if (revieverimg.equals("group")) {

					hm.put("reciever", "group");
					databaseReference.child("group").child(recieverLocation + "-chat").child(id).setValue(hm);
					Toast.makeText(LocationActivity.this, "Location send Successful", Toast.LENGTH_LONG).show();
					finish();
				} else {

					hm.put("reciever", recieverLocation);

					databaseReference.child(currentUser).child(currentUser + "-chat-" + recieverLocation).child(id).setValue(hm);
					databaseReference.child(recieverLocation).child(recieverLocation + "-chat-" + currentUser).child(id).setValue(hm);

////				MapModel mapModel = new MapModel(latitude, longitude);
//				databaseReference.child("chatdatabase").child(recieverLocation).setValue(hm);
					Log.d(getClass().getSimpleName(), "location");
					Toast.makeText(LocationActivity.this, "Location send Successful", Toast.LENGTH_LONG).show();
					Intent intent = new Intent(LocationActivity.this, LocationTrack.class);
					intent.putExtra("reciever", recieverLocation);
					intent.putExtra("sender", currentUser);
					intent.putExtra("chatId", id);
					startService(intent);
					finish();

				}


			}
		});


		mapFragment.getMapAsync(this);

	}


	public void takeScreenShot() {

		// Get device dimmensions
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

// Get root view
		View views = getWindow().getDecorView().getRootView();

// Create the bitmap to use to draw the screenshot
		final Bitmap bitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_4444);
		final Canvas canvas = new Canvas(bitmap);

// Get current theme to know which background to use
		final Activity activity = this.getParent();
		final Resources.Theme theme = activity.getTheme();
		final TypedArray ta = theme.obtainStyledAttributes(new int[]{android.R.attr.windowBackground});
		final int res = ta.getResourceId(0, 0);
		final Drawable background = activity.getResources().getDrawable(res);

// Draw background
		background.draw(canvas);

// Draw views
		views.draw(canvas);
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