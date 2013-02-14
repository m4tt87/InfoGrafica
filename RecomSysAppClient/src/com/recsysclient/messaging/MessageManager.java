package com.recsysclient.messaging;


import java.io.DataInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

import com.recsysclient.entity.Evento;
import com.recsysclient.entity.Servizio;
import com.recsysclient.entity.StatoContesto;

public class MessageManager {
	public static final int TYPE_CONTEXT_STATE_MSG = 0; 
	public static final int TYPE_SERVICE_LIST_MSG = 1;
	public static final int TYPE_FEEDBACK_MSG = 2;
	public static final int TYPE_EVENT_PREFERENCE_MSG = 3;
	public static final int TYPE_SERVICE_PREFERENCE_MSG = 4;
	public static final int TYPE_EVENT_SCORE_MSG = 5;
	public static final int TYPE_SERVICE_SCORE_MSG = 6;

	private String _uri_utente_sender;
	private long _msgTimestamp;
	private int _msgType;

	private List<Servizio> _listaServizi;
	private StatoContesto _statoContesto;

	private DataInputStream _dataInputStream;
	private Document _document;
	private XmlSerializer xmlSerializer;

	public MessageManager(){

	}

	public String get_uri_utente_sender() {
		return _uri_utente_sender;
	}


	public long getMsgTimestamp() {
		return _msgTimestamp;
	}


	public int getMsgType() {
		return _msgType;
	}


