package com.recsysclient.maps;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.recsysclient.entity.PoI;
import com.recsysclient.maps.utils.ExternalMarker;
import com.recsysclient.maps.utils.MapsVisibleRegion;

public interface IComputeExternalMarkersStrategy {

	public Map<Integer, List<ExternalMarker>> computeExternalMarkers(Set<PoI> pois, MapsVisibleRegion region, double d);

}
