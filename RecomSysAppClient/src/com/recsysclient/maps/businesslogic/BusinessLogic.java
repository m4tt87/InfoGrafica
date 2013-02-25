package com.recsysclient.maps.businesslogic;


import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.recsysclient.entity.PoI;
import com.recsysclient.entity.Position;
import com.recsysclient.maps.MapsContextMonitor;
import com.recsysclient.utility.AppDictionary;
import com.recsysclient.utility.DistanceBetweenCoords;
import com.recsysclient.utility.IntentHelper;
import com.recsysclient.utility.Setting;

public class BusinessLogic extends Service{
	public static final String BUSINESSLOGIC="BUSINESSLOGIC";
	
	private boolean hasNewPosition;
	private boolean hasNewPoI;
	
	private Set<PoI> returnedList;
	private Set<PoI> filteredList;

	//intervalli di campionamento
	private int positionUpdateInterval;
	
	//ritardo prima della lettura (in genere = 0)
	private int delay;
	Timer timer;
	
	private BroadcastReceiver broadcastReceiver;
	
	private Position lastRequiredPosition;
	private Position position;
	
	private StrategyFilterResults filter;
	
	private StrategyRetrievePoI retrievePoI;
	
	//FIXME metodo da cancellare quando il sistema sarà completamente up
	public void getPoIList(){
		//TODO utilizza il parser per andare a recuperare la lista completa dal file xml statico
		returnedList=retrievePoI.getPoISet(position.getLat(),position.getLng());
		
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		positionUpdateInterval=Setting.POSITION_SAMPLE_INTERVAL;
		delay=0;
		
		retrievePoI=new RetrieveWikipediaPoI();
		
		broadcastReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				hasNewPosition=false;
				hasNewPoI=false;
				if (intent.getAction().equals(MapsContextMonitor.CONTEXT_UPDATE)) {
					//TODO Logica per andare a recuperare la lista dei PoI da AppCommVar
				}
				else if(intent.getAction().equals(MapsContextMonitor.POSITION_UPDATE)){
					
					//FIXME Aggiornamento della lista da presentare (non ci sarà con il server su)
					Bundle extras = intent.getExtras();
					if(extras!= null){
						position=(Position)extras.get(AppDictionary.POSITION);
						
        				/*position.setLat(Float.parseFloat(extras.getString(AppDictionary.LAT)));
        				position.setLng(Float.parseFloat(extras.getString(AppDictionary.LNG)));
        				position.setBearing(Float.parseFloat(extras.getString(AppDictionary.BEARING)));
        				position.setTilt(Integer.parseInt(extras.getString(AppDictionary.TILT)));
        				position.setZoom(Float.parseFloat(extras.getString(AppDictionary.ZOOM)));
        				position.setMotionState(extras.getString(AppDictionary.MOTION_STATE));*/
        				
						//FIXME da commentare quando il server sarà su
						//controllo per verificare che non ci si è allontanati più di x-Km dal luogo in cui si è fatta richiesta dei poi l'ultima volta
						//se non si è mai scaricata la lista dei poi e se mi sono allontanato troppo la scarico
						if(lastRequiredPosition==null){
							if(position!=null){
							getPoIList();
							lastRequiredPosition=position;
							}
						}
						else if(DistanceBetweenCoords.CalculateDistance(lastRequiredPosition.getLat(), lastRequiredPosition.getLng(), position.getLat(), position.getLng())>=Setting.MAXKM){
							getPoIList();
							lastRequiredPosition=position;
						}
        				
						//applica il filtro appropriato alla lista completa per verificare la presenza di nuovi PoI nel raggio interessante per l'applicazione
        				if(!returnedList.isEmpty()){
	        				if(position.getMotionState()==AppDictionary.STR_OUTPUT_MOTION_CAR)
	        					filter=new FilterInAuto();
	        			
	        				else
	        					filter=new FilterByFoot();

	        				filteredList=filter.getFilteredList(returnedList,position.getLat(),position.getLng());
        				}
        				IntentHelper.addObjectForKey(AppDictionary.POSITION, position);
        				hasNewPosition=true;
        				
					}
				}
			}
			
		};
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		TimerTask task=new TimerTask(){
		
			Intent b_intent = new Intent(BUSINESSLOGIC);

			@Override
			public void run() {
				if(hasNewPosition){
					b_intent.putExtra(AppDictionary.POSITION, hasNewPosition);
					sendBroadcast(b_intent);
					hasNewPosition=false;
				}
				if(hasNewPoI){
					b_intent.putExtra(AppDictionary.POI, hasNewPoI);
					sendBroadcast(b_intent);
					hasNewPoI=false;
				}
				
			}
		};
		timer = new Timer();
		
		if(positionUpdateInterval<=0) 
			positionUpdateInterval = Setting.POSITION_SAMPLE_INTERVAL;
		if(delay<0)
			delay=0;
		
		timer.scheduleAtFixedRate(task, delay, positionUpdateInterval);
		
		return super.onStartCommand(intent, flags, startId);
	}
		
}
