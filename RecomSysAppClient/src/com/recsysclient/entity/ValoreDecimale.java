package com.recsysclient.entity;

import java.io.Serializable;

import android.util.Log;




public class ValoreDecimale extends Valore implements Serializable {
	private static final long serialVersionUID = 1L;

	
	private double valore;

	
    public ValoreDecimale() {
    	super.setTipo(VALORE_DECIMALE);
    }

	public double getValore() {
		return this.valore;
	}

	public void setValore(double valore) {
		this.valore = valore;
	}

    @Override
    public ValoreDecimale clone() {
    	// TODO Auto-generated method stub
    	try {
			return (ValoreDecimale)super.clone();
		} catch (CloneNotSupportedException e) {
			Log.e("ValoreDecimale", "clone: ERRORE! Funzione clone() non supportata");
			e.printStackTrace();
		}
		return null;
    }	
}