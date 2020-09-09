package com.vt.chatbox.Service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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

	public LocationTrack() {
	}


	@Override
	public void onCreate() {
		super.onCreate();

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		firebaseDatabase = FirebaseDatabase.getInstance();
		databaseReference = firebaseDatabase.getReference("chatdatabase");

	}

	public double getLongitude() {
		if (loc != null) {
			longitude = loc.getLongitude();
		}
		return longitude;
	}

	public double getLatitude() {
		if (loc != null) {
			latitude = loc.getLatitude();
		}
		return latitude;
	}

	public void showSettingsAlert() {

	}

	public boolean canGetLocation() {
		return this.canGetLocation;
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
//		Toast.makeText(LocationTrack.this, "checkGPS" +checkGPS, Toast.LENGTH_SHORT).show();

		// get network provider status
		checkNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//		Toast.makeText(LocationTrack.this, "checkNetwork" +checkNetwork, Toast.LENGTH_SHORT).show();

		if (!checkGPS && !checkNetwork) {
			Toast.makeText(LocationTrack.this, "No Service Provider is available", Toast.LENGTH_SHORT).show();
		} else {

			canGetLocation = true;
			Toast.makeText(LocationTrack.this, "checkNetwork" + canGetLocation, Toast.LENGTH_SHORT).show();

			// if GPS Enabled get lat/long using GPS Services

			if (checkGPS) {

				if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
						.PERMISSION_GRANTED && ActivityCompat
						.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
						!= PackageManager.PERMISSION_GRANTED) {

					// TODO: Consider calling
					//    ActivityCompat#requestPermissions
					// here to request the missing permissions, and then overriding
					//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
					//                                          int[] grantResults)
					// to handle the case where the user grants the permission. See the documentation
					// for ActivityCompat#requestPermissions for more details.
				}
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

				if (locationManager != null) {

					loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					if (loc != null) {
						latitude = loc.getLatitude();
//						Toast.makeText(LocationTrack.this, "lat" +latitude, Toast.LENGTH_SHORT).show();
						longitude = loc.getLongitude();
//						Toast.makeText(LocationTrack.this, "lon" +longitude, Toast.LENGTH_SHORT).show();
					}
				}
//				Toast.makeText(LocationTrack.this, "checkgps" +checkGPS, Toast.LENGTH_SHORT).show();
			}

			if (checkNetwork) {

//
//                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                        // TODO: Consider calling
//                        //    ActivityCompat#requestPermissions
//                        // here to request the missing permissions, and then overriding
//                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                        //                                          int[] grantResults)
//                        // to handle the case where the user grants the permission. See the documentation
//                        // for ActivityCompat#requestPermissions for more details.
//                    }
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER,
						MIN_TIME_BW_UPDATES,
						MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

				if (locationManager != null) {
					loc = locationManager
							.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					Toast.makeText(LocationTrack.this, "lon" + loc, Toast.LENGTH_SHORT).show();

				}

				if (loc != null) {
					latitude = loc.getLatitude();
					longitude = loc.getLongitude();
				}
			}

		}

//		Toast.makeText(LocationTrack.this, "ff", Toast.LENGTH_SHORT).show();
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