	//effettua il parsing dell'header in modo da ricavare il tipo di messaggio e il timestamp
	public Document parseHeader(String xmlString){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			InputSource inStream = new InputSource();
			inStream.setCharacterStream(new StringReader(xmlString));

			Document doc = db.parse(inStream);
			doc.getDocumentElement().normalize();

			//header
			Element header_element = (Element) (doc.getElementsByTagName("header")).item(0);

//			//uri_utente
//			Element uri_utente_element = (Element) (header_element.getElementsByTagName("uri_utente")).item(0);
//			if(uri_utente_element.hasChildNodes()){
//				String uri_utente_str = uri_utente_element.getFirstChild().getNodeValue().trim();
//				if(uri_utente_str != null && !(uri_utente_str.equalsIgnoreCase(""))){
//					_uri_utente_sender = uri_utente_str.trim();
//				}
//			}
			
			//timestamp
			Element timestamp_element = (Element) (header_element.getElementsByTagName("timestamp")).item(0);
			if(timestamp_element.hasChildNodes()){
				String timestamp_str = timestamp_element.getFirstChild().getNodeValue().trim();
				if(timestamp_str != null && !(timestamp_str.equalsIgnoreCase(""))){
					_msgTimestamp = Long.parseLong(timestamp_str.trim());
				}
			}

			//message_type
			Element message_type_element = (Element) (header_element.getElementsByTagName("message_type")).item(0);
			if(message_type_element.hasChildNodes()){
				String message_type_str = message_type_element.getFirstChild().getNodeValue().trim();
				if(message_type_str != null && !(message_type_str.equalsIgnoreCase(""))){
					_msgType = Integer.parseInt(message_type_str.trim());
				}
			}
			return doc;

		} catch (ParserConfigurationException e) {
			System.err.println("********* ParserConfigurationException");
			e.printStackTrace();
			return null; 
		} catch (SAXException e) {
			System.err.println("********* SAXException");
			e.printStackTrace();
			return null; 
		} catch (IOException e) {
			System.err.println("********* IOException");
			e.printStackTrace();
			return null; 
		}

	}


	public void parseServicesResult(Document document, Hashtable<String, Servizio> serviceTable, Hashtable<String, Evento> eventTable){

		long idServizio;
		String nomeServizio;
		String uri_servizio_ontologia;
		String uri_servizio;
		String tipoServizio;
		String descrizione;
		String score_str;
		float score;
		String feedback_str;
		int feedback;
		String has_feedback_str;
		boolean has_feedback;
		String freq_utilizzo_str;
		int freq_utilizzo;
		String freq_visualizzazioni_str;
		int freq_visualizzazioni;
		String uri_evento_origine;
		boolean interessaAdUtente;
		boolean segnalabileAdUtente;
		
		long idEvento;
		String nomeEvento;
		String uri_evento_ontologia;
		String uri_evento;
				
		List<String> lista_uri_servizi;
		List<Servizio> lista_servizi_evento;
		
		
		String uriInteresse;
		String nomeInteresse;

		if(this.getMsgType() == TYPE_SERVICE_LIST_MSG && document!=null){
			
			//--------- Body Element ---------------
			Element body_element = (Element)document.getElementsByTagName("body").item(0);
			Element servizi_element = (Element)document.getElementsByTagName("servizi").item(0);
			NodeList nodeListServizi = servizi_element.getElementsByTagName("servizio");

			if(nodeListServizi.getLength()>0){
				
				if(serviceTable==null) serviceTable = new Hashtable<String, Servizio>();
				
				for (int i = 0; i < nodeListServizi.getLength(); i++) {
					Element servizio_element = (Element)nodeListServizi.item(i);
	
					Servizio servizio = new Servizio();
					
					String idServizio_str = getElementValue(servizio_element, "idServizio");
					if(!(idServizio_str.equalsIgnoreCase(""))){
						idServizio = Long.parseLong(idServizio_str);
						servizio.set_idServizio(idServizio);
					}
					
					nomeServizio = getElementValue(servizio_element, "nomeServizio");
					String descrizione_UTF8 = getElementValue(servizio_element,"descrizione");
					uri_servizio_ontologia = getElementValue(servizio_element, "uri_servizio_ontologia");
					uri_servizio = getElementValue(servizio_element, "uri_servizio");
					tipoServizio = getElementValue(servizio_element, "tipoServizio");
					score_str = getElementValue(servizio_element, "score");
//					feedback_str = getElementValue(servizio_element, "feedback");
//					has_feedback_str = getElementValue(servizio_element, "has_feedback");
//					freq_utilizzo_str = getElementValue(servizio_element, "frequenza_utilizzo");
//					freq_visualizzazioni_str = getElementValue(servizio_element, "frequenza_visualizzazione");
//					uri_evento_origine = getElementValue(servizio_element, "uri_evento_origine");
					
					servizio.set_nomeServizio(nomeServizio);
					
					descrizione = descrizione_UTF8;
					
					try {
						descrizione = new String(descrizione_UTF8.getBytes(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						Log.e("MessageManager",
								"parseServicesResult: ERRORE! Errore codifica caratteri");
						e.printStackTrace();
					}
					servizio.set_descrizione(descrizione);
					servizio.set_URI_Individuo_Ontologia(uri_servizio_ontologia);
					servizio.set_URI_Servizio(uri_servizio);
					servizio.set_tipoServizio(tipoServizio);
//					servizio.set_uri_evento_origine(uri_evento_origine);
										
					score = 0;
					if(score_str!=null && !(score_str.equalsIgnoreCase(""))){
						score = Float.parseFloat(score_str);
					}
					servizio.set_score(score);
					
					
//					feedback = 0;
//					if(feedback_str!=null && !(feedback_str.equalsIgnoreCase(""))){
//						feedback = Integer.parseInt(feedback_str);
//					}
//					servizio.set_livello_preferenza(feedback);
//					
//					
//					has_feedback = false;
//					if(has_feedback_str!=null && !(has_feedback_str.equalsIgnoreCase(""))){
//						has_feedback = Boolean.parseBoolean(has_feedback_str);
//					}
//					servizio.set_has_livello_preferenza(has_feedback);
					
					String interessaAdUtente_str = getElementValue(servizio_element, "interessaAdUtente");
					if(!(interessaAdUtente_str.equalsIgnoreCase(""))){
						interessaAdUtente = Boolean.parseBoolean(interessaAdUtente_str);
						servizio.set_interessaAdUtente(interessaAdUtente);
					}
					
					String segnalabileAdUtente_str = getElementValue(servizio_element, "segnalabileAdUtente");
					if(!(segnalabileAdUtente_str.equalsIgnoreCase(""))){
						segnalabileAdUtente = Boolean.parseBoolean(segnalabileAdUtente_str);
						servizio.set_segnalabileAdUtente(segnalabileAdUtente);
					}
					
					
					serviceTable.put(servizio.get_URI_Individuo_Ontologia(), servizio);
//					Log.i("MessageManager", "parseServicesResult: "+ servizio.toString());
				} //end for
			}
			
			
			//**********************************************************************
			//								EVENTI
			//**********************************************************************
			
			Element eventi_element = (Element)document.getElementsByTagName("eventi").item(0);
			NodeList nodeListEventi = eventi_element.getElementsByTagName("evento");
			
			if(nodeListEventi.getLength()>0){
				
				if(eventTable==null) eventTable = new Hashtable<String, Evento>();
				
				for (int i = 0; i < nodeListEventi.getLength(); i++) {
					Element evento_element = (Element)nodeListEventi.item(i);
	
					Evento evento = new Evento();
					
					String idEvento_str = getElementValue(evento_element, "idEvento");
					if(!(idEvento_str.equalsIgnoreCase(""))){
						idEvento = Long.parseLong(idEvento_str);
						evento.set_idEvento(idEvento);
					}
					
					nomeEvento = getElementValue(evento_element, "nomeEvento");
					String descrizione_UTF8 = getElementValue(evento_element,"descrizione");
					uri_evento_ontologia = getElementValue(evento_element, "uri_evento_ontologia");
					score_str = getElementValue(evento_element, "score");
//					feedback_str = getElementValue(evento_element, "feedback");
//					has_feedback_str = getElementValue(evento_element, "has_feedback");
					
					evento.set_nomeEvento(nomeEvento);
					
					descrizione = descrizione_UTF8;
					
					try {
						descrizione = new String(descrizione_UTF8.getBytes(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						Log.e("MessageManager",
								"parseServicesResult: ERRORE! Errore codifica caratteri");
						e.printStackTrace();
					}
					
					evento.set_descrizione(descrizione);
					evento.set_URI_Individuo_Ontologia(uri_evento_ontologia);
					
					score = 0;
					if(score_str!=null && !(score_str.equalsIgnoreCase(""))){
						score = Float.parseFloat(score_str);
					}
					evento.set_score(score);
					
//					feedback = 0;
//					if(feedback_str!=null && !(feedback_str.equalsIgnoreCase(""))){
//						feedback = Integer.parseInt(feedback_str);
//					}
//					evento.set_livello_preferenza(feedback);
//					
//					
//					has_feedback = false;
//					if(has_feedback_str!=null && !(has_feedback_str.equalsIgnoreCase(""))){
//						has_feedback = Boolean.parseBoolean(has_feedback_str);
//					}
//					evento.set_has_livello_preferenza(has_feedback);
					
					
					String interessaAdUtente_str = getElementValue(evento_element, "interessaAdUtente");
					if(!(interessaAdUtente_str.equalsIgnoreCase(""))){
						interessaAdUtente = Boolean.parseBoolean(interessaAdUtente_str);
						evento.set_interessaAdUtente(interessaAdUtente);
					}
					
					String segnalabileAdUtente_str = getElementValue(evento_element, "segnalabileAdUtente");
					if(!(segnalabileAdUtente_str.equalsIgnoreCase(""))){
						segnalabileAdUtente = Boolean.parseBoolean(segnalabileAdUtente_str);
						evento.set_segnalabileAdUtente(segnalabileAdUtente);
					}
					
					
					//-------------- Lista uri Servizi legati all'evento
					Element lista_servizi_element = (Element)evento_element.getElementsByTagName("lista_servizi").item(0);
					NodeList nodeListURIServizi = lista_servizi_element.getElementsByTagName("uri_ont_servizio_evento");
					
					lista_uri_servizi = new ArrayList<String>();
					
					lista_servizi_evento = new ArrayList<Servizio>();
					Servizio servizio;
					
					for (int j = 0; j < nodeListURIServizi.getLength(); j++) {
						String uri_ont_servizio_evento = getElementValue((Element)nodeListURIServizi.item(j), "uri_ont_servizio_evento");
						servizio = serviceTable.get(uri_ont_servizio_evento);
						if(servizio!=null){
							lista_servizi_evento.add(servizio);
						}
					}
					
					evento.set_lista_servizi(lista_servizi_evento);
					
					
					//-------------- Categorie_interesse
					NodeList nodeListCategorieInteresse = body_element.getElementsByTagName("categorie_interesse");
									
					
					eventTable.put(evento.get_URI_Individuo_Ontologia(),evento);
				}//end for
			}
		}
		
	}

	
	private String getElementValue(Element parentElement, String elementName){
		String value="";
		Element element = (Element)parentElement.getElementsByTagName(elementName).item(0);
		if(element!=null && element.hasChildNodes()){
			value = element.getFirstChild().getNodeValue().trim();
			if(value != null)
				return value;
			
			else
				value = "";
		}
		return value;
	}

//	public List<Servizio> parseServicesScore(Document document){
//		if(this.getMsgType() == TYPE_FEEDBACK_MSG && document!=null){
//
//			//--------- Body Element ---------------
//			Element body_element = (Element)document.getElementsByTagName("body").item(0);
//
//			NodeList nodeListServizi = body_element.getElementsByTagName("service");
//
//			for (int i = 0; i < nodeListServizi.getLength(); i++) {
//				Element servizio_element = (Element)nodeListServizi.item(i);
//
//				Servizio new_servizio = new Servizio();
//
//				Element service_name_element = (Element)servizio_element.getElementsByTagName("service_name").item(0);
//				String service_name_str = service_name_element.getNodeValue().trim();
//				if(service_name_str != null && !(service_name_str.equalsIgnoreCase(""))){
//					//					new_servizio.setName();
//				} 
//
//				Element service_score_element = (Element)servizio_element.getElementsByTagName("service_score").item(0);
//				String service_score_str = service_score_element.getNodeValue().trim();
//				if(service_score_str != null && !(service_score_str.equalsIgnoreCase(""))){
//					//					new_servizio.setScore();
//				} 
//
//				_listaServizi.add(new_servizio);
//			}
//
//			return _listaServizi;
//		}
//		else{
//			//ERRORE
//			return null;
//		}			
//	}


	public String getContextXmlMessage(StatoContesto status, String uri_utente_sender){

		xmlSerializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();


		try {
			xmlSerializer.setOutput(writer);
			//definisco la codifica
			xmlSerializer.startDocument("UTF-8", true);
			//definisco il tag root
			xmlSerializer.startTag("","message"); //START MESSAGE

			//--------- HEADER -----------
			xmlSerializer.startTag("", "header");

			xmlSerializer.startTag("", "uri_utente");
			xmlSerializer.text(uri_utente_sender);
			xmlSerializer.endTag("", "uri_utente");			
			
			xmlSerializer.startTag("", "timestamp");
			xmlSerializer.text(""+System.currentTimeMillis());
			xmlSerializer.endTag("", "timestamp");

			xmlSerializer.startTag("", "message_type");
			xmlSerializer.text(""+TYPE_CONTEXT_STATE_MSG);
			xmlSerializer.endTag("", "message_type");

			xmlSerializer.endTag("", "header");

			//---------- BODY -----------
			xmlSerializer.startTag("", "body");


			//LOCATION
			xmlSerializer.startTag("", "location");

			xmlSerializer.startTag("", "latitude");
			xmlSerializer.text(""+status.getLatitudine());
			xmlSerializer.endTag("", "latitude");

			xmlSerializer.startTag("", "longitude");
			xmlSerializer.text(""+status.getLongitudine());
			xmlSerializer.endTag("", "longitude");

			xmlSerializer.startTag("", "altitude");
			xmlSerializer.text(""+ status.getAltitudine());
			xmlSerializer.endTag("", "altitude");

			xmlSerializer.startTag("", "accuracy");
			xmlSerializer.text(""+ status.getAccuratezza());
			xmlSerializer.endTag("", "accuracy");

			xmlSerializer.startTag("", "type_of_place");
			xmlSerializer.text(""+ status.getTipoDiLuogo());
			xmlSerializer.endTag("", "type_of_place");
			
			xmlSerializer.startTag("","idLocationNfcTag");
			xmlSerializer.text(""+ status.getIdLocationNfcTag());
			xmlSerializer.endTag("","idLocationNfcTag");
		

			xmlSerializer.endTag("", "location");


			//MOTION
			xmlSerializer.startTag("", "motion");

			xmlSerializer.startTag("", "motion_state");
			xmlSerializer.text(""+ status.getStato_moto());
			xmlSerializer.endTag("", "motion_state");
			
			xmlSerializer.startTag("","transport");
			xmlSerializer.text(""+ status.getMezzo_utente());
			xmlSerializer.endTag("","transport");

			xmlSerializer.startTag("", "speed");
			xmlSerializer.text(""+ status.getVelocita());
			xmlSerializer.endTag("", "speed");

			xmlSerializer.endTag("", "motion");

			//-------------
			xmlSerializer.endTag("", "body");

			xmlSerializer.endTag("", "message"); //END MESSAGE

			xmlSerializer.endDocument();

			return writer.toString();

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;	
	}

	public String getEventsAndServicesXmlMessage(Hashtable<String,Servizio> serviceTable, Hashtable<String,Evento> eventTable, String uri_utente_sender){
		xmlSerializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();


		try {
			xmlSerializer.setOutput(writer);
			//definisco la codifica
			xmlSerializer.startDocument("UTF-8", true);
			//definisco il tag root
			xmlSerializer.startTag("","message"); //START MESSAGE

			//--------- HEADER -----------
			xmlSerializer.startTag("", "header");

			xmlSerializer.startTag("", "uri_utente");
			xmlSerializer.text(uri_utente_sender);
			xmlSerializer.endTag("", "uri_utente");		
			
			xmlSerializer.startTag("", "timestamp");
			xmlSerializer.text(""+System.currentTimeMillis());
			xmlSerializer.endTag("", "timestamp");

			xmlSerializer.startTag("", "message_type");
			xmlSerializer.text(""+TYPE_FEEDBACK_MSG);
			xmlSerializer.endTag("", "message_type");

			xmlSerializer.endTag("", "header");

			//---------- BODY -----------
			xmlSerializer.startTag("","body");
			
			//************************* NODO Servizi *****************************
			xmlSerializer.startTag("","servizi");
			
			Enumeration<String> serviceKeys = serviceTable.keys();
			Servizio servizio;
			while(serviceKeys.hasMoreElements()){
				String key = serviceKeys.nextElement();
				servizio = serviceTable.get(key);
				
				//NODO servizio
				xmlSerializer.startTag("","servizio");
				
								
				//NODO uri_servizio_ontologia
				xmlSerializer.startTag("","uri_servizio_ontologia");
				xmlSerializer.text(""+ servizio.get_URI_Individuo_Ontologia());
				xmlSerializer.endTag("","uri_servizio_ontologia");
				
				
				//NODO feedback
				xmlSerializer.startTag("","feedback");
				xmlSerializer.text(""+ servizio.get_livello_preferenza());
				xmlSerializer.endTag("","feedback");
				
								
				
				xmlSerializer.endTag("","servizio");
			}
			
			xmlSerializer.endTag("","servizi");  //END Servizi
			
			
			//************************* NODO Eventi *****************************
			xmlSerializer.startTag("","eventi");
			
			Enumeration<String> eventKeys = eventTable.keys();
			Evento evento;
			while(eventKeys.hasMoreElements()){
				String key = eventKeys.nextElement();
				evento = eventTable.get(key);
				
				//NODO evento
				xmlSerializer.startTag("","evento");
				
				//NODO uri_evento_ontologia
				xmlSerializer.startTag("","uri_evento_ontologia");
				xmlSerializer.text(""+ evento.get_URI_Individuo_Ontologia());
				xmlSerializer.endTag("","uri_evento_ontologia");
				
				xmlSerializer.startTag("","feedback");
				xmlSerializer.text(""+ evento.get_livello_preferenza());
				xmlSerializer.endTag("","feedback");
				
				xmlSerializer.endTag("","evento");
							
			}

			xmlSerializer.endTag("","eventi");

			xmlSerializer.endTag("","body");
			
			xmlSerializer.endTag("", "message"); //END MESSAGE

			xmlSerializer.endDocument();

			return writer.toString();

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;		

			
	}
	
	
	public String getEventPreferenceXmlMessage(Evento evento, String uri_utente_sender){
		xmlSerializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();


		try {
			xmlSerializer.setOutput(writer);
			//definisco la codifica
			xmlSerializer.startDocument("UTF-8", true);
			//definisco il tag root
			xmlSerializer.startTag("","message"); //START MESSAGE

			//--------- HEADER -----------
			xmlSerializer.startTag("", "header");

			xmlSerializer.startTag("", "uri_utente");
			xmlSerializer.text(uri_utente_sender);
			xmlSerializer.endTag("", "uri_utente");		
			
			xmlSerializer.startTag("", "timestamp");
			xmlSerializer.text(""+System.currentTimeMillis());
			xmlSerializer.endTag("", "timestamp");

			xmlSerializer.startTag("", "message_type");
			xmlSerializer.text(""+TYPE_EVENT_PREFERENCE_MSG);
			xmlSerializer.endTag("", "message_type");

			xmlSerializer.endTag("", "header");

			//---------- BODY -----------
			xmlSerializer.startTag("","body");
			
			//************************* NODO Servizi *****************************
			
			
			//NODO evento
			xmlSerializer.startTag("","evento");
			
			//NODO uri_evento_ontologia
			xmlSerializer.startTag("","uri_evento_ontologia");
			xmlSerializer.text(""+ evento.get_URI_Individuo_Ontologia());
			xmlSerializer.endTag("","uri_evento_ontologia");
			
			xmlSerializer.startTag("","livello_preferenza");
			xmlSerializer.text(""+ evento.get_livello_preferenza());
			xmlSerializer.endTag("","livello_preferenza");
			
			xmlSerializer.endTag("","evento");
			
			xmlSerializer.endTag("","body");
			
			xmlSerializer.endTag("", "message"); //END MESSAGE

			xmlSerializer.endDocument();

			return writer.toString();

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;		

			
	}
	
	
	public String getServicePreferenceXmlMessage(Servizio servizio, String uri_utente_sender){
		xmlSerializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();


		try {
			xmlSerializer.setOutput(writer);
			//definisco la codifica
			xmlSerializer.startDocument("UTF-8", true);
			//definisco il tag root
			xmlSerializer.startTag("","message"); //START MESSAGE

			//--------- HEADER -----------
			xmlSerializer.startTag("", "header");

			xmlSerializer.startTag("", "uri_utente");
			xmlSerializer.text(uri_utente_sender);
			xmlSerializer.endTag("", "uri_utente");		
			
			xmlSerializer.startTag("", "timestamp");
			xmlSerializer.text(""+System.currentTimeMillis());
			xmlSerializer.endTag("", "timestamp");

			xmlSerializer.startTag("", "message_type");
			xmlSerializer.text(""+TYPE_SERVICE_PREFERENCE_MSG);
			xmlSerializer.endTag("", "message_type");

			xmlSerializer.endTag("", "header");

			//---------- BODY -----------
			xmlSerializer.startTag("","body");
			
			//************************* NODO Servizi *****************************
			
			
			//NODO evento
			xmlSerializer.startTag("","servizio");
			
			//NODO uri_evento_ontologia
			xmlSerializer.startTag("","uri_servizio_ontologia");
			xmlSerializer.text(""+ servizio.get_URI_Individuo_Ontologia());
			xmlSerializer.endTag("","uri_servizio_ontologia");
			
			xmlSerializer.startTag("","livello_preferenza");
			xmlSerializer.text(""+ servizio.get_livello_preferenza());
			xmlSerializer.endTag("","livello_preferenza");
			
			xmlSerializer.endTag("","servizio");
			
			xmlSerializer.endTag("","body");
			
			xmlSerializer.endTag("", "message"); //END MESSAGE

			xmlSerializer.endDocument();

			return writer.toString();

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;		

			
	}
	
	
	public Servizio parseServiceScore(Document document){
		if(this.getMsgType() == TYPE_SERVICE_SCORE_MSG && document!=null){

			//--------- Body Element ---------------
			Element body_element = (Element)document.getElementsByTagName("body").item(0);

			Element servizio_element = (Element)body_element.getElementsByTagName("servizio").item(0);
	
			Servizio new_servizio = new Servizio();

			Element service_uri_element = (Element)servizio_element.getElementsByTagName("uri_servizio_ontologia").item(0);
			String service_uri_str = service_uri_element.getFirstChild().getNodeValue().trim();
			if(service_uri_str != null && !(service_uri_str.equalsIgnoreCase(""))){
				new_servizio.set_URI_Individuo_Ontologia(service_uri_str);
			} 

			Element service_score_element = (Element)servizio_element.getElementsByTagName("score").item(0);
			String service_score_str = service_score_element.getFirstChild().getNodeValue().trim();
			if(service_score_str != null && !(service_score_str.equalsIgnoreCase(""))){
				new_servizio.set_score(Float.parseFloat(service_score_str));
			} 

			return new_servizio;
		}
		else{
			//ERRORE
			return null;
		}			
	}
	
	
	public Evento parseEventScore(Document document){
		if(this.getMsgType() == TYPE_EVENT_SCORE_MSG && document!=null){

			//--------- Body Element ---------------
			Element body_element = (Element)document.getElementsByTagName("body").item(0);

			Element servizio_element = (Element)body_element.getElementsByTagName("evento").item(0);
	
			Evento new_evento = new Evento();

			Element event_uri_element = (Element)servizio_element.getElementsByTagName("uri_evento_ontologia").item(0);
			String event_uri_str = event_uri_element.getFirstChild().getNodeValue().trim();
			if(event_uri_str != null && !(event_uri_str.equalsIgnoreCase(""))){
				new_evento.set_URI_Individuo_Ontologia(event_uri_str);
			} 

			Element event_score_element = (Element)servizio_element.getElementsByTagName("score").item(0);
			String event_score_str = event_score_element.getFirstChild().getNodeValue().trim();
			if(event_score_str != null && !(event_score_str.equalsIgnoreCase(""))){
				new_evento.set_score(Float.parseFloat(event_score_str));
			} 

			return new_evento;
		}
		else{
			//ERRORE
			return null;
		}			
	}

}


