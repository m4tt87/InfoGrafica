package com.recsysclient.utility;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.util.Log;

import com.recsysclient.entity.Evento;
import com.recsysclient.entity.Servizio;

public class AppCommonVar {
	private static boolean invia_richieste;
	
	
	private static String _uri_utente = "Utente_01";
	
	
	private static List<Evento> _lista_eventi;
	
	private static List<Servizio> _lista_servizi;
	
	//hashtable contenenti i servizi suggeriti all'utente e permetterà di tenere traccia degli eventi già visualizzati o meno
	private static Hashtable<String, Evento> _table_eventi_suggeriti = new Hashtable<String, Evento>();
	private static Hashtable<String, Servizio> _table_servizi_suggeriti = new Hashtable<String, Servizio>();
	
	private static List<Servizio> _lista_servizi_filtrata;
	private static List<Evento> _lista_eventi_filtrata;
	
	public static void updateScoreEvento(String uri_evento,float score){
		Evento e = _table_eventi_suggeriti.get(uri_evento);
		e.set_score(score);
		Log.i("AppCommonVar", "updateScoreEvento: CANCELLAMI: score evento = "+ score);
		e.set_has_livello_preferenza(true);
	}
	
	public static void updateScoreServizio(String uri_servizio,float score){
		Servizio s = _table_servizi_suggeriti.get(uri_servizio);
		s.set_score(score);
	}
	
	public static Hashtable<String, Evento> get_table_eventi_suggeriti() {
		return _table_eventi_suggeriti;
	}
	
	public static Hashtable<String, Servizio> get_table_servizi_suggeriti() {
		return _table_servizi_suggeriti;
	}
	
	public static boolean isInvia_richieste() {
		return invia_richieste;
		
	}
	public static void setInvia_richieste(boolean invia_richieste) {
		AppCommonVar.invia_richieste = invia_richieste;
	}
	
	
	public static List<Evento> getLista_eventi() {
		return _lista_eventi;
	}
	
	public static String get_uri_utente() {
		return _uri_utente;
	}

	public static void set_uri_utente(String uri_utente) {
		_uri_utente = uri_utente;
	}
	
	public static void setLista_servizi(List<Servizio> lista_nuovi_servizi) {
		AppCommonVar._lista_servizi = new ArrayList<Servizio>();
		
		if(lista_nuovi_servizi != null && lista_nuovi_servizi.size()>0){
			for(Servizio nuovo_servizio:lista_nuovi_servizi){
				//se la tabella servizi non contiene l'servizio della lista...
				if((!_table_servizi_suggeriti.containsKey(nuovo_servizio.get_URI_Individuo_Ontologia()))){
					//... il servizio non è mai stato suggerito e lo aggiungo alla tabella
					_table_servizi_suggeriti.put(nuovo_servizio.get_URI_Individuo_Ontologia(), nuovo_servizio);
					AppCommonVar._lista_servizi.add(nuovo_servizio);
					Log.i("AppCommonVar", "setLista_servizi: aggiungo nuovo servizio suggerito: " + nuovo_servizio.get_URI_Individuo_Ontologia());
				}
				else{
					//... il servizio è stato già suggerito quindi nella lista_eventi inseriso l'istanza presente nella Hashtable
					Servizio s = _table_servizi_suggeriti.get(nuovo_servizio.get_URI_Individuo_Ontologia());
					s.set_score(nuovo_servizio.get_score());
					AppCommonVar._lista_servizi.add(s);
//					AppCommonVar._lista_servizi.add(_table_servizi_suggeriti.get(nuovo_servizio.get_URI_Individuo_Ontologia()));
					Log.i("AppCommonVar", "setLista_servizi: aggiungo servizio già suggerito: " + nuovo_servizio.get_URI_Individuo_Ontologia());
				}					
			}
			
		}
	}
	
	public static void setLista_eventi(List<Evento> lista_nuovi_eventi) {
		//creo una nuova istanza della lista alla quale aggiungerò man mano gli elementi
		AppCommonVar._lista_eventi = new ArrayList<Evento>();
		
		if(lista_nuovi_eventi != null && lista_nuovi_eventi.size()>0){
			for(Evento nuovo_evento:lista_nuovi_eventi){
				//se la tabella eventi non contiene l'evento della lista...
				if((!_table_eventi_suggeriti.containsKey(nuovo_evento.get_URI_Individuo_Ontologia()))){
					//... l'evento non è mai stato suggerito e lo aggiungo alla tabella
					_table_eventi_suggeriti.put(nuovo_evento.get_URI_Individuo_Ontologia(), nuovo_evento);
					AppCommonVar._lista_eventi.add(nuovo_evento);
					Log.i("AppCommonVar", "setLista_eventi: aggiungo nuovo evento suggerito: " + nuovo_evento.get_URI_Individuo_Ontologia());
				}
				else{
					//... l'evento è stato già suggerito quindi nella lista_eventi inseriso l'istanza presente nella Hashtable
					Evento e = _table_eventi_suggeriti.get(nuovo_evento.get_URI_Individuo_Ontologia());
					e.set_score(nuovo_evento.get_score());
					AppCommonVar._lista_eventi.add(_table_eventi_suggeriti.get(nuovo_evento.get_URI_Individuo_Ontologia()));
					Log.i("AppCommonVar", "setLista_eventi: aggiungo evento già suggerito: " + nuovo_evento.get_URI_Individuo_Ontologia());
				}					
			}
			
		}
	}
	
	
	public static List<Servizio> get_lista_servizi_filtrata() {
		List<Servizio> filtered_service_list = null;
		if(_lista_servizi != null || _lista_servizi.size()>0){
			filtered_service_list = new ArrayList<Servizio>();
			for(Servizio servizio:_lista_servizi){
				//se la tabella eventi contiene l'evento della lista...
				if(!servizio.is_visualizzato()){
					//... l'evento non è mai stato visualizzato
					filtered_service_list.add(servizio);
					Log.i("AppCommonVar", "getLista_eventi_filtrata: " + servizio.get_URI_Individuo_Ontologia());
				}								
			}
			_lista_servizi = filtered_service_list;
		}
		
		return _lista_servizi; 
	}
	
	
	//Restituisce la lista degli eventi restituiti dal sistema ma non ancora visualizzati dall'utente
	public static List<Evento> getLista_eventi_filtrata(){
		List<Evento> filtered_event_list = null;
		if(_lista_eventi != null || _lista_eventi.size()>0){
			filtered_event_list = new ArrayList<Evento>();
			for(Evento evento:_lista_eventi){
				//se la tabella eventi contiene l'evento della lista...
				if(!evento.is_visualizzato()){
					//... l'evento non è mai stato visualizzato
					filtered_event_list.add(evento);
					Log.i("AppCommonVar", "getLista_eventi_filtrata: " + evento.get_URI_Individuo_Ontologia());
				}								
			}
			_lista_eventi = filtered_event_list;
		}
		
		return _lista_eventi; 
	}
			

	
	public static List<Servizio> getLista_servizi() {
		return _lista_servizi;
	}
	
	
	public static void resetResulList() {
		_lista_eventi = null;
		_lista_servizi = null;
		
	}
	
	
	public static void set_lista_eventi_filtrata(List<Evento> _lista_eventi_filtrata) {
		AppCommonVar._lista_eventi_filtrata = _lista_eventi_filtrata;
	}
	
	
	
	
	public static void set_lista_servizi_filtrata(List<Servizio> _lista_servizi_filtrata) {
		AppCommonVar._lista_servizi_filtrata = _lista_servizi_filtrata;
	}
	
	
}
