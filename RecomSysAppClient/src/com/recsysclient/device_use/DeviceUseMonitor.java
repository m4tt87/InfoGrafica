package com.recsysclient.device_use;

import java.util.Hashtable;

import com.recsysclient.entity.Valore;
import com.recsysclient.entity.ValoreDecimale;
import com.recsysclient.utility.AppDictionary;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class DeviceUseMonitor {

	private ScreenReceiver _screenReceiver;
	private Context _context;
	private boolean isAllRegistered = false;
	
	//Destinata a contenere tutte le informazioni sullo stato di utilizzo del device ovvero:
	// - stato del display: acceso/spento
	// - ...
	Hashtable<Integer, Valore> _device_use_table;
	
	public DeviceUseMonitor(Context context){
		_context = context;
		_screenReceiver = new ScreenReceiver();
		
		if(!isAllRegistered){
			//registro:
			// - lo  sceenReceiver
			// - ...
			registerAll();
		}
		
		inizializeDeviceUseTable();
	}
	
	public byte getDisplayStatus(){
		if(!isAllRegistered){
			//registro:
			// - lo  screenReceiver
			// - ...
			registerAll();
		}
		return _screenReceiver.screenEnabled();
	}

	
	private void inizializeDeviceUseTable(){
		_device_use_table = new Hashtable<Integer, Valore>();
		
		ValoreDecimale valDec = new ValoreDecimale();
		_device_use_table.put(AppDictionary.KEY_DISPLAY_STATUS,valDec);


	}
	
	
	public Hashtable<Integer, Valore> get_device_use_table() {
		ValoreDecimale valDec = (ValoreDecimale) _device_use_table.get(AppDictionary.KEY_DISPLAY_STATUS);
		valDec.setValore(getDisplayStatus());
		return _device_use_table;
	}

	public void registerAll() {
		//+***********************************************+
		//|               SCREEN STATUS                   |
		//+***********************************************+
		
		// initialize receiver: aggiungo gli intentfilter che permettono di filtrare i seguenti intent:
		// -Intent.ACTION_SCREEN_ON
		// -Intent.ACTION_SCREEN_OFF
		// Tali intent sono inviati in broadcast dal sitema quando si verifica l'accenzione o lo spegnimento del display.
		// ScreenReceive è un broadcast receiver personalizzato che permette di catturare tali intent e aggiornare lo stato interno che
		// può essere "interrogato" in qualsiasi momento.
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		_context.registerReceiver(_screenReceiver, filter);
		
		
		
		isAllRegistered = true;
	}
	
	public void unregisterAll() {
		_context.unregisterReceiver(_screenReceiver);
		
		
		isAllRegistered = false;
	}
}
