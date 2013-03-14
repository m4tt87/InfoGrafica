package com.recsysclient.location;


import java.util.Hashtable;
import java.util.List;

import com.recsysclient.entity.Valore;
import com.recsysclient.entity.ValoreDecimale;
import com.recsysclient.entity.ValoreVettore;
import com.recsysclient.utility.AppDictionary;

import android.R.bool;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class MyLocationListener implements LocationListener {

	
	//Destinata a contenere tutte le informazioni reperibili dal Location Provider ovvero:
	// - stato del GPS: eventualmente fosse spento si può suggerire all'utente di accenderlo
	// - coordinate
	// - accuratezza della posizione rilevata (in [m])
	// - velocità (in [m/s])
	Hashtable<Integer, Valore> _location_table;
	
	

	//la classe Location manager permette l'accesso ai servizi di localizzazione offerti da Android
	private LocationManager _locationManager;
	
	/*la classe LocationProvider fornisce report periodici sulla locazione geografica del device. 
    Ogni provider ha un insieme di criteri attraverso i quali è possibile utilizzarlo.Alcuni 
	richiedono hardware GPS e visibilità ad un certo numero di satelliti, altri necessitano della rete cellulare,
	oppure l'accesso ad una rete o internet. Ogni tipologia di provider comporta differenti costi 
	dal punto di vista del consumo di batteria o dal punto di vista monetario */
	private LocationProvider _provider;
	
	private String providerFine, providerCoarse,lastLocationProvider;
	
	private Location _location;
	
	//Lista di 3 elementi. Contiene nell'ordine:
	// >  _coord[0] -> latitudine
	// >  _coord[1] -> longitudine
	// >  _coord[2] -> altitudine
	private float[] _coord;
	
	//Accuratezza della posizione [in metri]
	private float _accuracy;
	
	//Variabile booleana per lo stato del GPS (attivato/disattivato)
	// Se == LocationProvider.OUT_OF_SERVICE --> GPS disabled
	// Se == LocationProvider.TEMPORARILY_UNAVAILABLE --> GPS enabled & position NOT available
	// Se == LocationProvider.AVAILABLE --> GPS enabled & position available 
	private float _gps_status;
	
	//Variabile destinata a conservare l'informazione riguardo la velocità ( in [m/s]):
	//Se == -1 --> velocità non rilevata;
	private float _speed;
	
	//context del servizio che utilizza tale listener
	private Context _context;
	
	private String _locationProvider;
	
	
	public String get_locationProvider() {
		return _locationProvider;
	}

	public float[] getCoord() {
		return _coord.clone();
	}

	public float get_gps_status() {
		ValoreDecimale v = ( ValoreDecimale)_location_table.get(AppDictionary.KEY_GPS_STATUS);
		if(v!= null)
			_gps_status = (float) v.getValore();
		return _gps_status;
	}

	public float get_speed() {
		return _speed;
	}

	public float get_accuracy() {
		return _accuracy;
	}
	
	public Hashtable<Integer, Valore> get_location_table() {
		return _location_table;
	}
	
	//Costruttore
	public MyLocationListener(Context context){ 
		_context = context;
		_coord = new float[3];
	}
	
	//Inizializza l'hashtable contenente i dati ricavati dai location provider (GPS e Network) e abilita il presente listener (this) ad aggiornare tali dati
	public void registerLocationListener(){
		_locationManager = (LocationManager)_context.getSystemService(Context.LOCATION_SERVICE);
		
		Criteria criteria = new Criteria();
		criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        
        //seleziono il provider più accurato (dovrebbe essere il GPS)
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		providerFine = _locationManager.getBestProvider(criteria, true);
		
		//seleziono il provider più grossolano (dovrebbe essere 'Network')
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		providerCoarse = _locationManager.getBestProvider(criteria, true);
		
		//inizializzo la _location_table
		inizializeLocationTable();
		
		if (providerFine != null && providerCoarse != null && providerFine.equals(providerCoarse)) providerCoarse = null;
             
        if (providerCoarse != null){
//        	Log.i("MyLocationListener", "registerLocationListener: providerCoarse = " + providerCoarse);
        	//REGISTRO IL LOCATION LISTENER PER IL LOCATION PROVIDER "GROSSOLAN" (Network)
        	_locationManager.requestLocationUpdates(providerCoarse, 0, 0, this);
        }
       
        if (providerFine != null){
//        	Log.i("MyLocationListener", "registerLocationListener: providerFine = " + providerFine);
        	
        	//REGISTRO IL LOCATION LISTENER PER IL LOCATION PROVIDER "FINE" (GPS)
        	_locationManager.requestLocationUpdates(providerFine, 0, 0, this);
        	boolean gpsEnabled = _locationManager.isProviderEnabled(providerFine);
			if (gpsEnabled && providerFine.equalsIgnoreCase(LocationManager.GPS_PROVIDER)) {
				(( ValoreDecimale)_location_table.get(AppDictionary.KEY_GPS_STATUS)).setValore(AppDictionary.GPS_ON);
			} else {
				(( ValoreDecimale)_location_table.get(AppDictionary.KEY_GPS_STATUS)).setValore(AppDictionary.GPS_OFF);
			}
        }
        else{
        	(( ValoreDecimale)_location_table.get(AppDictionary.KEY_GPS_STATUS)).setValore(AppDictionary.GPS_NOT_AVAILABLE);
        }
        
        
	}
	
	
	//Disabilita il presente listener (this) ad ascoltare e quindi aggiornare i dati provenienti dai location provider (GPS e Network)
	public void unregisterLocationListener(){
		if(_locationManager!=null)
		_locationManager.removeUpdates(this);
	}
	
	
	private void inizializeLocationTable(){
		_location_table = new Hashtable<Integer, Valore>();
		
		ValoreVettore valVet = new ValoreVettore();
		float[] coord = {-1f, -1f ,-1f};
		valVet.setValore(coord);
		_location_table.put(AppDictionary.KEY_LOCATION_COORD,valVet);
		
		ValoreDecimale valDec = new ValoreDecimale();
		_location_table.put(AppDictionary.KEY_LOCATION_ACCURACY,valDec);

		valDec = new ValoreDecimale();
		valDec.setValore(-1);
		_location_table.put(AppDictionary.KEY_LOCATION_SPEED,valDec);

		valDec = new ValoreDecimale();
		_location_table.put(AppDictionary.KEY_GPS_STATUS,valDec);
	}
		
	//-------------------------------------- Metodi del Location Listener -------------------------------------
	@Override
	public void onLocationChanged(Location location) {
		//latitudine
		_coord[0] = (float)location.getLatitude();
		
		//longitudine
		_coord[1] = (float)location.getLongitude();
		
		//altitudine
		if (location.hasAltitude()) {
			_coord[2] = (float)location.getAltitude();
		}
		else{
			_coord[2] = -1;
		}
		
		((ValoreVettore)_location_table.get(AppDictionary.KEY_LOCATION_COORD)).setValore(_coord);
		
		//accuratezza
		_accuracy = (float)location.getAccuracy();
		((ValoreDecimale)_location_table.get(AppDictionary.KEY_LOCATION_ACCURACY)).setValore(_accuracy);
		Log.e("MyLocationListener","accuracy="+_accuracy);
		
		//velocità
		if (location.hasSpeed()) {
			_speed = location.getSpeed();
		}
		else{
			_speed = -1;
		}
		((ValoreDecimale)_location_table.get(AppDictionary.KEY_LOCATION_SPEED)).setValore(_speed);
		
		_locationProvider = location.getProvider();
	}

	@Override
	public void onProviderDisabled(String provider) {
		// GPS not available
		if(provider.equalsIgnoreCase(LocationManager.GPS_PROVIDER)){
			_gps_status = AppDictionary.GPS_OFF;
			((ValoreDecimale)_location_table.get(AppDictionary.KEY_GPS_STATUS)).setValore(_gps_status);
		}
	}

	@Override
	public void onProviderEnabled(String provider) {
		// GPS available
		if(provider.equalsIgnoreCase(LocationManager.GPS_PROVIDER)){
			_gps_status = AppDictionary.GPS_ON;
			((ValoreDecimale)_location_table.get(AppDictionary.KEY_GPS_STATUS)).setValore(_gps_status);
		}

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		if (status == LocationProvider.AVAILABLE) {
			if(_locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
				_gps_status = AppDictionary.GPS_ON;
			else
				_gps_status = AppDictionary.GPS_OFF;
		} 
		else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
			_gps_status = AppDictionary.GPS_OFF;
		}
		else {
			_gps_status = AppDictionary.GPS_NOT_AVAILABLE;
		}
		((ValoreDecimale)_location_table.get(AppDictionary.KEY_GPS_STATUS)).setValore(_gps_status);
		
	}
	
	

}
