package com.recsysclient.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Outlier {
	
	public List<Double> list;
	public List<Double> outlierList;
	public List<Double> purgedList;
	
	public int p1;
	public int p3;
	public double q1;
	public double q3;
	
	public double minThreshold;
	public double maxThreshold;
	
	public static final float K=1.5f;
	
	public Outlier(List <Double> l){
		list=l;
		Collections.sort(list);
		calcolaQuartili();
		calcolaOutlier();
		calcolaPurgedList();
			
	}
	
	private void calcolaQuartili(){
		double s= list.size()/4;
		p1=(int)Math.floor(s);
		q1=list.get(p1);
		p3=(int)Math.floor(3*s);
		q3=list.get(p3);
		minThreshold=q1-K*(q3-q1);
		maxThreshold=q3+K*(q3-q1);
	}
	
	private void calcolaOutlier(){
		for(int i=p1;i>=0; i--)
			if(list.get(i)<minThreshold)
				outlierList.add(list.get(i));
		
		for(int i=p3; i<list.size();i++)
			if(list.get(i)>maxThreshold)
				outlierList.add(list.get(i));
	}
	
	public List<Double> getOutlierList(){
		return outlierList;
	}
	
	private void calcolaPurgedList(){
		purgedList=new ArrayList<Double>(list);
		if(outlierList!=null && outlierList.size()!=0)
			purgedList.removeAll(outlierList);
	}
	
	public List<Double> getPurgedList(){
		return purgedList;
	}
	
	public Double getMean(){
		int size= purgedList.size();
		double c=0;
		for(int i=0;i<size;i++)
			c+=purgedList.get(i);
		return c/size;
	}
	
}
