package com.recsysclient.entity;

public class StatoContesto {
	
	private long timestamp;
	
	private float latitudine;
	private float longitudine;
	private float altitudine;
	private float accuratezza;
	
	private String tipoDiLuogo; // indoor/outdoor
	private String idLocationNfcTag;
	
		
	private String stato_moto;
	private String mezzo_utente;
	private float velocita;
	
	private int id_stato_moto;
	private int stato_device;
	private int tipo_utilizzo_device;
	


	public int getId_stato_moto() {
		return id_stato_moto;
	}



	public void setId_stato_moto(int id_stato_moto) {
		this.id_stato_moto = id_stato_moto;
	}



	public int getStato_device() {
		return stato_device;
	}



	public void setStato_device(int stato_device) {
		this.stato_device = stato_device;
	}



	public int getTipo_utilizzo_device() {
		return tipo_utilizzo_device;
	}



	public void setTipo_utilizzo_device(int tipo_utilizzo_device) {
		this.tipo_utilizzo_device = tipo_utilizzo_device;
	}


	//Metodi GET e SET
	
	public long getTimestamp() {
		return timestamp;
	}



	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	
	
	public float getLatitudine() {
		return latitudine;
	}



	public void setLatitudine(float latitudine) {
		this.latitudine = latitudine;
	}



	public float getLongitudine() {
		return longitudine;
	}



	public void setLongitudine(float longitudine) {
		this.longitudine = longitudine;
	}



	public float getAltitudine() {
		return altitudine;
	}



	public void setAltitudine(float altitudine) {
		this.altitudine = altitudine;
	}



	public float getAccuratezza() {
		return accuratezza;
	}



	public void setAccuratezza(float accuratezza) {
		this.accuratezza = accuratezza;
	}



	public String getTipoDiLuogo() {
		return tipoDiLuogo;
	}



	public void setTipoDiLuogo(String tipoDiLuogo) {
		this.tipoDiLuogo = tipoDiLuogo;
	}


	public String getIdLocationNfcTag() {
		return idLocationNfcTag;
	}


	public void setIdLocationNfcTag(String idLocationNfcTag) {
		this.idLocationNfcTag = idLocationNfcTag;
	}
	
	
	


	
	
	public String getStato_moto() {
		return stato_moto;
	}



	public void setStato_moto(String stato_moto) {
		this.stato_moto = stato_moto;
	}



	public String getMezzo_utente() {
		return mezzo_utente;
	}



	public void setMezzo_utente(String mezzo_utente) {
		this.mezzo_utente = mezzo_utente;
	}



	public float getVelocita() {
		return velocita;
	}



	public void setVelocita(float velocita) {
		this.velocita = velocita;
	}

	
	@Override
	public String toString(){
		String str ="";
		
		str += " timestamp:" + timestamp;
		
		str += "\n longitudine:" + longitudine;
		str += "\n latitudine:" + latitudine;
		str += "\n altitudine:" + altitudine;
		str += "\n accuratezza:" + accuratezza;
		
		str += "\n tipoDiLuogo:" + tipoDiLuogo; // indoor/outdoor
		
		
		str += "\n stato_moto:" + stato_moto;
		str += "\n mezzo_utente:" + mezzo_utente;
		str += "\n velocita:" + velocita;
		
		
				
		
		return str;
	}

}
