package com.recsysclient.maps.utils;

import com.google.android.gms.maps.model.VisibleRegion;

public class MapsVisibleRegion {
	
	public MapsVisibleRegion(){}
	
	public MapsVisibleRegion( MapsLatLng nearLeft, MapsLatLng nearRight, MapsLatLng farLeft, MapsLatLng farRight ){
		this.nearLeft= nearLeft;
		this.nearRight= nearRight;
		this.farLeft=farLeft;
		this.farRight= farRight;
	}
	
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
