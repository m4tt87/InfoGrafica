package com.recsysclient.entity;

public class PoI extends Evento {
	private float lat;
	private float lng;
	private String categoria;
	private String descrDettaglio;
	private String URI_modello; //eventuale URI al file contenente il modello 3D del
	
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
	public String getCategoria() {
		return categoria;
	}
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	public String getDescrDettaglio() {
		return descrDettaglio;
	}
	public void setDescrDettaglio(String descrDettaglio) {
		this.descrDettaglio = descrDettaglio;
	}
	public String getURI_modello() {
		return URI_modello;
	}
	public void setURI_modello(String uRI_modello) {
		URI_modello = uRI_modello;
	}
	
}
