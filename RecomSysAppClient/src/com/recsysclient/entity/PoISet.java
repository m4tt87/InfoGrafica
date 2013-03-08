package com.recsysclient.entity;

import java.util.Set;

public class PoISet {
	
	private Set<PoI> geonames;
	public Set<PoI> getPoiSet() {
		return geonames;
	}
	
	public void setPoiSet(Set<PoI> poiSet) {
		this.geonames = poiSet;
	}
	public PoISet(){}
	
	public PoISet(Set<PoI> poiSet) {
        this.geonames = poiSet;
    }
	
}
