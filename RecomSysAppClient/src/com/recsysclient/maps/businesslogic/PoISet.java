package com.recsysclient.maps.businesslogic;

import java.util.Set;

public class PoISet {
	
	protected Set<PoI> geonames;
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
