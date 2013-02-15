package com.recsysclient.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;

import com.recsysclient.entity.Evento;
import com.recsysclient.entity.Servizio;
import com.recsysclient.messaging.MessageManager;
import com.recsysclient.utility.AppCommonVar;
import com.recsysclient.utility.AppUtils;

import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.ListView;

public class ServiceSocket implements IMessageClient {
	private Socket _clientSocket = null;
	private DataOutputStream _dataOutputStream = null;
	private DataInputStream _dataInputStream = null;
		
	private static IMessageClient istanza = null;

	private ServiceSocket() {
	}

	// Metodo della classe impiegato per accedere al Singleton
	public static synchronized IMessageClient getInstance() {
		if (istanza == null)
			istanza = new ServiceSocket();
		return istanza;
	}
	
	private boolean openConnection(){
		try {
			Log.i("ServiceSocket", "openConnection()");
			_clientSocket = new Socket();
			//FIXME indirizzo ip server
			SocketAddress sockaddr = new InetSocketAddress("192.168.43.128", 8081); //"192.168.2.3" - "192.168.59.1"
			_clientSocket.connect(sockaddr, 5000);
//			Log.i("ServiceSocket", "setto il timeout");
			_clientSocket.setSoTimeout(0);
			Log.i("ServiceSocket", "openConnection()... Connessione stabilita!");
		} catch (UnknownHostException e) {
			Log.e("ServiceSocket", "openConnection: Impossibile stabilire una connessione Connessione");
			e.printStackTrace();
			return false; //connessione non riuscita
		} catch (IOException e) {
			Log.e("ServiceSocket", "openConnection: Impossibile stabilire una connessione Connessione");
			//e.printStackTrace();
			return false; //connessione non riuscita
		}	
		return true;
	}
	
//	public boolean sendRequest(){
//		
//		// TODO Auto-generated method stub
//		
//		if((_clientSocket == null) || !(_clientSocket.isConnected()) || (_clientSocket.isClosed())){
//			if(!openConnection()) return false; //connessione non riuscita
//		}
//		try {
//			_dataOutputStream = new DataOutputStream(_clientSocket.getOutputStream());
//		
//			_dataInputStream = new DataInputStream(_clientSocket.getInputStream());
//		  
//		  	_dataOutputStream.writeUTF("sono il client android");
//		  	String msg = _dataInputStream.readUTF();
//		  	System.out.println("msg ricevuto da Server : " + msg);
//		  
//		  	_dataOutputStream.flush();
//		} catch (IOException e) {
//			e.printStackTrace();
//			return false; //comunicazione non riuscita			
//		}
//	  	return true;
//	}
	
	/* (non-Javadoc)
	 * @see com.motiondetector.network.IMessageClient#sendContextXmlMessage(java.lang.String)
	 */
	@Override
	public boolean sendContextXmlMessage(String xmlMessage){
		
		// TODO Auto-generated method stub
		String messaggio_da_server = "";
		boolean hasNewListElements = false;
		
		if((_clientSocket == null) || !(_clientSocket.isConnected()) || (_clientSocket.isClosed())){
			Log.i("ServiceSocket", "sendXmlMessage: Apro una nuova connessione");
			if(!openConnection()){
				Log.i("ServiceSocket", " Connessione non riuscita");
				return false; 
			}
		}
		try {
			_dataOutputStream = new DataOutputStream(_clientSocket.getOutputStream());
		
			_dataInputStream = new DataInputStream(_clientSocket.getInputStream());
		  
			//INVIO DEL MESSAGGIO AL SERVER
		  	_dataOutputStream.writeUTF(xmlMessage);
		  	System.out.println("msg inviato al Server: " + xmlMessage);
		  	_dataOutputStream.flush();
		  	
		    //RICEZIONE DEL MESSAGGIO DAL SERVER
		  	messaggio_da_server = _dataInputStream.readUTF();
		  	System.out.println("msg ricevuto dal Server: " + messaggio_da_server);
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
		    	else if(messageManager.getMsgType() == MessageManager.TYPE_EVENT_PREFERENCE_MSG){
		    		
		    	}		    	
		    	else if(messageManager.getMsgType() == MessageManager.TYPE_SERVICE_PREFERENCE_MSG){
		    		
		    	}
	    	}
		  	System.out.println("msg ricevuto da Server: " + messaggio_da_server);
		  
//		  	_dataOutputStream.flush();
		  	
		} catch (IOException e) {
			e.printStackTrace();
			closeClientConnection();
			return false; //comunicazione non riuscita			
		}
	  	
		return hasNewListElements;
	}
	
	
	
/* (non-Javadoc)
 * @see com.motiondetector.network.IMessageClient#sendPreferenceXmlMessage(java.lang.String)
 */
@Override
public float sendPreferenceXmlMessage(String xmlMessage){
		
		// TODO Auto-generated method stub
		String messaggio_da_server = "";
		float score = 0;
		
		if((_clientSocket == null) || !(_clientSocket.isConnected()) || (_clientSocket.isClosed())){
			Log.i("ServiceSocket", "sendXmlMessage: Apro una nuova connessione");
			if(!openConnection()){
				Log.i("ServiceSocket", " Connessione non riuscita");
				return score; 
			}
		}
		try {
			_dataOutputStream = new DataOutputStream(_clientSocket.getOutputStream());
		
			_dataInputStream = new DataInputStream(_clientSocket.getInputStream());
		  
			//INVIO DEL MESSAGGIO AL SERVER
		  	_dataOutputStream.writeUTF(xmlMessage);
		  	System.out.println("msg preferenza evento inviato al Server: " + xmlMessage);
		  	_dataOutputStream.flush();
		  	
		    //RICEZIONE DEL MESSAGGIO DAL SERVER
		  	messaggio_da_server = _dataInputStream.readUTF();
		  	System.out.println("msg di tipo event score ricevuto dal Server: " + messaggio_da_server);
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
		  
//		  	_dataOutputStream.flush();
		  	
		} catch (IOException e) {
			e.printStackTrace();
			closeClientConnection();
			return score; //comunicazione non riuscita			
		}
	  	
		return score;
	}
	
	
	/* (non-Javadoc)
	 * @see com.motiondetector.network.IMessageClient#closeClientConnection()
	 */
	@Override
	public void closeClientConnection(){
		Log.i("ServiceSocket", "\n\n closeClientConnection(): Chiudo il socket \n\n");
		if(_clientSocket!=null){
			try {
				_clientSocket.close();
			} catch (IOException e) {
				System.err.println("Close failed.");
//				System.exit(1);
			}
		}

		if( _dataInputStream!= null){
			try {
				_dataInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if(_dataOutputStream!= null){
			try {
				_dataOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
