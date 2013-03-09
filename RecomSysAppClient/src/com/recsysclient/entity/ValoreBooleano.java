package com.recsysclient.entity;

import java.io.Serializable;

import android.util.Log;


public class ValoreBooleano extends Valore implements Serializable {
	private static final long serialVersionUID = 1L;

	
	
	private boolean valore;

	
    public ValoreBooleano() {
    	super.setTipo(VALORE_BOOLEANO);
    }

	public boolean getValore() {
		return this.valore;
	}

	public void setValore(boolean valore) {
		this.valore = valore;
	}
	
	@Override
	public ValoreBooleano clone() {
		// TODO Auto-generated method stub
		try {
			return (ValoreBooleano)super.clone();
		} catch (CloneNotSupportedException e) {
			Log.e("ValoreBooleano", "clone: ERRORE! Funzione clone() non supportata");
			e.printStackTrace();
		}
		return null;
	}
		
}