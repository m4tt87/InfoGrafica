package com.recsysclient.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class Connection extends AsyncTask <String,Integer,String> {
	private String json;
	private String url;
	
	public void connect(String url){
		json ="";
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null;) {
			    builder.append(line).append("\n");
			}
			json= builder.toString();
			//System.out.println(json);
			} catch (ClientProtocolException e) {
			Log.d("HTTPCLIENT", e.getLocalizedMessage());
			} catch (IOException e) {
			Log.d("HTTPCLIENT", e.getLocalizedMessage());
		}
			
	}
	
	@Override
	protected String doInBackground(String... params) {
		int size=params.length;
		if(size>0){
			url=params[0];
			//System.out.println("url="+url);
			connect(url);
			return json;
		}
		return null;
	}

	
}
