package com.recsysclient.entity;

import java.io.Serializable;

public class PoI implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3484973893388207915L;
	private long id;
	private double lat;
	private double lng;
	private String title;
	private String feature;
	private String summary;
	private String wikipediaUrl;
	private float distance;
	private String modelURI; //eventuale URI al file contenente il modello 3D
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFeature() {
		return feature;
	}
	public void setFeature(String feature) {
		this.feature = feature;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getWikipediaUrl() {
		return wikipediaUrl;
	}
	public void setWikipediaUrl(String wikipediaUrl) {
		this.wikipediaUrl = wikipediaUrl;
	}
	public String getModelURI() {
		return modelURI;
	}
	public void setModelURI(String modelURI) {
		this.modelURI = modelURI;
	}
	
	public float getDistance() {
		return distance;
	}
	public void setDistance(float distance) {
		this.distance = distance;
	}
	@Override
    public String toString() {
        return "PoI [lat=" + lat + ", lng=" + lng + ", title="+ title +", descr="+summary+"]";
    }
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(lat);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(lng);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}
	
	
}
