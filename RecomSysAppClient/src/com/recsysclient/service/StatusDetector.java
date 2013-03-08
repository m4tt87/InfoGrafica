package com.recsysclient.service;

import java.math.RoundingMode;
import java.security.acl.LastOwnerException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender.SendIntentException;
import android.location.GpsStatus;
import android.media.AudioManager;
import android.text.InputFilter.LengthFilter;
import android.util.Log;

import com.recsysclient.device_use.DeviceUseMonitor;
import com.recsysclient.device_use.ScreenReceiver;
import com.recsysclient.entity.Evento;
import com.recsysclient.entity.Servizio;
import com.recsysclient.entity.StatoContesto;
import com.recsysclient.entity.Valore;
import com.recsysclient.entity.ValoreDecimale;
import com.recsysclient.entity.ValoreVettore;
import com.recsysclient.location.MyLocationListener;
import com.recsysclient.messaging.MessageManager;
import com.recsysclient.network.HttpMessageClient;
import com.recsysclient.network.ServiceSocket;
import com.recsysclient.sensor.MySensorListener;
import com.recsysclient.utility.AppCommonVar;
import com.recsysclient.utility.AppDictionary;
import com.recsysclient.utility.AppUtils;
import com.recsysclient.utility.Setting;

public class StatusDetector {

	private Context _context;

	private SamplesWindow _samplesWindow;
	
	//Listener che monitora i sensori di interesse
	private MySensorListener _mySensorListener;
	
	//Listener che monitora i locationProvider (GPS e Network)	
	private MyLocationListener _myLocationListener;

	
	
	// Sarà un Valore di tipo decimale destinato a contenere lo stato del
	// display
	private ValoreDecimale _valore_screen_status;

	
	// La seguente HashTable contiene una chiave di tipo String contenente il
	// nome identificativo
	// di un determinato 'Valore', ad esempio "posizione", "velocità", oppure il
	// nome del sensore
	private Hashtable<Integer, Valore> _main_table;

	// Monitora l'utilizzo dello smartphone
	private DeviceUseMonitor _deviDeviceUseMonitor;

	// gestisce il task (task scheduler)
	private Timer _timer;

	// intervallo di campionamento
	private int _interval_ms;

	// ritardo prima della lettura (in genere = 0)
	private int _delay;

	// dimensione della finestra di campionamento
	private int _sampleWindowLenght;

	

//	//Oggetto contenente il contesto
//	StatoContesto _statoContesto;
	
	private static StatusDetector istanza = null;
	
	// Metodo della classe impiegato per accedere al Singleton
	public static synchronized StatusDetector getInstance(Context context) {
		if (istanza == null)
			istanza = new StatusDetector(context);
		return istanza;
	}
	
	// --------------- COSTRUTTORE --------------
	private StatusDetector(Context context) {

		this._context = context;
		
		// ------------- SENSOR LISTENER --------------
		//inizializzo il listener dei sensori
		_mySensorListener = new MySensorListener(context);
		
		
		
		// ------------- DEVICE USE MONITOR --------------
		// inizializzo il monitor che mantiene aggiornato lo stato di utilizzo dello smartphone 

		_deviDeviceUseMonitor = new DeviceUseMonitor(context);

		// ------------- LOCATION MANAGER------------------
		// _locationManager = (LocationManager)
		// getSystemService(LOCATION_SERVICE);
		// _gpsProvider =
		// _locationManager.getProvider(LocationManager.GPS_PROVIDER);
		
		_myLocationListener = new MyLocationListener(context);
		
		
		// ---------------- Inizializzo l'hashtable che conterrà i valori istantanei ----------
		// questa Hashtable conterrà i valori istantanei di rilevati ogni
		// "intervallo_campionamento" ms:
		// - sensori
		// - posizione
		// - velocità
		// - stato monitor
		_main_table = new Hashtable<Integer, Valore>();
		
		
		
		//------------------------- Inizializzo tabella valori istantanei --------------------------------
		// ****** Valori da Location Listener
		//Recupero il location provider
		
		
		// ****** Valori STATO Display
		_valore_screen_status = new ValoreDecimale();
		_main_table.put(AppDictionary.KEY_DISPLAY_STATUS,_valore_screen_status);

		
		// ****** Valori Sensori
		
		
		// inizializzo gli intervalli, delay, la lunghezza della finestra dei
		// campioni
		_interval_ms = Setting.SAMPLE_INTERVAL_MS;
		_delay = 0;
		_samplesWindow = new SamplesWindow(_context);
		_sampleWindowLenght = Setting.SAMPLES_WINDOW_LENGHT; 

	}

