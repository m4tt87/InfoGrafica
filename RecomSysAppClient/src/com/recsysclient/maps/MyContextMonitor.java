package com.recsysclient.maps;

import java.util.Timer;
import java.util.TimerTask;

import com.recsysclient.RecommendationListActivity;
import com.recsysclient.service.ContextMonitorService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyContextMonitor extends Service {
	
	//classe che monitora i sensori, lo stato del telefono e la posizione 
	private MyStatusDetector statusDetector;
		

	//gestisce il task (task scheduler)
	private Timer timer;
	
	//intervallo di campionamento
	private int interval_ms;

	//ritardo prima della lettura (in genere = 0)
	private int delay;


	public static final String CONTEXT_UPDATE = "com.recsysclient.maps.MyContextMonitor";
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.w("MyContext", "Create");
		interval_ms = 20;
		delay=0;
		statusDetector =  MyStatusDetector.getInstance(this);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.w("MyContext", "Start");
		
		statusDetector.startMonitoring();
		
		TimerTask task = new TimerTask() {
			
			//Broadcast Intent
			Intent b_intent = new Intent(CONTEXT_UPDATE);
			
			@Override
			public void run() {								
				Log.w("MyContext", "runnede");
				b_intent.putExtra("bearing", statusDetector.getBearing());
				// if(random()){
				// sendBroadcast(new_b_intent)
				sendBroadcast(b_intent);
			}
		};
		timer = new Timer();
		

		if(interval_ms<=0) 
			interval_ms = 20;
			delay=0;
	
		timer.scheduleAtFixedRate(task, delay, interval_ms);
		
		return super.onStartCommand(intent, flags, startId);
	}
}
