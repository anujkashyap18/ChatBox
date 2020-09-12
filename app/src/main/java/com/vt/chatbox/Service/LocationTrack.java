package com.vt.chatbox.Service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LocationTrack extends Service implements LocationListener {
	boolean checkGPS = false;
	boolean checkNetwork = false;
	boolean canGetLocation = false;
	Location loc;
	double latitude;
	double longitude;
	long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
	long MIN_TIME_BW_UPDATES = 1000;
	LocationManager locationManager;
	String sender, reciever, chatId, trackLocation;
	DatabaseReference databaseReference;
	FirebaseDatabase firebaseDatabase;
	long duration;

	public LocationTrack() {

	}

	@Override
	public void onCreate() {
		super.onCreate();

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		firebaseDatabase = FirebaseDatabase.getInstance();
		databaseReference = firebaseDatabase.getReference("chatdatabase");

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				onDestroy();
			}
		}, duration);

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		sender = intent.getStringExtra("sender");
		reciever = intent.getStringExtra("reciever");
		chatId = intent.getStringExtra("chatId");

		//get GPS status
		checkGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

		checkNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (!checkGPS && !checkNetwork) {
			Toast.makeText(LocationTrack.this, "No Service Provider is available", Toast.LENGTH_SHORT).show();
		} else {

			canGetLocation = true;
//

			if (checkGPS) {

				if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
						.PERMISSION_GRANTED && ActivityCompat
						.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
						!= PackageManager.PERMISSION_GRANTED) {

				}
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

				if (locationManager != null) {

					loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					if (loc != null) {
						latitude = loc.getLatitude();
						longitude = loc.getLongitude();
					}
				}
			}

			if (checkNetwork) {
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER,
						MIN_TIME_BW_UPDATES,
						MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

				if (locationManager != null) {
					loc = locationManager
							.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

				}

				if (loc != null) {
					latitude = loc.getLatitude();
					longitude = loc.getLongitude();
				}
			}

		}
		return START_STICKY;
	}

	@Override
	public void onLocationChanged(@NonNull Location location) {
		trackLocation = location.getLatitude() + ":" + location.getLongitude();
		databaseReference.child(sender).child(sender + "-chat-" + reciever).child(chatId).child("message").setValue(trackLocation);
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
}
