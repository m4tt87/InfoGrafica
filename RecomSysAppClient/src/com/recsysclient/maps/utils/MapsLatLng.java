package com.recsysclient.maps.utils;

import com.google.android.gms.maps.model.LatLng;

public class MapsLatLng {	
	
	public MapsLatLng(){}
	
	public MapsLatLng(LatLng googleLL){
		latitude=googleLL.latitude;
		longitude=googleLL.longitude;
	}
	
	public MapsLatLng(double latitude, double longitude){
		this.latitude=latitude;
		this.longitude=longitude;
	}
	
	public double latitude;
	public double longitude;
}
