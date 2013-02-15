package com.recsysclient.service;

public class Range {

	private float _min;
	private float _max;
	
	public Range(float min, float max){
		_min = min;
		_max = max;
	}
	
	public float getMin() {
		return _min;
	}
	
	public void setMin(float min) {
		this._min = min;
	}
	
	public float getMax() {
		return _max;
	}
	
	public void setMax(float max) {
		this._max = max;
	}
	
	public boolean isInTheRange(float value){
		if(value >= _min && value <= _max){
			return true;
		}
		return false;
	}
	
}
