package com.recsysclient.entity;

public class PoI{
	private long id;
	private float lat;
	private float lng;
	private String title;
	private String feature;
	private String summary;
	private String wikipediaUrl;
	private String modelURI; //eventuale URI al file contenente il modello 3D
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
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
	
}
