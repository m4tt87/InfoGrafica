package com.recsysclient.maps.businesslogic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.recsysclient.entity.PoI;

public class FilterInAuto implements StrategyFilterResults {
	//TODO mettere il range letto da costante dichiarata da qualche parte
	private int fixedRange = 1000;
	@Override
	public List<PoI> getFilteredList(List<PoI> p) {
		List<PoI> filteredList= new ArrayList<PoI>();
		Iterator<PoI> i= p.iterator();
		PoI instance;
		while(i.hasNext()){
			instance= i.next();
			if(Math.pow(fixedRange,2)<=Math.pow(instance.getLat(), 2.0)+Math.pow(instance.getLng(),2.0))
				filteredList.add(instance);
		}
		return filteredList;
	}

}
