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
		_sampleWindowLenght = Setting.SAMPLES_WINDOW_LENGHT; 

	}

	public void startMonitoring() {
		
		_samplesWindow = new SamplesWindow(_context);
				
				
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
	
	
	


	//TODO - Cancellami: metodo prova
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
	}

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
	

	
	public String getLoactionProvider(){
		return _myLocationListener.get_locationProvider();
	}

	
	//Calcola lo stato del contesto
	public StatoContesto calcolaStatoContesto() {
		if(_samplesWindow != null){
			
			return _samplesWindow.calcolaStatoContestoPercepetron();
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

class SamplesWindow{
	private int max_window_size;
	private int size; //lunghezza attuale
	
	//Memorizza lo stato di moto precedente utiliper distinguere quando l'utente è fermo in auto da quando è fermo a piedi
	private int stato_moto_precedente;
	
	private float _gps_status;
	
	private StatoContesto _statoContesto = new StatoContesto();
	
	//SENSOR
	private List<float[]> list_values_lin_acc = new ArrayList<float[]>();
	private List<Float> list_abs_sum_acc = new ArrayList<Float>(); // modulo calcolato sulla base dei valori di "list_values_lin_acc"
	
	private List<Float> list_module_acc = new ArrayList<Float>(); // modulo calcolato sulla base dei valori di "list_values_lin_acc"
		
	private List<float[]> list_values_gyroscope = new ArrayList<float[]>();
	private List<Float> list_abs_sum_gyr = new ArrayList<Float>(); // modulo calcolato sulla base dei valori di "list_values_gyroscope"
	
	private List<float[]> list_values_orientation = new ArrayList<float[]>();
	
	private List<float[]> list_values_light = new ArrayList<float[]>();
	
	private List<float[]> list_values_proximity = new ArrayList<float[]>();
	
	private double media_somme_acc = 0;
	private double media_somme_gyr = 0;
	
	//LOCATION
	private List<float[]> list_coordinate = new ArrayList<float[]>();
	
	private List<Float> list_accuracy = new ArrayList<Float>();
	
	private List<Float> list_speed = new ArrayList<Float>();
	
	
	//DEVICE
	private List<Float> list_display_status = new ArrayList<Float>();
	
	
	//Variabili utili per il calcolo dello stato
	float max_mod_acc;
	float min_mod_acc;
	float avg_mod_acc;
	float dev_std_acc;

	float max_sum_gyr;
	float min_sum_gyr;
	float avg_sum_gyr;

	float min_inclinazione_pitch;
	float max_inclinazione_pitch;
	float min_inclinazione_roll;
	float max_inclinazione_roll;
	
	int freq_oscillazioni = 0;
	
	Context _context;
	
//	private float media_speed = 0;
//	private int n_speed = 0;
	
	public SamplesWindow(Context context){
		this._context = context;
		this.max_window_size = Setting.SAMPLES_WINDOW_LENGHT;
		this.size=0;
	}
	
	public void addSamplesTables(Hashtable<Integer, Valore> sensor_table, Hashtable<Integer, Valore> device_use_table, Hashtable<Integer, Valore> location_table, float GPS_status){
		
		ValoreVettore v;
		ValoreDecimale d;
		
		//---- SENSOR -------------------------------------
		if(sensor_table!=null){
			v = (ValoreVettore) sensor_table.get(AppDictionary.KEY_LINEAR_ACCELEROMETER_SENSOR);
			list_values_lin_acc.add(v.getValore());
			
			d = (ValoreDecimale) sensor_table.get(AppDictionary.KEY_LIN_ACC_SUM);
			list_abs_sum_acc.add((float) d.getValore());
			
			v = (ValoreVettore) sensor_table.get(AppDictionary.KEY_GYROSCOPE_SENSOR);
			list_values_gyroscope.add(v.getValore());
			
			d = (ValoreDecimale)sensor_table.get(AppDictionary.KEY_LIN_ACC_MODULE);
			list_module_acc.add((float) d.getValore());
			
			d = (ValoreDecimale) sensor_table.get(AppDictionary.KEY_GYROSCOPE_SUM);
			list_abs_sum_gyr.add((float) d.getValore());
			
			v = (ValoreVettore) sensor_table.get(AppDictionary.KEY_ORIENTATION_SENSOR); 
			list_values_orientation.add(v.getValore());
			
			v = (ValoreVettore) sensor_table.get(AppDictionary.KEY_LIGHT_SENSOR); 
			list_values_light.add(v.getValore());
			
			v = (ValoreVettore) sensor_table.get(AppDictionary.KEY_PROXYMITY_SENSOR); 
			list_values_proximity.add(v.getValore());
		}
		
		//---- LOCATION -------------------------------------
		if(location_table!=null){	
			v = (ValoreVettore) location_table.get(AppDictionary.KEY_LOCATION_COORD);
			list_coordinate.add(v.getValore());
			
			d = (ValoreDecimale) location_table.get(AppDictionary.KEY_LOCATION_ACCURACY); 
			list_accuracy.add((float) d.getValore());
			
			d = (ValoreDecimale) location_table.get(AppDictionary.KEY_LOCATION_SPEED); 
			list_speed.add((float) d.getValore());	
		}
		_gps_status = GPS_status;
		
		//---- DEVICE USE -----------------------------------
		if(device_use_table!=null){
			d = (ValoreDecimale) device_use_table.get(AppDictionary.KEY_DISPLAY_STATUS);
			list_display_status.add((float) d.getValore());
		}
		
		
		
		if((size+1) > max_window_size){
			
			list_values_lin_acc.remove(0);
			list_abs_sum_acc.remove(0);
			list_module_acc.remove(0);
			list_values_gyroscope.remove(0);
			list_abs_sum_gyr.remove(0);
			list_values_orientation.remove(0);
			list_values_light.remove(0);
			list_values_proximity.remove(0);
			
			list_coordinate.remove(0);
			list_accuracy.remove(0);
			list_speed.remove(0);
			
			list_display_status.remove(0);
			
		}
		else{
			size++;
		}
	}
	
	
	
public StatoContesto calcolaStatoContesto(){

		
		long time_start = System.currentTimeMillis()/1000; //TIME START
		Log.i("SamplesWindow", "calcolaStatoContesto: START:"+ time_start);
		
		//*********** Definisco i Range delle medie per il riconoscimento del movimento ********
		Range range_acc_IMMOBILE = new Range(0.00f, 0.18f);
		Range range_acc_STILL = new Range(0.18f, 1.8f);
		Range range_acc_CAR = new Range(0.3f, 1.6f);
		Range range_acc_WALK = new Range(1.8f, 5f);
		Range range_acc_WALK_POCKET = new Range(1.8f, 10f);
		Range range_acc_RUN = new Range(7.5f, 14f);
		
		Range range_gyr_IMMOBILE = new Range(0.00f, 0.03f);
		Range range_gyr_STILL = new Range(0.03f, 1.1f);
		Range range_gyr_CAR = new Range(0.01f, 0.5f);
		Range range_gyr_WALK = new Range(0.5f, 2.3f);
		Range range_gyr_WALK_POCKET = new Range(1.1f, 5.9f);
		Range range_gyr_RUN = new Range(2.35f, 7.7f);
		
		Range range_pich_in_su = new Range(-5f, +5f);
		Range range_pich_in_giu_1 = new Range(175f, 180f);
		Range range_pich_in_giu_2 = new Range(-180f, -175f);
		Range range_roll = new Range(-5f, +5f);
		
		int last_index = size-1;
		printStatus(true);
		if(size>0){
			long timestamp = System.currentTimeMillis();
			
			float latitudine =  list_coordinate.get(last_index)[0];
			float longitudine = list_coordinate.get(last_index)[1];
			float altitudine =  list_coordinate.get(last_index)[2];
			float accuratezza = list_accuracy.get(last_index);
			
			String tipoDiLuogo = "Outdoor"; // indoor/outdoor
			String citta = "Lecce";
			String area = "";
			String via_indirizzo = "Via Trinchese";
			String numero_civico = "";
			String nome_edificio = "";
			String piano = "";
			String appartamento = "";
			String stanza = "";
			
			int stato_moto = AppDictionary.MOTION_NOT_AVAILABLE;
			float velocita = list_speed.get(last_index);
			
			int stato_device = AppDictionary.DEVICE_USE_NOT_AVAILABLE;
			int tipo_utilizzo_device = 0;
			
			float livello_luminosita = list_values_light.get(last_index)[0];
			float temperatura = 13f;
			int tempo = 1;
			
			
			
			max_mod_acc = list_abs_sum_acc.get(0);
			min_mod_acc = list_abs_sum_acc.get(0);
			avg_mod_acc = list_abs_sum_acc.get(0);
	
			max_sum_gyr = list_abs_sum_gyr.get(0);
			min_sum_gyr = list_abs_sum_gyr.get(0);
			avg_sum_gyr = list_abs_sum_gyr.get(0);
			
			min_inclinazione_pitch = list_values_orientation.get(0)[1];
			max_inclinazione_pitch = list_values_orientation.get(0)[1];
			min_inclinazione_roll = list_values_orientation.get(0)[2];
			max_inclinazione_roll = list_values_orientation.get(0)[2];
			//------------------------------------------
			
			freq_oscillazioni = 0;
			
			for(int i=1; i < size; i++){
								
				float valore_somma_acc = list_abs_sum_acc.get(i);
				if(valore_somma_acc>max_mod_acc){
					max_mod_acc = valore_somma_acc;
				}
				else if(valore_somma_acc < min_mod_acc){
					min_mod_acc = valore_somma_acc;
				}
				avg_mod_acc+=valore_somma_acc;
				
				float somma_componenti_acc = list_values_lin_acc.get(i)[0]+list_values_lin_acc.get(i)[1]+list_values_lin_acc.get(i)[2];
				if(Math.abs(somma_componenti_acc)>1) freq_oscillazioni++;
				
				float valore_somma_gyr = list_abs_sum_gyr.get(i);
				if(valore_somma_gyr>max_sum_gyr){
					max_sum_gyr = valore_somma_gyr;
				}
				else if(valore_somma_gyr<min_sum_gyr){
					min_sum_gyr = valore_somma_gyr;
				}
				avg_sum_gyr+=valore_somma_gyr;
				
				float valore_pitch = list_values_orientation.get(i)[1];
				if(valore_pitch>max_inclinazione_pitch){
					max_inclinazione_pitch = valore_pitch;
				}
				else if(valore_pitch<min_inclinazione_pitch){
					min_inclinazione_pitch = valore_pitch;
				}
				
				float valore_roll = list_values_orientation.get(i)[2];
				if(valore_roll>max_inclinazione_roll){
					max_inclinazione_roll = valore_roll;
				}
				else if(valore_roll<min_inclinazione_roll){
					min_inclinazione_roll = valore_roll;
				}
			}
			
			//CALCOLO DELLE MEDIE
			avg_mod_acc/=size;
			avg_sum_gyr/=size;
			
			
			
			Log.i("SamplesWindow", " > MEDIA_ACCEL_LIN 	= " + avg_mod_acc);
			Log.i("SamplesWindow", " > MEDIA_GYROSCOPIO = " + avg_sum_gyr);
			
			
			//////************* CALCOLO DELLO STATO *****************\\\\\\
			
			//OUTDOOR
			float prossimita = list_values_proximity.get(last_index)[0];
			float livello_luce = list_values_light.get(last_index)[0];
			boolean display_acceso = list_display_status.get(last_index)>0;
			
			if(prossimita <5f){ //telefono "in tasca" o "vicino all'orecchio" o "poggiato all'ingiù"
	//			if(isCallActive()){
	//				stato_device = AppDictionary.DEVICE_PLACED;
	//			}
				if(livello_luce <= 10 && !display_acceso) { 
					//telefono "in tasca" o "vicino all'orecchio" o "poggiato all'ingiù"
					stato_device = AppDictionary.DEVICE_IN_POCKET;
				}
				else{
					//potrebbe essere in tasca con il display acceso oppure vicino all'orecchio
					stato_device = AppDictionary.DEVICE_USE_NOT_AVAILABLE;
				}
			}
			else{ //telefono "in uso" o "poggiato" 
				if(display_acceso){
					//"in uso"
					stato_device = AppDictionary.DEVICE_IN_USE;
				}
				else{
					//"non in uso"
					stato_device = AppDictionary.DEVICE_NOT_IN_USE;
					if(range_acc_STILL.isInTheRange(avg_mod_acc) && range_gyr_STILL.isInTheRange(avg_sum_gyr)){
						stato_moto = AppDictionary.MOTION_STILL;
					}
				}
			}
			
			
			
			//Auto (smartphone in tasca)
			//Auto (smartphone cruscotto)
			
			//---------- Basato su coordinate e velocità GPS ---------------
			// 1m/s = 3,6Km/h
			if(_gps_status  ==  AppDictionary.GPS_ON && accuratezza<=200 && velocita>= 0f){
				if(velocita >= 5f){
					//In Auto
					stato_moto = AppDictionary.MOTION_CAR;
				}
				else if(isInTheRange(velocita, 0f, 0.1f)){
					//Fermo 
					stato_moto = AppDictionary.MOTION_STILL;
				}
				else if(isInTheRange(velocita, 0.2f, 1.6f)){
					//Camminata
					stato_moto = AppDictionary.MOTION_WALK;
	//				if(stato_device == AppDictionary.DEVICE_IN_POCKET && isInTheRange(avg_sum_acc,5,7) && isInTheRange(avg_sum_gyr, 2.5f, 5)){
	//					stato_moto = AppDictionary.MOTION_WALK;
	//				}
	//				//Camminare (smartphone in mano)
	//				else if(stato_device == AppDictionary.DEVICE_IN_USE && isInTheRange(avg_sum_acc,3.5f,4) && isInTheRange(avg_sum_gyr, 0.7f, 1)){
	//					stato_moto = AppDictionary.MOTION_WALK;
	//				}
				}
				else if(isInTheRange(velocita, 1.6f, 3f) ){
					if(range_acc_RUN.isInTheRange(avg_mod_acc) && range_gyr_RUN.isInTheRange(avg_sum_gyr)){
						stato_moto = AppDictionary.MOTION_RUN;
					}
					else if(range_acc_CAR.isInTheRange(avg_mod_acc) && range_gyr_CAR.isInTheRange(avg_sum_gyr)){
						stato_moto = AppDictionary.MOTION_CAR;
					}
				}
				
			}
			//---------- Basato solo sui motion detection e non su informazioni GPS ---------------
			else{
				
				//Camminare (smartphone in tasca)
				if(stato_device == AppDictionary.DEVICE_IN_POCKET && range_acc_WALK_POCKET.isInTheRange(avg_mod_acc) && range_gyr_WALK_POCKET.isInTheRange(avg_sum_gyr)){
					stato_moto = AppDictionary.MOTION_WALK;
				}
				//Camminare (smartphone in mano e in uso)
				else if(stato_device == AppDictionary.DEVICE_IN_USE && range_acc_WALK.isInTheRange(avg_mod_acc) && range_gyr_WALK.isInTheRange(avg_sum_gyr)){
					if(freq_oscillazioni >= (3*(max_window_size/5))) //>=15
						stato_moto = AppDictionary.MOTION_WALK;
					else
						stato_moto = AppDictionary.MOTION_STILL;
				}
				//Camminare (smartphone in mano e NON in uso)
				else if(stato_device == AppDictionary.DEVICE_NOT_IN_USE && range_acc_WALK.isInTheRange(avg_mod_acc) && range_gyr_WALK.isInTheRange(avg_sum_gyr)){
					if(freq_oscillazioni >= (3*(max_window_size/5))) //>=15
						stato_moto = AppDictionary.MOTION_WALK;
					else
						stato_moto = AppDictionary.MOTION_STILL;
				}
				//Correre con smartphone in tasca
				else if((stato_device == AppDictionary.DEVICE_IN_POCKET || prossimita <5f) && range_acc_RUN.isInTheRange(avg_mod_acc) && range_gyr_RUN.isInTheRange(avg_sum_gyr)){
					stato_moto = AppDictionary.MOTION_RUN;
				}
				//Fermo (smartphone in mano)
				else if(stato_device == AppDictionary.DEVICE_IN_USE && range_acc_STILL.isInTheRange(avg_mod_acc) && range_gyr_STILL.isInTheRange(avg_sum_gyr)){
					stato_moto = AppDictionary.MOTION_STILL;
				}
				//Fermo (smartphone in tasca)
				else if(stato_device == AppDictionary.DEVICE_IN_POCKET && range_acc_STILL.isInTheRange(avg_mod_acc) && range_gyr_STILL.isInTheRange(avg_sum_gyr)){
					stato_moto = AppDictionary.MOTION_STILL;
				}
				else if(range_acc_IMMOBILE.isInTheRange(avg_mod_acc) && range_acc_IMMOBILE.isInTheRange(avg_sum_gyr)){
					if( (range_pich_in_giu_1.isInTheRange(min_inclinazione_pitch) && range_pich_in_giu_1.isInTheRange(max_inclinazione_pitch)) 
							|| (range_pich_in_giu_2.isInTheRange(min_inclinazione_pitch) && range_pich_in_giu_2.isInTheRange(max_inclinazione_pitch)) 
							|| (range_pich_in_su.isInTheRange(min_inclinazione_pitch) && range_pich_in_su.isInTheRange(max_inclinazione_pitch))
							&& range_roll.isInTheRange(min_inclinazione_roll) && range_roll.isInTheRange(max_inclinazione_roll)){
						stato_device = AppDictionary.DEVICE_PLACED;
					}
					else{
						stato_moto = AppDictionary.MOTION_STILL;
					}
				}
			}
			
			
			
			//Fermo (in piedi)
			//Fermo (seduto)
			
			_statoContesto.setTimestamp(timestamp);
			
			_statoContesto.setLatitudine(latitudine);
			_statoContesto.setLongitudine(longitudine);
			_statoContesto.setAltitudine(altitudine);
			_statoContesto.setAccuratezza(accuratezza);
			
			_statoContesto.setTipoDiLuogo(tipoDiLuogo);
			
			
			_statoContesto.setId_stato_moto(stato_moto);
			_statoContesto.setStato_moto(AppDictionary.getStatoMotoForOntology(stato_moto));
			_statoContesto.setMezzo_utente(AppDictionary.getMezzoUtenteForOntology(stato_moto));
			_statoContesto.setVelocita(velocita);
			
			_statoContesto.setStato_device(stato_device);
			_statoContesto.setTipo_utilizzo_device(tipo_utilizzo_device);
			
					
		}
		

		long time_end = System.currentTimeMillis()/1000; //TIME START
		Log.i("SamplesWindow", "calcolaStatoContesto: END:"+ time_end);
		Log.i("SamplesWindow", "calcolaStatoContesto: STATO MOTO:" + AppDictionary.getStringStatus(_statoContesto.getId_stato_moto())
				+ ";  STATO DEVICE:" + AppDictionary.getStringStatus(_statoContesto.getStato_device()));
		
		return _statoContesto;
	}
	
	
	
	
	
	
public StatoContesto calcolaStatoContestoPercepetron(){

	
	long time_start = System.currentTimeMillis()/1000; //TIME START
	Log.i("SamplesWindow", "calcolaStatoContestoFisher: START:"+ time_start);
	
	
	int last_index = size-1;
	printStatus(true);
	if(size>0){
		long timestamp = System.currentTimeMillis();
		
		float latitudine =  list_coordinate.get(last_index)[0];
		float longitudine = list_coordinate.get(last_index)[1];
		float altitudine =  list_coordinate.get(last_index)[2];
		float accuratezza = list_accuracy.get(last_index);
		
				
		int stato_moto = AppDictionary.MOTION_NOT_AVAILABLE;
		float velocita = list_speed.get(last_index);
		
		int stato_device = AppDictionary.DEVICE_USE_NOT_AVAILABLE;
		int tipo_utilizzo_device = 0;
		
			
		
		
		max_mod_acc = list_module_acc.get(0);
		min_mod_acc = list_module_acc.get(0);
		avg_mod_acc = list_module_acc.get(0);
		dev_std_acc = list_module_acc.get(0);

				
		min_inclinazione_pitch = list_values_orientation.get(0)[1];
		max_inclinazione_pitch = list_values_orientation.get(0)[1];
		min_inclinazione_roll = list_values_orientation.get(0)[2];
		max_inclinazione_roll = list_values_orientation.get(0)[2];
		//------------------------------------------
		
		freq_oscillazioni = 0;
		
		//lista dei moduli dell'accelerazione relativi alla finestra temporale considerata
		List<Float> lista_moduli_window = new ArrayList<Float>();
		
		for(int i=1; i < size; i++){
							
			float valore_modulo_acc = list_module_acc.get(i);
			
			lista_moduli_window.add(valore_modulo_acc);
			
			if(valore_modulo_acc>max_mod_acc){
				max_mod_acc = valore_modulo_acc;
			}
			else if(valore_modulo_acc < min_mod_acc){
				min_mod_acc = valore_modulo_acc;
			}
			avg_mod_acc+=valore_modulo_acc;
			
						
			float valore_pitch = list_values_orientation.get(i)[1];
			if(valore_pitch>max_inclinazione_pitch){
				max_inclinazione_pitch = valore_pitch;
			}
			else if(valore_pitch<min_inclinazione_pitch){
				min_inclinazione_pitch = valore_pitch;
			}
			
			float valore_roll = list_values_orientation.get(i)[2];
			if(valore_roll>max_inclinazione_roll){
				max_inclinazione_roll = valore_roll;
			}
			else if(valore_roll<min_inclinazione_roll){
				min_inclinazione_roll = valore_roll;
			}
		}
		
		//CALCOLO DELLE MEDIE
		avg_mod_acc/=size;
		
		float somma_quadr = 0;
		for(float mod_acc:list_module_acc){
			float diff = (mod_acc-avg_mod_acc);
			somma_quadr += (diff*diff);
		}
		
		//deviazione standard dei moduli dell'accelerazione
		dev_std_acc = (float) Math.sqrt(somma_quadr/lista_moduli_window.size());
		
		
		Log.i("SamplesWindow", " > MEDIA_ACCEL_LIN 	= " + avg_mod_acc);
		Log.i("SamplesWindow", " > MEDIA_GYROSCOPIO = " + avg_sum_gyr);
		
		
		//////************* CALCOLO DELLO STATO *****************\\\\\\
		
		//OUTDOOR
		float prossimita = list_values_proximity.get(last_index)[0];
		float livello_luce = list_values_light.get(last_index)[0];
		boolean display_acceso = list_display_status.get(last_index)>0;
		
		if(prossimita <5f){ //telefono "in tasca" o "vicino all'orecchio" o "poggiato all'ingiù"
//			if(isCallActive()){
//				stato_device = AppDictionary.DEVICE_PLACED;
//			}
			if(livello_luce <= 10 && !display_acceso) { 
				//telefono "in tasca" o "vicino all'orecchio" o "poggiato all'ingiù"
				stato_device = AppDictionary.DEVICE_IN_POCKET;
			}
			else{
				//potrebbe essere in tasca con il display acceso oppure vicino all'orecchio
				stato_device = AppDictionary.DEVICE_USE_NOT_AVAILABLE;
			}
		}
		else{ //telefono "in uso" o "poggiato" 
			if(display_acceso){
				//"in uso"
				stato_device = AppDictionary.DEVICE_IN_USE;
			}
			else{
				//"non in uso"
				stato_device = AppDictionary.DEVICE_NOT_IN_USE;
				
			}
		}
		
		boolean utente_in_movimento_a_piedi = false;
		float a1,a2,b; // linea di separazione a1*x + a2*y + b = 0
//		a1 = -0.0079f;
//		a2 = 0.0077f;
//		b = 0.0099f;
		
		a1 = -47.0615f;
		a2 = 57.097f;
		b = 36f;
		
		float indice_valutazione_stato = a1 * avg_mod_acc + a2 * dev_std_acc + b;
		if(indice_valutazione_stato<0) { //utente in movimento a piedi
			utente_in_movimento_a_piedi = true;
			stato_moto = AppDictionary.MOTION_WALK;
		}
		else{
			utente_in_movimento_a_piedi = false;
			if(stato_moto_precedente == AppDictionary.MOTION_STILL_CAR || stato_moto_precedente == AppDictionary.MOTION_CAR){
				stato_moto = AppDictionary.MOTION_STILL_CAR;
			}
			else{
				stato_moto = AppDictionary.MOTION_STILL;
			}
		}
		
		//Auto (smartphone in tasca)
		//Auto (smartphone cruscotto)
		
		//---------- Basato su coordinate e velocità GPS ---------------
		// 1m/s = 3,6Km/h
		if(_gps_status  ==  AppDictionary.GPS_ON && accuratezza<=200 && velocita>= 0f){
			if(velocita >= 5f){
				//In Auto
				stato_moto = AppDictionary.MOTION_CAR;
			}
			else if(!utente_in_movimento_a_piedi && isInTheRange(velocita, 0.2f, 5f)){
				//Fermo 
				stato_moto = AppDictionary.MOTION_CAR;
			}
		}
		
		//setto la variabile stato_moto_precedente con l'attuale stato_moto
		stato_moto_precedente = stato_moto;		
		
		
		_statoContesto.setTimestamp(timestamp);
		
		_statoContesto.setLatitudine(latitudine);
		_statoContesto.setLongitudine(longitudine);
		_statoContesto.setAltitudine(altitudine);
		_statoContesto.setAccuratezza(accuratezza);
		
		
		
		_statoContesto.setId_stato_moto(stato_moto);
		_statoContesto.setStato_moto(AppDictionary.getStatoMotoForOntology(stato_moto));
		_statoContesto.setMezzo_utente(AppDictionary.getMezzoUtenteForOntology(stato_moto));
		_statoContesto.setVelocita(velocita);
		
		_statoContesto.setStato_device(stato_device);
		_statoContesto.setTipo_utilizzo_device(tipo_utilizzo_device);
		
	}
	

	long time_end = System.currentTimeMillis()/1000; //TIME START
	Log.i("SamplesWindow", "calcolaStatoContesto: END:"+ time_end);
	Log.i("SamplesWindow", "calcolaStatoContesto: STATO MOTO:" + AppDictionary.getStringStatus(_statoContesto.getId_stato_moto())
			+ ";  STATO DEVICE:" + AppDictionary.getStringStatus(_statoContesto.getStato_device()));
	
	return _statoContesto;
}
	
	private boolean isCallActive(){
		   AudioManager manager = (AudioManager)_context.getSystemService(Context.AUDIO_SERVICE);
		   if(manager.getMode()==AudioManager.MODE_IN_CALL){
		         return true;
		   }
		   else if(manager.getMode()==AudioManager.MODE_NORMAL){
		         return true;
		   }
		   else{
		       return false;
		   }
		}
	
	
	private boolean isInTheRange(float valore, float min, float max){
		
		if(valore >= min && valore <= max){
			Log.i("SamplesWindow", "isInTheRange: TRUE ->  valore =" + valore + ",  min=" + min + ",  max=" + max);
			return true;
		}
		Log.i("SamplesWindow", "isInTheRange: FALSE -> valore =" + valore + ",  min=" + min + ",  max=" + max);
		return false;
	}
	
	
	//print status
	public void printStatus(boolean onlyLastSampleElement) {

		Log.i("SamplesWindow", "\n-----------START WINDOW: size=" + size + "-------------\n");
		for(int i =0; i< size;i++) {
			if(onlyLastSampleElement){
				i = size-1; //in modo da puntare all'ultimo campione di elementi della SampleWindows
				Log.i("SamplesWindow", "\n ==> ULTIMO CAMPIONE\n");
			}
			String str = "Display:" + list_display_status.get(i) + "; ";

			str += "GPS Status:" + _gps_status + "; ";

			str += "GPS Speed:" + list_speed.get(i) + "; ";

			str += "GPS coord:" + list_coordinate.get(i)[0] +" - "+ list_coordinate.get(i)[1] +" - "+ list_coordinate.get(i)[2]+ "; ";
			
			str += " LIGHT_SENSOR:" + list_values_light.get(i)[0]+ "; ";
			
			NumberFormat nf = NumberFormat.getInstance(Locale.ITALIAN);
			nf.setMinimumFractionDigits(2);
			nf.setMaximumFractionDigits(2);
			
			str +=  " ACC_LIN:" + nf.format(list_values_lin_acc.get(i)[0]) +" - "+ nf.format(list_values_lin_acc.get(i)[1]) +" - "+nf.format(list_values_lin_acc.get(i)[2])+ "; "
						+ "  SUM_ABS:"+ nf.format(list_abs_sum_acc.get(i));
			
			str += " GYROSCOPE:" + nf.format(list_values_gyroscope.get(i)[0]) +" - "+nf.format(list_values_gyroscope.get(i)[1]) +" - "+nf.format(list_values_gyroscope.get(i)[2])+ "; "
				+ "  SUM_ABS:"+ nf.format(list_abs_sum_gyr.get(i));  
			
			
			str += " ORIENTATION:" + nf.format(list_values_orientation.get(i)[0]) +" - "+nf.format(list_values_orientation.get(i)[1]) +" - "+nf.format(list_values_orientation.get(i)[2])+ "; ";
			
			
			str += " PROXIMITY:" + nf.format(list_values_proximity.get(i)[0]);
			str += "\n";
			Log.i("SamplesWindow -> printStatus()", str);
		}
		
		NumberFormat nf = NumberFormat.getInstance(Locale.ITALIAN);
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		
		String msg = "";
		String str_out = "";
		
		str_out = "STATO GPS :" + AppDictionary.getStringStatus((int) _gps_status);
		msg += str_out + "\n";
		Log.i("SamplesWindow -> printStatus()", str_out);
		
		str_out = "VELOCITA' :" + nf.format(list_speed.get(size-1));
		msg += str_out + "\n";
		Log.i("SamplesWindow -> printStatus()", str_out);
		
		str_out = "MEDIA_SOMME_ACC_LIN:" + nf.format(avg_mod_acc);
		msg += str_out + "\n";
		Log.i("SamplesWindow -> printStatus()", str_out);
		
		str_out = "MEDIA_SOMME_GYROSC :" + nf.format(avg_sum_gyr);
		msg += str_out + "\n";
		Log.i("SamplesWindow -> printStatus()", str_out);
		
		str_out = "DEVICE STATE:" + AppDictionary.getStringStatus(_statoContesto.getStato_device());
		msg += str_out + "\n";
		Log.i("SamplesWindow -> printStatus()", str_out);
		
		str_out = "MOTION STATE:" + _statoContesto.getStato_moto() + " mezzo:" + _statoContesto.getMezzo_utente();
		msg += str_out + "\n";
		Log.i("SamplesWindow -> printStatus()", str_out);
		
		
	}
	
	public boolean sendContextStatus(){
		Log.i("SamplesWindow", "printStatus: Sto per inviare il messaggio al server al Server");
		
		MessageManager messageManager = new MessageManager();
		String xmlMsg = messageManager.getContextXmlMessage(_statoContesto, AppCommonVar.get_uri_utente());
		boolean has_nuoviElementi = HttpMessageClient.getInstance().sendContextXmlMessage(xmlMsg);
		Log.i("SamplesWindow", "printStatus: Messaggio inviato");
		
		return has_nuoviElementi;		
		
	}
	
}
