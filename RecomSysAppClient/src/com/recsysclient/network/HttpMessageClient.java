package com.recsysclient.network;


import java.io.IOException;
import java.net.CookieStore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;

import android.R;
import android.util.Log;

import com.recsysclient.entity.Evento;
import com.recsysclient.entity.Servizio;
import com.recsysclient.messaging.MessageManager;
import com.recsysclient.utility.AppCommonVar;

public class HttpMessageClient implements IMessageClient {

	private String URL_richiesta_raccomandazione = "http://192.168.2.12:8080/RestAppServerRecomSys/rest/recom/itemList"; //"http://192.168.2.12:8080/AppServerRecomSys/RecommendationService";
	private String URL_feedback = "http://192.168.2.12:8080/RestAppServerRecomSys/rest/recom/preference"; //"http://192.168.2.12:8080/AppServerRecomSys/RecommendationService"; 
	
	private static IMessageClient istanza = null;
	
	

	private HttpMessageClient() {
		
		
	}

	// Metodo della classe impiegato per accedere al Singleton
	public static synchronized IMessageClient getInstance() {
		if (istanza == null)
			istanza = new HttpMessageClient();
		return istanza;
	}
	
	@Override
	public boolean sendContextXmlMessage(String xmlMessage) {
		// TODO Auto-generated method stub
		String messaggio_da_server = "";
		boolean hasNewListElements = false;
		
		HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(URL_richiesta_raccomandazione);

	    try {
	        StringEntity se = new StringEntity( xmlMessage, HTTP.UTF_8);
	        se.setContentType("text/xml");
	        httppost.setEntity(se);

	        HttpResponse httpResponse = httpclient.execute(httppost);
	        System.out.println("msg inviato al Server: " + xmlMessage);
	        
	        HttpEntity resEntity = httpResponse.getEntity();
	        
	        messaggio_da_server = EntityUtils.toString(resEntity);
	        System.out.println("msg ricevuto dal Server: " + messaggio_da_server);

	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	        return false;
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	        return false;
	    }
	  
	    MessageManager messageManager = new MessageManager();
	  	Document document = messageManager.parseHeader(messaggio_da_server);
    	
    	//messaggio di risposta per il client
    	String msg_risposta="Messaggio non definito dal server";
    	
    	if (document != null){
	    	if(messageManager.getMsgType() == MessageManager.TYPE_SERVICE_LIST_MSG){
	    		
				Hashtable<String, Evento> eventTable = new Hashtable<String, Evento>();
				Hashtable<String, Servizio> serviceTable = new Hashtable<String, Servizio>();
				messageManager.parseServicesResult(document, serviceTable, eventTable);
				
				//***** EVENTI ******
				List<Evento> listaEventi = new ArrayList<Evento>();
				if(eventTable != null && eventTable.size()>0){
					
					listaEventi.addAll(eventTable.values());
					hasNewListElements = true;
				}
				else{
					Log.i("ServiceSocket", "sendXmlMessage: eventTable non ha elementi");
					hasNewListElements = false;
				}
				
				
				//***** SERVIZI ******
				List<Servizio> listaServizi = new ArrayList<Servizio>();
				if(serviceTable != null && serviceTable.size()>0){
					Collection<Servizio> servizi = serviceTable.values();
					for(Servizio servizio: servizi){
						if(servizio.is_segnalabileAdUtente()){
							listaServizi.add(servizio);
							hasNewListElements = true;
						}
						
					}
					
					
				}
				else{
					Log.i("ServiceSocket", "sendXmlMessage: serviceTable non ha elementi");
					hasNewListElements |= false; //eseguo un OR logico
				}
				AppCommonVar.setLista_servizi(listaServizi);
				AppCommonVar.setLista_eventi(listaEventi);
	    	}
	    	
    	}
	  		  	
		  	
	return hasNewListElements;
	}
	
	
	@Override
	public float sendPreferenceXmlMessage(String xmlMessage) {
		// TODO Auto-generated method stub
		String messaggio_da_server = "";
		float score = 0;
		
		HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(URL_feedback);

	    try {
	        StringEntity se = new StringEntity( xmlMessage, HTTP.UTF_8);
	        se.setContentType("text/xml");
	        httppost.setEntity(se);

	        HttpResponse httpResponse = httpclient.execute(httppost);
	        System.out.println("msg inviato al Server: " + xmlMessage);
	        
	        HttpEntity resEntity = httpResponse.getEntity();
	        
	        messaggio_da_server = EntityUtils.toString(resEntity);
	        System.out.println("msg ricevuto dal Server: " + messaggio_da_server);

	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	        return score;
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	        return score;
	    }
	  	MessageManager messageManager = new MessageManager();
	  	Document document = messageManager.parseHeader(messaggio_da_server);
    	
    	//messaggio di risposta per il client
    	String msg_risposta="Messaggio non definito dal server";
    	
    	if(messageManager.getMsgType() == MessageManager.TYPE_EVENT_SCORE_MSG){
    		Evento e = messageManager.parseEventScore(document);
    		score = e.get_score();
    		AppCommonVar.updateScoreEvento(e.get_URI_Individuo_Ontologia(), score);
    		Log.i("ServiceSocket", "sendPreferenceXmlMessage: score evento:"+score);
    		return score;
	    }
    	else if(messageManager.getMsgType() == MessageManager.TYPE_SERVICE_SCORE_MSG){
    		Servizio s = messageManager.parseServiceScore(document);
    		score = s.get_score();
    		AppCommonVar.updateScoreServizio(s.get_URI_Individuo_Ontologia(), score);
    		Log.i("ServiceSocket", "sendPreferenceXmlMessage: score servizio:"+score);
    		return score;
    	}	
	  
    	return score;
	}
	

	@Override
	public void closeClientConnection() {
		// TODO Auto-generated method stub

	}

}
