package com.recsysclient.entity;


import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;


public class Evento implements Comparable<Evento>{
	private long _idEvento;
	
	//URI che indentifica l'individuo di tipo "Evento" nell'ontologia
	private String _URI_Individuo_Ontologia;
	
	//URI relativa all' Evento che deve essere esplicitato
	private String _URI_Evento;
	
	//nome dell'Evento così come verrà visualizzato all'utente
	private String _nomeEvento;
	
	//Descrizione dell' Evento
	private String _descrizione;
	
	private boolean _segnalabileAdUtente = false;
	
	private boolean _interessaAdUtente = false;
	
	
	//lista dei servizi legati all'evento
	List<Servizio> _lista_servizi;
	
	private boolean _visualizzato = false;
	
	//utile al client per l'ordinamento degli eventi da proporre
	private float _score;
	
	//utile al server per aggiornare il livello di interesse degli interessi relativi a tale evento
	private int _livello_preferenza;
	private boolean _has_livello_preferenza = false;
	
	public boolean get_has_livello_preferenza() {
		return _has_livello_preferenza;
	}

	public void set_has_livello_preferenza(boolean has_livello_preferenza) {
		this._has_livello_preferenza = has_livello_preferenza;
	}

	public int get_livello_preferenza() {
		return _livello_preferenza;
	}

	public void set_livello_preferenza(int livello_preferenza) {
		this._livello_preferenza = livello_preferenza;
	}
	
	public float get_score() {
		return _score;
	}

	public void set_score(float _score) {
		this._score = _score;
	}
	
	
	public boolean is_visualizzato() {
		return _visualizzato;
	}

	public void set_visualizzato(boolean _visualizzato) {
		this._visualizzato = _visualizzato;
	}

	
	public Evento(){
		_lista_servizi = new ArrayList<Servizio>();
		_score = 0;
	}

	public long get_idEvento() {
		return _idEvento;
	}

	public void set_idEvento(long _idEvento) {
		this._idEvento = _idEvento;
	}

	public String get_URI_Individuo_Ontologia() {
		return _URI_Individuo_Ontologia;
	}

	public void set_URI_Individuo_Ontologia(String _URI_Individuo_Ontologia) {
		this._URI_Individuo_Ontologia = _URI_Individuo_Ontologia;
	}

	public String get_URI_Evento() {
		return _URI_Evento;
	}

	public void set_URI_Evento(String _URI_Evento) {
		this._URI_Evento = _URI_Evento;
	}

	public String get_nomeEvento() {
		return _nomeEvento;
	}

	public void set_nomeEvento(String _nomeEvento) {
		this._nomeEvento = _nomeEvento;
	}

	public String get_descrizione() {
		return _descrizione;
	}

	public void set_descrizione(String _descrizione) {
		this._descrizione = _descrizione;
	}

	public List<Servizio> get_lista_servizi() {
		return _lista_servizi;
	}

	public void set_lista_servizi(List<Servizio> _lista_servizi) {
		this._lista_servizi = _lista_servizi;
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
	
	@Override
	public String toString(){
		String str = "";
		str += "idEvento: "+_idEvento + "; URI_Individuo_Ontologia: " + _URI_Individuo_Ontologia + "; URI_Evento:" + _URI_Evento + "; nome Evento: " + _nomeEvento +
		"\n Descrizione: " + _descrizione + "\n";
		if(_segnalabileAdUtente) str += "E' SEGNALABILE ALL'UTENTE \n";
		if(_interessaAdUtente) str += "E' DI INTERESSE PER L'UTENTE \n";
					
		if(_lista_servizi!=null && _lista_servizi.size()>0){
			str += "nessun servizio legato a questo evento";
			for(Servizio s: _lista_servizi){
				str+="  -"+ s.get_nomeServizio();
			}
		}
		else{
			str += "nessun servizio legato a questo evento";
		}
		return str;
	}

	@Override
	public int compareTo(Evento e) {
		if(!(e instanceof Evento)){
			throw new ClassCastException("Non è un oggetto di tipo Evento");
		}
		
		Evento evento = (Evento) e;
		if(this.get_score() > evento.get_score()){
			return -1;
		} else if(this.get_score() < evento.get_score()){
			return 1;
		}else{
			return 0;
		}
	}

	
}
