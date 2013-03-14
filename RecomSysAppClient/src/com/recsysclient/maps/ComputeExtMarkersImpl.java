package com.recsysclient.maps;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.recsysclient.entity.PoI;
import com.recsysclient.maps.utils.ExternalMarker;
import com.recsysclient.maps.utils.MapsLatLng;
import com.recsysclient.maps.utils.MapsVisibleRegion;

public class ComputeExtMarkersImpl implements IComputeExternalMarkersStrategy {

	// vector[0] angular coefficient, vector[1] offset 
	private double[] rightEdge;
	private double[] leftEdge;
	private double[] diagonal1; // nearLeft-farRight
	private double[] diagonal2;// nearRight-farLeft
	
	@Override
	public Map<Integer, Map<Long, ExternalMarker>> computeExternalMarkers(Set<PoI> pois,
			MapsVisibleRegion region, double bearing) {
		
		
		double[] translation = computeTranslationVector(region);
		
		region = applyTransformationToRegion( region, bearing, translation);
		
		setupLinesParameters(region);
		
		Map<Integer,Map<Long,ExternalMarker>> externalMarkers = new HashMap<Integer, Map<Long,ExternalMarker> >();
		
		externalMarkers.put(1, new HashMap<Long,ExternalMarker>());
		externalMarkers.put(2, new HashMap<Long,ExternalMarker>());
		externalMarkers.put(3, new HashMap<Long,ExternalMarker>());
		externalMarkers.put(4, new HashMap<Long,ExternalMarker>());
		
		float bottomEdgeLength = distance2(region.nearRight,region.nearLeft);
		float upEdgeLength = distance2(region.farLeft,region.farRight);
		float rightEdgeLength = distance2(region.nearRight,region.farRight);
		float leftEdgeLength = distance2(region.nearLeft,region.farLeft);
		
		for ( PoI p : pois ){
			MapsLatLng coords = applyTransformation( new MapsLatLng(p.getLat(), p.getLng()), bearing, translation );
			if(!isIntoVisibleRegion( region, coords)){
				double f1=Math.signum(diagonal1[0]*coords.longitude+diagonal1[1]-coords.latitude);				
				double f2=Math.signum(diagonal2[0]*coords.longitude+diagonal2[1]-coords.latitude);
				if( f1 > 0 && f2 > 0){//destra
					double intersecX = rightEdge[1]/( (coords.latitude/coords.longitude) - rightEdge[0] );
					double intersecY = rightEdge[0]*intersecX + rightEdge[1];
					float position = distance2(region.nearRight, new MapsLatLng(intersecY,intersecX))/rightEdgeLength;
					externalMarkers.get(1).put( p.getId(),new ExternalMarker(p,position));
				} else if(f1 < 0 && f2 < 0){//sinistra
					double intersecX = leftEdge[1]/( (coords.latitude/coords.longitude) - leftEdge[0] );
					double intersecY = rightEdge[0]*intersecX + rightEdge[1];
					float position = distance2(region.nearLeft, new MapsLatLng(intersecY,intersecX))/leftEdgeLength;
					externalMarkers.get(3).put( p.getId(),new ExternalMarker(p,position));
				} else if(f1 < 0 && f2 > 0){//sopra
					double intersecX = (coords.latitude/coords.longitude)*region.farLeft.latitude;
					float position = distance2(region.farLeft, new MapsLatLng(region.farLeft.latitude,intersecX))/upEdgeLength;
					externalMarkers.get(2).put( p.getId(),new ExternalMarker(p,position));
				} else {//sotto
					double intersecX = (coords.latitude/coords.longitude)*region.nearLeft.latitude;
					float position = distance2(region.nearLeft, new MapsLatLng(region.nearLeft.latitude,intersecX))/bottomEdgeLength;
					externalMarkers.get(4).put( p.getId(),new ExternalMarker(p,position));
				}
			}
		}
		
		return externalMarkers;
	}
	
