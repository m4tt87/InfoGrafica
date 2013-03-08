package com.recsysclient.maps.businesslogic;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.util.Log;

import com.google.gson.Gson;
import com.recsysclient.entity.PoI;
import com.recsysclient.entity.PoISet;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class RetrieveWikipediaPoI implements StrategyRetrievePoI{
	private Client client;
	private WebResource resource;
	private Map <Integer, Integer> map;
	private boolean sentRequest;
	File f;

	private Integer id = 0;

	public RetrieveWikipediaPoI(){

		client = Client.create();
		map= new HashMap<Integer, Integer>();
		sentRequest=false;
	}

	public Set<PoI> getPoISet(double lat, double lng){
		Log.e("retri", "get");
		if(sentRequest==false){
			resource = client.resource("http://api.geonames.org/findNearbyWikipediaJSON?username=m4tt&lang=it&maxRows=500&radius=10&lat="+lat+"&lng="+lng);
			sentRequest=true;
			//Fire a GET request to RESTfull web service url
			ClientResponse clientResponse=resource.accept("text/xml").get(ClientResponse.class);
			if(clientResponse!=null){
				Set<PoI> poiSet = new HashSet<PoI>();
				String response= clientResponse.getEntity(String.class);
				System.out.println(response);

				Gson gson = new Gson();
				PoISet ps = gson.fromJson(response, PoISet.class); 
				poiSet=ps.getPoiSet();

				Iterator <PoI> it= poiSet.iterator();

				PoI p;

				while(it.hasNext()){
					p=it.next();
					System.out.println("Hashcode="+p.hashCode()+", Nome="+p.getTitle());
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

				//System.out.println(poiSet);
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
