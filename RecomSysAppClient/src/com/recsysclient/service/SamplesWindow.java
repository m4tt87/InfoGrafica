package com.recsysclient.service;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.recsysclient.entity.StatoContesto;
import com.recsysclient.entity.Valore;
import com.recsysclient.entity.ValoreDecimale;
import com.recsysclient.entity.ValoreVettore;
import com.recsysclient.messaging.MessageManager;
import com.recsysclient.network.HttpMessageClient;
import com.recsysclient.utility.AppCommonVar;
import com.recsysclient.utility.AppDictionary;
import com.recsysclient.utility.Outlier;
import com.recsysclient.utility.Setting;

public class SamplesWindow{
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
	
	double meanBearing;
	
	int freq_oscillazioni = 0;
	
	Context _context;
	
//	private float media_speed = 0;
//	private int n_speed = 0;
	
	public SamplesWindow(Context context){
		this._context = context;
		this.max_window_size = Setting.SAMPLES_WINDOW_LENGHT;
		this.size=0;
	}
	
	public synchronized void addSamplesTables(Hashtable<Integer, Valore> sensor_table, Hashtable<Integer, Valore> device_use_table, Hashtable<Integer, Valore> location_table, float GPS_status){
		
		ValoreVettore v;
		ValoreDecimale d;
		
		//---- SENSOR -------------------------------------
		if(sensor_table!=null){
			v = (ValoreVettore) sensor_table.get(AppDictionary.KEY_LINEAR_ACCELEROMETER_SENSOR);
			if(v!=null)
				list_values_lin_acc.add(v.getValore());
			else{
				float[] f={0f,0f,0f};
				list_values_lin_acc.add(f);
			}
			
			d = (ValoreDecimale) sensor_table.get(AppDictionary.KEY_LIN_ACC_SUM);
			if(d!=null)
				list_abs_sum_acc.add((float) d.getValore());
			else{
				list_abs_sum_acc.add(0f);
			}
			
			v = (ValoreVettore) sensor_table.get(AppDictionary.KEY_GYROSCOPE_SENSOR);
			if(v!=null)
				list_values_gyroscope.add(v.getValore());
			else{
				float[] f={0f,0f,0f};
				list_values_gyroscope.add(f);
			}
			
			d = (ValoreDecimale)sensor_table.get(AppDictionary.KEY_LIN_ACC_MODULE);
			if(d!=null)
				list_module_acc.add((float) d.getValore());
			else{
				list_module_acc.add(0f);
			}
			
			d = (ValoreDecimale) sensor_table.get(AppDictionary.KEY_GYROSCOPE_SUM);
			if(d!=null)
				list_abs_sum_gyr.add((float) d.getValore());
			else{
				list_abs_sum_gyr.add(0f);
			}
			
			v = (ValoreVettore) sensor_table.get(AppDictionary.KEY_ORIENTATION_SENSOR); 
			if(v!=null)
				list_values_orientation.add(v.getValore());
			else{
				float[] f={0f,0f,0f};
				list_values_orientation.add(f);
			}
			
			v = (ValoreVettore) sensor_table.get(AppDictionary.KEY_LIGHT_SENSOR); 
			if(v!=null)
				list_values_light.add(v.getValore());
			else{
				float[] f={0f,0f,0f};
				list_values_light.add(f);
			}
			
			v = (ValoreVettore) sensor_table.get(AppDictionary.KEY_PROXYMITY_SENSOR); 
			if(v!=null)
				list_values_proximity.add(v.getValore());
			else{
				float[] f={0f,0f,0f};
				list_values_proximity.add(f);
			}
		}
		
		//---- LOCATION -------------------------------------
		if(location_table!=null){	
			v = (ValoreVettore) location_table.get(AppDictionary.KEY_LOCATION_COORD);
			if(v!=null)
				list_coordinate.add(v.getValore());
			else{
				float[] f={40.27f,18.05f,10f};
				list_coordinate.add(f);
			}
			
			d = (ValoreDecimale) location_table.get(AppDictionary.KEY_LOCATION_ACCURACY); 
			if(d!=null)
				list_accuracy.add((float) d.getValore());
			
			d = (ValoreDecimale) location_table.get(AppDictionary.KEY_LOCATION_SPEED); 
			if(d!=null)
				list_speed.add((float) d.getValore());
		}
		_gps_status = GPS_status;
		
		//---- DEVICE USE -----------------------------------
		if(device_use_table!=null){
			d = (ValoreDecimale) device_use_table.get(AppDictionary.KEY_DISPLAY_STATUS);
			if(d!=null)
				list_display_status.add((float) d.getValore());
			else{
				list_display_status.add(0f);
			}
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
			Log.d("Samples","New Size: "+size);
		}
	}
	
public synchronized StatoContesto calcolaStatoContestoPercepetron(){

	
	long time_start = System.currentTimeMillis()/1000; //TIME START
	Log.i("SamplesWindow", "calcolaStatoContestoFisher: START:"+ time_start);
	
	int last_index = size;
	Log.d("samples", "clonedSize:"+size);
	last_index--;
	//printStatus(true);
	Log.d("samples", "clonedSize before if:"+size);
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

		//------------------------------------------
		
		freq_oscillazioni = 0;
		
		//lista dei moduli dell'accelerazione relativi alla finestra temporale considerata
		List<Float> lista_moduli_window = new ArrayList<Float>();
		List<Double> bearingList = new ArrayList<Double>();
		
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
			
			//estrazione lista degli orientamenti
			
			bearingList.add((double) list_values_orientation.get(i)[0]);
			
		}
		
		//CALCOLO MEDIA BEARING
		if(bearingList!=null && bearingList.size()>0){
			Outlier o = new Outlier(bearingList);
			meanBearing= o.getMean();
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
		_statoContesto.setBearing(meanBearing);
		
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
	
	/*
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
	*/
	public boolean sendContextStatus(){
		Log.i("SamplesWindow", "printStatus: Sto per inviare il messaggio al server al Server");
		
		MessageManager messageManager = new MessageManager();
		String xmlMsg = messageManager.getContextXmlMessage(_statoContesto, AppCommonVar.get_uri_utente());
		boolean has_nuoviElementi = HttpMessageClient.getInstance().sendContextXmlMessage(xmlMsg);
		Log.i("SamplesWindow", "printStatus: Messaggio inviato");
		
		return has_nuoviElementi;		
		
	}
	
}