	public void startMonitoring() {				
				
		//+***********************************************+
		//|              LOCATION STATUS                   |
		//+***********************************************+
		
		//Registro il myLocationListener
		_myLocationListener.registerLocationListener();
		
		//Registro il mySensorListener
		_mySensorListener.registerMySensorListener();
		
		/*
		 * task che aggiorna la finestra dei campioni "sampleWindow" con i dati
		 * relativi a: 
		 * - sensori 
		 * - posizione 
		 * - stato di utilizzo del telefono 
		 * e infine ricava il contesto (stato moto e utilizzo telefono)
		 */
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				long current_timestamp = System.currentTimeMillis();

				// aggiorno tutti i valori di sensori e di stato e gli aggiungo
				// alla samples_windows

				//DEVICE USE
				Hashtable<Integer, Valore> table_device_use = AppUtils.cloneHashTable(_deviDeviceUseMonitor.get_device_use_table());
				
				//SENSORI
				Hashtable<Integer, Valore> table_sensori =  AppUtils.cloneHashTable(_mySensorListener.get_table_valori_sensori());
									
				//LOCATION
				Hashtable<Integer, Valore> table_location =  AppUtils.cloneHashTable(_myLocationListener.get_location_table());
				
				_samplesWindow.addSamplesTables(table_sensori, table_device_use, table_location,_myLocationListener.get_gps_status());
												
//				_samplesWindow.printStatus();
			}
		};
		_timer = new Timer();

		if (_interval_ms <= 0)
			_interval_ms = 1;
		if (_delay < 0)
			_delay = 0;

		_timer.scheduleAtFixedRate(task, _delay, _interval_ms);

	}

	public Hashtable<Integer, Integer> getContext() {
		Hashtable<Integer, Integer> contextTable = new Hashtable<Integer, Integer>();
		// TODO - calcolo dello stato

		return contextTable;
	}
	
	
	


	/*//TODO - Cancellami: metodo prova
	public StatoContesto getSampleStatoContesto(){
		StatoContesto statoContesto = new StatoContesto();
		long timestamp = System.currentTimeMillis();
		
		float latitudine = 10f;
		float longitudine = 11f;
		float altitudine = 12f;
		float accuratezza = 13f;
		
		String tipoDiLuogo = "Outdoor"; // indoor/outdoor
		
		
		int stato_moto = AppDictionary.MOTION_WALK;
		float velocita = 2f;
		
		int stato_device = AppDictionary.DEVICE_IN_POCKET;
		int tipo_utilizzo_device = 0;
		
				
		statoContesto.setTimestamp(timestamp);
		
		statoContesto.setLatitudine(latitudine);
		statoContesto.setLongitudine(longitudine);
		statoContesto.setAltitudine(altitudine);
		statoContesto.setAccuratezza(accuratezza);
		
		statoContesto.setTipoDiLuogo(tipoDiLuogo);
				
		statoContesto.setStato_moto(AppDictionary.getStatoMotoForOntology(stato_moto));
		statoContesto.setMezzo_utente(AppDictionary.getMezzoUtenteForOntology(stato_moto));
		statoContesto.setVelocita(velocita);
		
		statoContesto.setStato_device(stato_device);
		statoContesto.setTipo_utilizzo_device(tipo_utilizzo_device);
		
				
		return statoContesto;
	}*/

	public void stopMonitoring() {
		// TODO Auto-generated method stub
		
		_myLocationListener.unregisterLocationListener();
		_mySensorListener.unregisterMySensorListener();
		_deviDeviceUseMonitor.unregisterAll();
		
		_timer.cancel();
		_timer = null;
	}
	
	
	//************ STATUS GET***************
	//\\\\\\\\\\\\  MOTION STATUS ////////////
	public int getMotionState(){
		int id_motion_state = AppDictionary.MOTION_STILL; //Stato fermo
		return id_motion_state;
	}
	
	
	//\\\\\\\\\\\\  LOCATION STATUS ////////////
	
	public float getGpsStatus(){
		return _myLocationListener.get_gps_status();
	}
	
	public float[] getPositionCoord(){
		ValoreVettore valoreVettore =  (ValoreVettore) _main_table.get(AppDictionary.KEY_LOCATION_COORD);
		float[] coord = {-1f, -1f , -1f};
		if(valoreVettore!=null){
			coord = valoreVettore.getValore();
		}
		return coord;
	}
	
	public float getPositionAccuracy(){
		ValoreDecimale valoreDecimale = (ValoreDecimale)_main_table.get(AppDictionary.KEY_LOCATION_ACCURACY);
		float accuracy = -1f; 
		if(valoreDecimale!=null){
			accuracy = (float)valoreDecimale.getValore();
		}
		return accuracy;
	}
	

	
	public String getLocationProvider(){
		return _myLocationListener.get_locationProvider();
	}

	
	//Calcola lo stato del contesto
	public StatoContesto calcolaStatoContesto() {
		if(_samplesWindow != null){
			synchronized(_samplesWindow){
				return _samplesWindow.calcolaStatoContestoPercepetron();
			}
		}
		Log.e("StatusDetector", "calcolaStatoContesto: ERRORE! _samplesWindow = null");
		return null;
	}
	
	//Calcola lo stato del contesto
	public boolean sendNewRequest(boolean msg_server_attivi) {
		boolean has_nuoviElementi = false;
		if(_samplesWindow != null){
			
			_samplesWindow.printStatus(true);
			if(msg_server_attivi){
				has_nuoviElementi =  _samplesWindow.sendContextStatus();
				Log.i("StatusDetector", " -----> sendNewRequest: nuovi elementi?"+has_nuoviElementi);
			}
		}
		else{
			Log.e("StatusDetector", "printStatus(): ERRORE! _samplesWindow = null");
		}
		return has_nuoviElementi;
	}

}
