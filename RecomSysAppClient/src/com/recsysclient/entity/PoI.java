package com.recsysclient.entity;

public class PoI extends Evento {
	private double lat;
	private double lng;
	private String categoria;
	private String descrDettaglio;
	private String URI_modello; //eventuale URI al file contenente il modello 3D del
	
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
