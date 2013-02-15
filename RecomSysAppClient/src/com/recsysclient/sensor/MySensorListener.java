package com.recsysclient.sensor;

import java.util.Hashtable;

import com.recsysclient.entity.Valore;
import com.recsysclient.entity.ValoreDecimale;
import com.recsysclient.entity.ValoreVettore;
import com.recsysclient.utility.AppDictionary;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MySensorListener implements SensorEventListener {

	private SensorManager _sensorManager;
	private Context _context;
	private Hashtable<Integer, Valore> _table_valori_sensori;
	
	private boolean _isMySensorListener_registered = false;

	//Valori di tipo vettore destinati a contenere le letture ddei sensori
	private ValoreVettore _valore_accelerazione_lineare;
	private ValoreVettore _valore_orientamento;
	private ValoreVettore _valore_luminosita;
	private ValoreVettore _valore_prossimita;
	private ValoreVettore _valore_giroscopio;
	
	private ValoreDecimale _somma_acc_lin;
	private ValoreDecimale _somma_giroscopio;
	private ValoreDecimale _media_somme_acc_lin;
	private ValoreDecimale _media_somme_giroscopio;
	
	private ValoreDecimale _modulo_acc_lin;
	private ValoreDecimale _modulo_acc_lin_avg;
	private ValoreDecimale _modulo_acc_lin_dev_std;
	
	public MySensorListener(Context context){
		_context = context;
		_sensorManager = (SensorManager)_context.getSystemService(Context.SENSOR_SERVICE);
		
		_isMySensorListener_registered = false;
		registerMySensorListener();
	}
	
	public void registerMySensorListener(){
		if(!_isMySensorListener_registered){
			//registro i sensori al listener e inizializzo la table
			//---------------- Inizializzo l'hashtable che conterrà i valori istantanei ----------
			//Istanzio l'hashTable che conterrà i valori istantanei
			//questa Hashtable contiene i valori istantanei di rilevati ogni "intervallo_campionamento" ms:
			//	- sensori
			//	- posizione
			//	- velocità
			//	- stato monitor
			_table_valori_sensori =  new Hashtable<Integer, Valore>();
			
			//Istanzio i Valori
			
			Sensor sensor = _sensorManager.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION).get(0);
			if(sensor != null){
				//registro il listener relativo sensore
				_sensorManager.registerListener(this, sensor,SensorManager.SENSOR_DELAY_NORMAL);
				_valore_accelerazione_lineare = new ValoreVettore();
				_valore_accelerazione_lineare.setValore(getInitialArray());
				_table_valori_sensori.put(AppDictionary.KEY_LINEAR_ACCELEROMETER_SENSOR, _valore_accelerazione_lineare);
			}
			
			
			sensor = _sensorManager.getSensorList(Sensor.TYPE_ORIENTATION).get(0);
			if(sensor != null){
				//registro il listener relativo sensore
				_sensorManager.registerListener(this, sensor,SensorManager.SENSOR_DELAY_NORMAL);
				_valore_orientamento = new ValoreVettore();
				_valore_orientamento.setValore(getInitialArray());
				_table_valori_sensori.put(AppDictionary.KEY_ORIENTATION_SENSOR, _valore_orientamento);
			}
			
			
			sensor = _sensorManager.getSensorList(Sensor.TYPE_LIGHT).get(0);
			if(sensor != null){
				//registro il listener relativo sensore
				_sensorManager.registerListener(this, sensor,SensorManager.SENSOR_DELAY_NORMAL);
				_valore_luminosita = new ValoreVettore();
				_valore_luminosita.setValore(getInitialArray());
				_table_valori_sensori.put(AppDictionary.KEY_LIGHT_SENSOR, _valore_luminosita);
			}
				
			sensor = _sensorManager.getSensorList(Sensor.TYPE_PROXIMITY).get(0);
			if(sensor != null){
				//registro il listener relativo sensore
				_sensorManager.registerListener(this, sensor,SensorManager.SENSOR_DELAY_NORMAL);
				_valore_prossimita = new ValoreVettore();
				_valore_prossimita.setValore(getInitialArray());
				_table_valori_sensori.put(AppDictionary.KEY_PROXYMITY_SENSOR, _valore_prossimita);
			}
			
			sensor = _sensorManager.getSensorList(Sensor.TYPE_GYROSCOPE).get(0);
			if(sensor != null){
				//registro il listener relativo sensore
				_sensorManager.registerListener(this, sensor,SensorManager.SENSOR_DELAY_NORMAL);
				_valore_giroscopio = new ValoreVettore();
				_valore_giroscopio.setValore(getInitialArray());
				_table_valori_sensori.put(AppDictionary.KEY_GYROSCOPE_SENSOR, _valore_giroscopio);
			}
			
			_somma_acc_lin = new ValoreDecimale();
			_somma_acc_lin.setValore(0);
			_table_valori_sensori.put(AppDictionary.KEY_LIN_ACC_SUM, _somma_acc_lin);

			_somma_giroscopio = new ValoreDecimale();
			_somma_giroscopio.setValore(0);
			_table_valori_sensori.put(AppDictionary.KEY_GYROSCOPE_SUM, _somma_giroscopio);
			
			_modulo_acc_lin = new ValoreDecimale();
			_modulo_acc_lin.setValore(0);
			_table_valori_sensori.put(AppDictionary.KEY_LIN_ACC_MODULE, _modulo_acc_lin);
			
			_isMySensorListener_registered = true;
		}
	}
	
	private float[] getInitialArray() {
		float[] array = new float[3];
		array[0] = 0;
		array[1] = 0;
		array[2] = 0;
		return array;
	}


	public Hashtable<Integer, Valore> get_table_valori_sensori() {
		return _table_valori_sensori;
	}

	public void set_table_valori_sensori(Hashtable<Integer, Valore> _table_valori_sensori) {
		this._table_valori_sensori = _table_valori_sensori;
	}
	
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		long event_timestamp = System.currentTimeMillis();
		//restituisce il sensore che ha scatenato l'evento
		Sensor dev_sensor = event.sensor;
		
		//recupero il vettore dei valori del sensore
		float[] values = event.values;
		ValoreVettore v;
		ValoreDecimale vd;
		
		switch (dev_sensor.getType()) {
		case Sensor.TYPE_LINEAR_ACCELERATION:
			v = (ValoreVettore)_table_valori_sensori.get(AppDictionary.KEY_LINEAR_ACCELEROMETER_SENSOR);
			v.setTimestamp(event_timestamp);
			v.setValore(values.clone());
			
			float abs_sum_acc = Math.abs(values[0]) + Math.abs(values[1]) + Math.abs(values[2]);
			vd = (ValoreDecimale)_table_valori_sensori.get(AppDictionary.KEY_LIN_ACC_SUM);
			vd.setValore(abs_sum_acc);
			
			//aggiungo modulo dell'accelerazione lineare
			float modulo_acc_lin = (float) Math.sqrt(values[0]*values[0] + values[1]*values[1] + values[2]*values[2]);
			vd = (ValoreDecimale)_table_valori_sensori.get(AppDictionary.KEY_LIN_ACC_MODULE);
			vd.setValore(modulo_acc_lin);
			
			break;
		case Sensor.TYPE_ORIENTATION:
			v = (ValoreVettore)_table_valori_sensori.get(AppDictionary.KEY_ORIENTATION_SENSOR);
			v.setTimestamp(event_timestamp);
			v.setValore(values.clone());
			break;
		case Sensor.TYPE_LIGHT:
			v = (ValoreVettore)_table_valori_sensori.get(AppDictionary.KEY_LIGHT_SENSOR);
			v.setTimestamp(event_timestamp);
			v.setValore(values.clone());
			break;
		case Sensor.TYPE_PROXIMITY:
			v = (ValoreVettore)_table_valori_sensori.get(AppDictionary.KEY_PROXYMITY_SENSOR);
			v.setTimestamp(event_timestamp);
			v.setValore(values.clone());
			break;
		case Sensor.TYPE_GYROSCOPE:
			v = (ValoreVettore)_table_valori_sensori.get(AppDictionary.KEY_GYROSCOPE_SENSOR);
			v.setTimestamp(event_timestamp);
			v.setValore(values.clone());
			
			float abs_sum_gyr = Math.abs(values[0]) + Math.abs(values[1]) + Math.abs(values[2]);
			vd = (ValoreDecimale)_table_valori_sensori.get(AppDictionary.KEY_GYROSCOPE_SUM);
			vd.setValore(abs_sum_gyr);
			break;
			
		default:
			break;
		}
		
	}
	
	public void unregisterMySensorListener(){
		_sensorManager.unregisterListener(this);
		_isMySensorListener_registered = false;
	}
}
