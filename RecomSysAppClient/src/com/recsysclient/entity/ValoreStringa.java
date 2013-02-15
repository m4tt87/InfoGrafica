package com.recsysclient.entity;

import java.io.Serializable;

public class ValoreStringa extends Valore implements Serializable {
	private static final long serialVersionUID = 1L;

	

	private String valore;

	
    public ValoreStringa() {
    	super.setTipo(VALORE_STRINGA);
    }

	

	public String getValore() {
		return valore;
	}

	public void setValore(String valore) {
		this.valore = valore;
	}

	@Override
	public ValoreStringa clone() {
		ValoreStringa valoreStringa = new ValoreStringa();
		valoreStringa.setTipo(this.getTipo());
		valoreStringa.setTimestamp(this.getTimestamp());
		valoreStringa.setValore(String.copyValueOf(this.valore.toCharArray()));
		
		return valoreStringa;
	}
		
}
