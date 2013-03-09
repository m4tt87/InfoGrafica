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
	private boolean retrieved;
	
	private ContextInfo lastRequiredInfo;
	private ContextInfo info;
	
	private StrategyFilterResults filter;
	
	private StrategyRetrievePoI retrievePoI;
	
	private StatusDetector statusDetector;
	
	//FIXME metodo da cancellare quando il sistema sarà completamente up
	public void getPoIList(){
		//TODO utilizza il parser per andare a recuperare la lista completa dal file xml statico
		if(info.isLocalizationAvailable()){
			Set<PoI> retrievedList = retrievePoI.getPoISet(info.getLat(),info.getLng());
			if(retrievedList!=null)
				returnedList = new HashSet<PoI>(retrievedList);
			//returnedList=retrievePoI.getPoISet(40.27,18.05);
			if( returnedList!=null && !returnedList.isEmpty())
				retrieved=true;
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		positionUpdateInterval=Setting.POSITION_SAMPLE_INTERVAL_MS;
		retrieved=false;
		delay=0;
		info = new ContextInfo();
		retrievePoI=new RetrieveWikipediaPoI();
		statusDetector =  StatusDetector.getInstance(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		statusDetector.startMonitoring();
		
		TimerTask task=new TimerTask(){
			Intent intent = new Intent(BUSINESSLOGIC);
			StatoContesto statoContesto;

			@Override
			public void run() {
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
				//FIXME da commentare quando il server sarà su
				//controllo per verificare che non ci si è allontanati più di x-Km dal luogo in cui si è fatta richiesta dei poi l'ultima volta
				//se non si è mai scaricata la lista dei poi e se mi sono allontanato troppo la scarico
								
				if(lastRequiredInfo==null ){
					getPoIList();
					lastRequiredInfo=info;
				}
				else if( retrieved==false || DistanceBetweenCoords.CalculateDistance(lastRequiredInfo.getLat(), lastRequiredInfo.getLng(), info.getLat(), info.getLng())>=Setting.MAXKM){
					getPoIList();
					lastRequiredInfo=info;
				}
        				
				//applica il filtro appropriato alla lista completa per verificare la presenza di nuovi PoI nel raggio interessante per l'applicazione
        		if(returnedList!=null &&!returnedList.isEmpty()){
	        		if(statoContesto.getId_stato_moto()==AppDictionary.MOTION_CAR)
	        			filter=new FilterInAuto();
	        			
	        		else
	        			filter=new FilterByFoot();

	        		filteredList=filter.getFilteredList(returnedList,info.getLat(),info.getLng());
	        		
	        		for(PoI p: filteredList){
	        			Log.w("BL",p.toString());
	        		}
	        		
	        		IntentHelper.addObjectForKey(AppDictionary.POI, filteredList);
	        		intent.putExtra(AppDictionary.POI, true);
	        		Log.w("BL","Sent POI!");
	        		sendBroadcast(intent);
        		}
        				
        		if(info!=null && info.isLocalizationAvailable()){
        			IntentHelper.addObjectForKey(AppDictionary.CONTEXT_INFO, info);
	        		intent.putExtra(AppDictionary.CONTEXT_INFO, true);
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
