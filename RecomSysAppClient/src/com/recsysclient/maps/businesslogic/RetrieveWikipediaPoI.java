package com.recsysclient.maps.businesslogic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import android.util.Log;

import com.recsysclient.utility.Connection;
import com.google.gson.Gson;
import com.recsysclient.entity.PoI;
import com.recsysclient.entity.PoISet;
import com.recsysclient.utility.Setting;

public class RetrieveWikipediaPoI implements StrategyRetrievePoI{
	private Map <Integer, Integer> map;
	private boolean sentRequest;

	private Integer id = 0;

	public RetrieveWikipediaPoI(){

		map= new HashMap<Integer, Integer>();
		sentRequest=false;
	}

	public Set<PoI> getPoISet(double lat, double lng){
		long t1;
		Log.e("retri", "get");
		Set<PoI> poiSet = new HashSet<PoI>();

		String url=Setting.BASEURL+Setting.RADIUSSTRING+Setting.RADIUS+Setting.MAXROWSSTRING+Setting.MAXROWS;
		if(sentRequest==false){
			Connection conn= new Connection();
			String response="";
			t1=System.currentTimeMillis();
			try {
				response=conn.execute(url).get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			t1= System.currentTimeMillis()-t1;
			System.out.println("Tempo richiesta remota= "+t1);
			sentRequest=true;
			
			
			if(response!=null){
				t1=System.currentTimeMillis();
				Gson gson = new Gson();
				PoISet ps = gson.fromJson(response, PoISet.class); 
				poiSet=ps.getPoiSet();
				t1=System.currentTimeMillis()-t1;
				System.out.println("Tempo di conversione in PoISet= "+t1);
				
				t1=System.currentTimeMillis();
				
				Iterator <PoI> it= poiSet.iterator();
				
				PoI p;
				
				while(it.hasNext()){
					p=it.next();
					//System.out.println("Hashcode="+p.hashCode()+", Nome="+p.getTitle());
					//System.out.println("Punto di interesse: "+p.toString());
					if(map.size()!=0){
						
						if(map.containsKey(p.hashCode())){
							p.setId(map.get(p.hashCode()));
						}
						else{
							//System.out.println("ho un nuovo elemento");
							map.put(p.hashCode(), id);
							p.setId(id);
							id++;
						}
					}
					else{
						//System.out.println("Hashmap uguale a 0");
						map.put(p.hashCode(), id);
						p.setId(id);
						id++;
					}

				}
				
				t1=System.currentTimeMillis()-t1;
				System.out.println("Tempo di determinazione id= "+t1);
				
				System.out.println("Numero PoI trovati: "+ poiSet.size());
				return poiSet;
			}
		}
		return null;
	}

	@Override
	public Set<PoI> getPoISet() {
		// TODO Auto-generated method stub
		return null;
	}

}
