package com.recsysclient.maps;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;

public class MyStatusDetector {
private static MyStatusDetector istanza = null;
private Context context;

private static int interval_ms;
private static int delay;

private Timer timer;

private float currentBearing;
private float newBearing;
	
	public MyStatusDetector(Context context) {
		this.context = context;
		interval_ms = 20;
		delay = 0;
	}

	// Metodo della classe impiegato per accedere al Singleton
	public static synchronized MyStatusDetector getInstance(Context context) {
		if (istanza == null)
			istanza = new MyStatusDetector(context);
		return istanza;
	}
	
	public void startMonitoring() {
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				if(currentBearing==0){
					currentBearing=updateBearing();
					newBearing=currentBearing;
				}
				else
					newBearing=updateBearing();
			}
		};
		timer = new Timer();

		if (interval_ms <= 0)
			interval_ms = 20;
		if (delay < 0)
			delay = 0;

		timer.scheduleAtFixedRate(task, delay, interval_ms);
	}
	
	public void stopMonitoring() {
		
		timer.cancel();
		timer = null;
	}
	
	public float updateBearing(){
		if(currentBearing==0)
			return 1f;
		return currentBearing+1f;
	}
	
	public float getBearing(){
		currentBearing=newBearing;
		return currentBearing;
	}
}
