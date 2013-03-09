package com.recsysclient.maps.businesslogic;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.recsysclient.entity.PoI;
import com.recsysclient.utility.DistanceBetweenCoords;

public class FilterInAuto implements StrategyFilterResults {
	//TODO mettere il range letto da costante dichiarata da qualche parte (in Km)
	private int fixedRange = 10000;
	@Override
	public Set<PoI> getFilteredList(Set<PoI> p, double currLat, double currLng) {
		Set<PoI> filteredList= new HashSet<PoI>();
		Iterator<PoI> i= p.iterator();
		PoI instance;
		while(i.hasNext()){
			instance= i.next();
			double d= DistanceBetweenCoords.CalculateDistance(currLat,currLng,instance.getLat(),instance.getLng());
			if(d<=fixedRange)
				filteredList.add(instance);
		}
		return filteredList;
	}

}
