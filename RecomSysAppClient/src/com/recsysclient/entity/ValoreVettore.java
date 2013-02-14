package com.recsysclient.entity;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.List;



public class ValoreVettore extends Valore implements Serializable {
	private static final long serialVersionUID = 1L;

	private float[] valore;
	
	
    public ValoreVettore() {
    	super.setTipo(VALORE_VETTORIALE);
    }

	public float[] getValore() {
		return this.valore;
	}

	public void setValore(float[] valore) {
		this.valore = valore;
	}

	@Override
	public ValoreVettore clone() {
		ValoreVettore valoreVettore = new ValoreVettore();
		valoreVettore.setTipo(this.getTipo());
		valoreVettore.setTimestamp(this.getTimestamp());
		if(valore !=null)
			valoreVettore.setValore((float[])valore.clone());
		
		return valoreVettore;
	}	
}