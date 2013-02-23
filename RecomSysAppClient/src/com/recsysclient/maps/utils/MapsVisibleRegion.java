package com.recsysclient.maps.utils;

import com.google.android.gms.maps.model.VisibleRegion;

public class MapsVisibleRegion {
	
	public MapsVisibleRegion(){}
	
	public MapsVisibleRegion( VisibleRegion googleVR ){
		this.nearLeft= new MapsLatLng(googleVR.nearLeft);
		this.nearRight= new MapsLatLng(googleVR.nearRight);
		this.farLeft= new MapsLatLng(googleVR.farLeft);
		this.farRight= new MapsLatLng(googleVR.farRight);
	}
	public MapsLatLng nearLeft;
	public MapsLatLng nearRight;
	public MapsLatLng farLeft;
	public MapsLatLng farRight;		
}
