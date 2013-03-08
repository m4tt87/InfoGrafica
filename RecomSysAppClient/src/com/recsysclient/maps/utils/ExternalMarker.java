package com.recsysclient.maps.utils;

import com.recsysclient.entity.PoI;

public class ExternalMarker {

	private PoI point;
	private float position; // posizione lungo la barra degli indicatori
	
	public ExternalMarker( PoI point, float position){
		this.point=point;
		this.position=position;		
	}

	public PoI getPoint() {
		return point;
	}

	public void setPoint(PoI point) {
		this.point = point;
	}

	public float getPosition() {
		return position;
	}

	public void setPosition(float position) {
		this.position = position;
	}
}
