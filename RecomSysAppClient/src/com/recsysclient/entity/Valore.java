package com.recsysclient.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;




public class Valore implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	public static final int VALORE_DECIMALE = 1;
	public static final int VALORE_BOOLEANO = 2;
	public static final int VALORE_STRINGA = 3;
	public static final int VALORE_VETTORIALE = 4;
	
	private long id;

	private long timestamp;

	private int tipoValore;

	
    public Valore() {
    	
    }

    public Valore getClone(){
    	Valore v = new Valore();
    	
    	v.tipoValore = this.tipoValore;
    	return v;
    }
    
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getTipo() {
		return this.tipoValore;
	}

	public void setTipo(int tipoValore) {
		this.tipoValore = tipoValore;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}	
	
	
}