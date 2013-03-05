package com.recsysclient.entity;

public class ContextInfo {
	private float lat;
	private float lng;
	private double bearing;
	private String motionState;
	private String motionKind;
	private String locationProvider;
	private float gpsStatus;
	public float getLat() {
		return lat;
	}
	public void setLat(float lat) {
		this.lat = lat;
	}
	public float getLng() {
		return lng;
	}
	public void setLng(float lng) {
		this.lng = lng;
	}
	public double getBearing() {
		return bearing;
	}
	public void setBearing(double d) {
		this.bearing = d;
	}
	public String getMotionState() {
		return motionState;
	}
	public void setMotionState(String motionState) {
		this.motionState = motionState;
	}
	public String getMotionKind() {
		return motionKind;
	}
	public void setMotionKind(String motionKind) {
		this.motionKind = motionKind;
	}
	public String getLocationProvider() {
		return locationProvider;
	}
	public void setLocationProvider(String locationProvider) {
		this.locationProvider = locationProvider;
	}
	public float getGpsStatus() {
		return gpsStatus;
	}
	public void setGpsStatus(float gpsStatus) {
		this.gpsStatus = gpsStatus;
	}
	
}