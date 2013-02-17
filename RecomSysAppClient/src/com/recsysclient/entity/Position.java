package com.recsysclient.entity;

public class Position {
	private float lat;
	private float lng;
	private float bearing;
	private int tilt;
	private float zoom;
	private String motionState;
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
	public float getBearing() {
		return bearing;
	}
	public void setBearing(float bearing) {
		this.bearing = bearing;
	}
	public int getTilt() {
		return tilt;
	}
	public void setTilt(int tilt) {
		this.tilt = tilt;
	}
	public float getZoom() {
		return zoom;
	}
	public void setZoom(float zoom) {
		this.zoom = zoom;
	}
	public String getMotionState() {
		return motionState;
	}
	public void setMotionState(String motionState) {
		this.motionState = motionState;
	}
	
}
