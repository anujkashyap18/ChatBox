package com.vt.chatbox;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

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

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

	SupportMapFragment mapFragment;
	LocationManager locationManager;
	Location location;
	GoogleMap googleMap;
	Marker marker;
	double lat, lon;


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_location);
		mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.location_fragment);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

			return;
		}
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0, this);

		location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		lat = location.getLatitude();
		lon = location.getLongitude();

//		 latLng =new LatLng(location1.getLatitude(),location1.getLonitude());

		mapFragment.getMapAsync(this);

	}

	@Override
	public void onMapReady(GoogleMap googleMap) {

		this.googleMap = googleMap;
		LatLng sydney = new LatLng(lat, lon);
		marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("My location"));
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,12f));

	}

	@Override
	public void onLocationChanged(@NonNull Location location) {
		lat = location.getLatitude();
		lon = location.getLongitude();
		marker.setPosition(new LatLng(lat, lon));
		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon),16f));
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