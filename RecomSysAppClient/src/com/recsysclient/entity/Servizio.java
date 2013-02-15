package com.recsysclient.entity;


import java.util.ArrayList;
import java.util.List;

public class Servizio implements Comparable<Servizio>{

	private long _idServizio;
	
	//URI che indentifica l'individuo di tipo "Servizio" nell'ontologia
	private String _URI_Individuo_Ontologia;
	
	//URI relativa al servizio che deve essere esplicitato
	private String _URI_Servizio;
	
	//nome del servizio così come verrà visualizzato all'utente
	private String _nomeServizio;
	
	//Descrizione del Servizio
	private String _descrizione;
	
	//contiene l'evento dal quale ha avuto origine il suggerimento che ha permesso 
	//di richiamare il servizio.
	private String _uri_evento_origine;
	
	//lista degli interessi che il servizio soddisfa
//	private List<Interesse> _lista_interessi;
	
	
	//tipo di servizio (App, Web, Comando domotico)
	private String _tipoServizio;

	private boolean _segnalabileAdUtente;

	//utile al client per l'ordinamento dei servizi da proporre
	private float _score;
	
	//utili per il server per aggiornare l'ontologia dopo il feedback dell'utente  
	//e per calcolare lo score dopo la richista
	
	private boolean _has_livello_preferenza = false;
	private int _livello_preferenza;
	
	public int get_livello_preferenza() {
		return _livello_preferenza;
	}

	public void set_livello_preferenza(int _livello_preferenza) {
		this._livello_preferenza = _livello_preferenza;
	}
	
	public boolean get_has_livello_preferenza() {
		return _has_livello_preferenza;
	}

	public void set_has_livello_preferenza(boolean has_feedback) {
		this._has_livello_preferenza = has_feedback;
	}
	
	public float get_score() {
		return _score;
	}

	public void set_score(float _score) {
		this._score = _score;
	}

		
	private boolean _visualizzato = false;
	
	public boolean is_visualizzato() {
		return _visualizzato;
	}

	public void set_visualizzato(boolean _visualizzato) {
		this._visualizzato = _visualizzato;
	}
	
		
	public String get_uri_evento_origine() {
		return _uri_evento_origine;
	}

	public void set_uri_evento_origine(String _uri_evento_origine) {
		this._uri_evento_origine = _uri_evento_origine;
	}
	
	

	public String get_tipoServizio() {
		return _tipoServizio;
	}

	public void set_tipoServizio(String _tipoServizio) {
		this._tipoServizio = _tipoServizio;
	}

	public boolean is_segnalabileAdUtente() {
		return _segnalabileAdUtente;
	}

	public void set_segnalabileAdUtente(boolean _segnalabileAdUtente) {
		this._segnalabileAdUtente = _segnalabileAdUtente;
	}

	public boolean is_interessaAdUtente() {
		return _interessaAdUtente;
	}

	public void set_interessaAdUtente(boolean _interessaAdUtente) {
		this._interessaAdUtente = _interessaAdUtente;
	}

	private boolean _interessaAdUtente;
	
	
	public Servizio(){
//		_lista_interessi = new ArrayList<Interesse>();
		_score = 0;
	}
	
	public long get_idServizio() {
		return _idServizio;
	}

	public void set_idServizio(long _idServizio) {
		this._idServizio = _idServizio;
	}

	public String get_URI_Individuo_Ontologia() {
		return _URI_Individuo_Ontologia;
	}

	public void set_URI_Individuo_Ontologia(String _URI_Individuo_Ontologia) {
		this._URI_Individuo_Ontologia = _URI_Individuo_Ontologia;
	}

	public String get_URI_Servizio() {
		return _URI_Servizio;
	}

	public void set_URI_Servizio(String _URI_Servizio) {
		this._URI_Servizio = _URI_Servizio;
	}

	public String get_nomeServizio() {
		return _nomeServizio;
	}

	public void set_nomeServizio(String _nomeServizio) {
		this._nomeServizio = _nomeServizio;
	}

	public String get_descrizione() {
		return _descrizione;
	}

	public void set_descrizione(String _descrizione) {
		this._descrizione = _descrizione;
	}

//	public List<Interesse> get_lista_interessi() {
//		return _lista_interessi;
//	}
//
//	public void set_lista_interessi(List<Interesse> _lista_interessi) {
//		this._lista_interessi = _lista_interessi;
//	}

	@Override
	public String toString(){
		String str = "";
		
		str += "TipoServizio: " + _tipoServizio +";URI_Individuo: " + _URI_Individuo_Ontologia + "URI_Servizio:" + _URI_Servizio + "; nome Servizio:" + _nomeServizio +
		"\n Descrizione: " + _descrizione + "\n";
		
		if(_segnalabileAdUtente) str += "E' SEGNALABILE ALL'UTENTE \n";
		if(_interessaAdUtente) str += "E' DI INTERESSE PER L'UTENTE \n";
		
		return str;
	}

	@Override
	public int compareTo(Servizio s) {
		if(!(s instanceof Servizio)){
			throw new ClassCastException("Non è un oggetto di tipo Servizio");
		}
		
		Servizio servizio = (Servizio) s;
		if(this.get_score() > servizio.get_score()){
			return -1;
		} else if(this.get_score() < servizio.get_score()){
			return 1;
		}else{
			return 0;
		}
	}
	
}
