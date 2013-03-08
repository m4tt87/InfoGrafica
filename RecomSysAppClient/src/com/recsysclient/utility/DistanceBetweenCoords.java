package com.recsysclient.utility;

public class DistanceBetweenCoords {
	public static double CalculateDistance(double currLat, double currLng, double d, double e) {
		    double pk = (float) (180/3.14169);

		    double a1 = currLat / pk;
		    double a2 = currLng / pk;
		    double b1 = d / pk;
		    double b2 = e / pk;

		    double t1 = Math.cos(a1)*Math.cos(a2)*Math.cos(b1)*Math.cos(b2);
		    double t2 = Math.cos(a1)*Math.sin(a2)*Math.cos(b1)*Math.sin(b2);
		    double t3 = Math.sin(a1)*Math.sin(b1);
		    double tt = Math.acos(t1 + t2 + t3);

		    return 6366000*tt;
	}
}