	private float distance2(MapsLatLng point1, MapsLatLng point2) {
		return (float) (Math.pow( point1.latitude-point2.latitude,2) + Math.pow( point1.longitude-point2.longitude,2)); 
	}

	private void setupLinesParameters(MapsVisibleRegion region) {
		diagonal1 = new double[2];
		diagonal2 = new double[2];
		leftEdge = new double[2];
		rightEdge = new double[2];
		
		diagonal1[0] = (region.nearLeft.latitude - region.farRight.latitude) / (region.nearLeft.longitude - region.farRight.longitude);
		diagonal2[0] = (region.nearRight.latitude - region.farLeft.latitude) / (region.nearRight.longitude - region.farLeft.longitude);
		diagonal1[1] = region.farRight.latitude - region.farRight.longitude * diagonal1[0];
		diagonal2[1] = region.farLeft.latitude - region.farLeft.longitude * diagonal2[0];
		
		leftEdge[0] = (region.nearLeft.latitude - region.farLeft.latitude) / (region.nearLeft.longitude - region.farLeft.longitude);
		leftEdge[1] = region.farLeft.latitude - region.farLeft.longitude * leftEdge[0];
		
		rightEdge[0] = (region.nearRight.latitude - region.farRight.latitude) / (region.nearRight.longitude - region.farRight.longitude);
		rightEdge[1] = region.farRight.latitude - region.farRight.longitude * rightEdge[0];
		
		
	}

	private boolean isIntoVisibleRegion(MapsVisibleRegion region,
			MapsLatLng coords) {
		if( coords.latitude > region.farLeft.latitude || coords.latitude < region.nearLeft.latitude)
			return false;
		if ( coords.longitude > region.farRight.longitude || coords.longitude < region.farLeft.longitude)
			return false;
		if( coords.longitude <= region.nearRight.longitude && coords.longitude >= region.nearLeft.longitude)
			return true;
		if( coords.latitude > leftEdge[0]*coords.longitude + leftEdge[1] && coords.latitude > rightEdge[0]*coords.longitude + rightEdge[1] )
			return true;
		return false;
	}

	private MapsVisibleRegion applyTransformationToRegion( MapsVisibleRegion region, double bearing, double[] translation ){
		region.farLeft = applyTransformation( region.farLeft, bearing, translation );
		region.farRight = applyTransformation( region.farRight, bearing, translation );
		region.nearLeft = applyTransformation( region.nearLeft, bearing, translation );
		region.nearRight = applyTransformation( region.nearRight, bearing, translation );
				
		return region;	
	}
	
	private MapsLatLng applyTransformation( MapsLatLng coords, double rotationAngle, double[] translation ){
		coords.latitude = coords.latitude+translation[0];
		coords.longitude = coords.longitude+translation[1];
		
		rotationAngle = Math.toRadians(rotationAngle);
		
		MapsLatLng newCoords = new MapsLatLng();
		newCoords.longitude = coords.longitude*Math.cos(rotationAngle)-coords.latitude*Math.sin(rotationAngle);
		newCoords.latitude = coords.longitude*Math.sin(rotationAngle)+coords.latitude*Math.cos(rotationAngle);

		return newCoords;		
	}
	
	private double[] computeTranslationVector(MapsVisibleRegion region) {
		//calcolo intersezione
		double m1 = (region.nearLeft.latitude - region.farRight.latitude) / (region.nearLeft.longitude - region.farRight.longitude);
		double m2 = (region.nearRight.latitude - region.farLeft.latitude) / (region.nearRight.longitude - region.farLeft.longitude);
		double q1 = region.farRight.latitude - region.farRight.longitude * m1;
		double q2 = region.farLeft.latitude - region.farLeft.longitude * m2;
		double intersecX = (q2-q1)/(m1-m2);
		double intersecY = m1*intersecX + q1;
		double[] translation = { -intersecX, -intersecY };
		return translation;
	}

}
