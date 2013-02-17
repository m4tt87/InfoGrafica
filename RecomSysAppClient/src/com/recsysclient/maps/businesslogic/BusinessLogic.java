package com.recsysclient.maps.businesslogic;

import java.util.List;
import java.util.Timer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.recsysclient.entity.PoI;
import com.recsysclient.entity.Position;
import com.recsysclient.maps.MapsContextMonitor;
import com.recsysclient.service.ContextMonitorService;
import com.recsysclient.utility.AppDictionary;

public class BusinessLogic extends Service{
	
	public static final String POSITION_UPDATE = "com.recsysclient.BusinessLogic.NEW_POSITION";
	public static final String MARKERS_UPDATE = "com.recsysclient.BusinessLogic.MARKERS_UPDATE";

	private boolean hasNewPosition;
	
	private List<PoI> returnedList;
	private List<PoI> filteredList;
	
	//gestisce il task (task scheduler)
	private Timer timer;

	//intervallo di campionamento
	private int intervalUpdate;
	
	//ritardo prima della lettura (in genere = 0)
	private int delay;
	
	private BroadcastReceiver broadcastReceiver;
	
	private Position position;
	
	private StrategyFilterResults filter;
	
	//FIXME metodo da cancellare quando il sistema sarà completamente up
	public void getPoIList(){
		//TODO utilizza il parser per andare a recuperare la lista completa dal file xml statico
		//
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		intervalUpdate=100;
		delay=0;
		position=new Position();
		
		broadcastReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				hasNewPosition=false;
				if (intent.getAction().equals(MapsContextMonitor.CONTEXT_UPDATE)) {
					//TODO Logica per andare a recuperare la lista dei PoI da AppCommVar
				}
				else if(intent.getAction().equals(MapsContextMonitor.POSITION_UPDATE)){
					//TODO Logica per ricevere la posizione corrente
					//FIXME Aggiornamento della lista da presentare (non ci sarà con il server su)
					Bundle extras = intent.getExtras();
					if(extras!= null){
						
        				position.setLat(Float.parseFloat(extras.getString(AppDictionary.LAT)));
        				position.setLng(Float.parseFloat(extras.getString(AppDictionary.LNG)));
        				position.setBearing(Float.parseFloat(extras.getString(AppDictionary.BEARING)));
        				position.setTilt(Integer.parseInt(extras.getString(AppDictionary.TILT)));
        				position.setZoom(Float.parseFloat(extras.getString(AppDictionary.ZOOM)));
        				position.setMotionState(extras.getString(AppDictionary.MOTION_STATE));
        				
        				//FIXME da commentare quando il server sarà su
        				if(!returnedList.isEmpty()){
	        				if(position.getMotionState()==AppDictionary.STR_OUTPUT_MOTION_CAR)
	        					filter=new FilterInAuto();
	        			
	        				else
	        					filter=new FilterByFoot();

	        				filteredList=filter.getFilteredList(returnedList);
        				}
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
		
		
		return startId;
	}
	
	
	
	
}
