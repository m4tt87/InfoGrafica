package com.recsysclient.maps.businesslogic;

import java.util.List;
import com.recsysclient.entity.PoI;

public interface StrategyFilterResults {
	public List<PoI> getFilteredList(List<PoI> p);
}
