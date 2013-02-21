package com.recsysclient.maps.businesslogic;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class RetrieveWikipediaPoI{
	Client client;
	WebResource resource;
	public RetrieveWikipediaPoI(float lat, float lng){
		
		//TODO: http://api.geonames.org/findNearbyWikipedia?lat=40.35&lng=18.17&username=m4tt&lang=it&maxRows=500&radius=20
		client = Client.create();
		resource = client.resource("http://api.geonames.org/findNearbyWikipedia?lat=40.35&lng=18.17&username=m4tt&lang=it&maxRows=500&radius=10");

		//Fire a GET request to RESTfull web service url
		//with parameters "param1" and "param2" and
		//set xml as the acceptable response type
		ClientResponse clientResponse=resource.accept("text/xml").get(ClientResponse.class);

		//get the response as string
		String response = clientResponse.getEntity(String.class);
		
		//FIXME
		System.out.println(response);
	}

}
