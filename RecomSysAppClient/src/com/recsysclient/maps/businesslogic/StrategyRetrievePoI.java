package com.recsysclient.maps.businesslogic;

import java.util.Set;

import com.recsysclient.entity.PoI;

public interface StrategyRetrievePoI {
	public Set<PoI> getPoISet();

	public Set<PoI> getPoISet(double lat, double lng);
}
