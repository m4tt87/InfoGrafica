package com.recsysclient.maps.businesslogic;

import java.util.Set;
import com.recsysclient.entity.PoI;

public interface StrategyFilterResults {
	public Set<PoI> getFilteredList(Set<PoI> p, double currLat, double currLng);
}
