package com.recsysclient.maps.businesslogic;


import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.recsysclient.entity.ContextInfo;
import com.recsysclient.entity.PoI;
import com.recsysclient.entity.StatoContesto;
import com.recsysclient.maps.MapsContextMonitor;
import com.recsysclient.service.StatusDetector;
import com.recsysclient.utility.AppDictionary;
import com.recsysclient.utility.DistanceBetweenCoords;
import com.recsysclient.utility.IntentHelper;
import com.recsysclient.utility.Setting;

public class BusinessLogic extends Service{
	public static final String BUSINESSLOGIC="BUSINESSLOGIC";
	
	private Set<PoI> returnedList;
	private Set<PoI> filteredList;

	//intervalli di campionamento
	private int positionUpdateInterval;
	
	//ritardo prima della lettura (in genere = 0)
	private int delay;
	Timer timer;
	
	private ContextInfo lastRequiredInfo;
	private ContextInfo info;
	
	private StrategyFilterResults filter;
	
	private StrategyRetrievePoI retrievePoI;
	
	private StatusDetector statusDetector;
	
	//FIXME metodo da cancellare quando il sistema sar� completamente up
	public void getPoIList(){
		//TODO utilizza il parser per andare a recuperare la lista completa dal file xml statico
		if(info.isLocalizationAvailable())
			//returnedList=retrievePoI.getPoISet(info.getLat(),info.getLng());
			returnedList=retrievePoI.getPoISet(40.27,18.05);
		
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("BL","Creo!");
		
		positionUpdateInterval=Setting.POSITION_SAMPLE_INTERVAL_MS;
		
		delay=0;
		info = new ContextInfo();
		retrievePoI=new RetrieveWikipediaPoI();
		
		statusDetector =  StatusDetector.getInstance(this);
		Log.d("BL","Ho Creato!");
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("BL","Started!");
		statusDetector.startMonitoring();
		
		TimerTask task=new TimerTask(){
		
			Intent intent = new Intent(BUSINESSLOGIC);
			StatoContesto statoContesto;

			@Override
			public void run() {
				Log.d("BL","Runned!");
				statoContesto = statusDetector.calcolaStatoContesto();
				
				info.setLat(statoContesto.getLatitudine());
				info.setLng(statoContesto.getLongitudine());
				info.setBearing(statoContesto.getBearing());
				info.setIdMotionState(statoContesto.getId_stato_moto());
				info.setLocationProvider(statusDetector.getLocationProvider());
				info.setGpsStatus(statusDetector.getGpsStatus());
				//Log.w("BL","accuratezza: " + statoContesto.getAccuratezza());
				if((info.getLat()==-1 && info.getLng()==-1 && statoContesto.getAltitudine()==-1))
						info.setLocalizationAvailable(false);
				else info.setLocalizationAvailable(true);
				//FIXME da commentare quando il server sar� su
				//controllo per verificare che non ci si � allontanati pi� di x-Km dal luogo in cui si � fatta richiesta dei poi l'ultima volta
				//se non si � mai scaricata la lista dei poi e se mi sono allontanato troppo la scarico
				if(lastRequiredInfo==null || (info.isLocalizationAvailable() && !lastRequiredInfo.isLocalizationAvailable())){
					if(info!=null){
						getPoIList();
						lastRequiredInfo=info;
					}
				}
				else if(DistanceBetweenCoords.CalculateDistance(lastRequiredInfo.getLat(), lastRequiredInfo.getLng(), info.getLat(), info.getLng())>=Setting.MAXKM){
					getPoIList();
					lastRequiredInfo=info;
				}
        				
				//applica il filtro appropriato alla lista completa per verificare la presenza di nuovi PoI nel raggio interessante per l'applicazione
        		if(returnedList!=null &&!returnedList.isEmpty()){
	        		if(statoContesto.getId_stato_moto()==AppDictionary.MOTION_CAR)
	        			filter=new FilterInAuto();
	        			
	        		else
	        			filter=new FilterByFoot();

	        		filteredList=filter.getFilteredList(new HashSet<PoI>(returnedList),info.getLat(),info.getLng());
	        		
	        		for(PoI p: filteredList){
	        			Log.w("BL",p.toString());
	        		}
	        		
	        		IntentHelper.addObjectForKey(AppDictionary.POI, filteredList);
	        		intent.putExtra(AppDictionary.POI, true);
	        		Log.d("BL","Sent POI!");
	        		sendBroadcast(intent);
        		}
        				
        		if(info!=null && info.isLocalizationAvailable()){
        			IntentHelper.addObjectForKey(AppDictionary.CONTEXT_INFO, info);
	        		intent.putExtra(AppDictionary.CONTEXT_INFO, true);
	        		Log.d("BL","Sent Info!");
					sendBroadcast(intent);
        		}
			}
		};
		timer = new Timer();
		
		if(positionUpdateInterval<=0) 
			positionUpdateInterval = Setting.POSITION_SAMPLE_INTERVAL_MS;
		if(delay<0)
			delay=0;
		
		timer.scheduleAtFixedRate(task, delay, positionUpdateInterval);
		
		return super.onStartCommand(intent, flags, startId);
	}
		
}
