package com.recsysclient.maps.businesslogic;

import java.util.Timer;
import java.util.TimerTask;

import com.recsysclient.R;
import com.recsysclient.RecommendationListActivity;
import com.recsysclient.maps.MapsActivity;
import com.recsysclient.network.IMessageClient;
import com.recsysclient.service.ContextMonitorService;
import com.recsysclient.service.StatusDetector;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class MapsContextMonitor extends Service{
	//FIXME
	public static final String CONTEXT_UPDATE = "com.recsysclient.service.ContextMonitorService";
	
	private StatusDetector statusDetector;
	
	//gestisce il task (task scheduler)
	private Timer timer;

	//intervallo di campionamento
	private int interval_ms;

	//ritardo prima della lettura (in genere = 0)
	private int delay;
	
	private Notification notification;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		//FIXME
		interval_ms = 4000;
		
		statusDetector =  StatusDetector.getInstance(this);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		statusDetector.startMonitoring();
		
		TimerTask task = new TimerTask() {
			
			//Broadcast Intent
			Intent b_intent = new Intent(CONTEXT_UPDATE);
			
			Intent intentMapsActivity= new Intent(MapsContextMonitor.this, MapsActivity.class);
			
			//******Notification start
			NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			Notification new_notification = new Notification(R.drawable.applogo2, "Nuovi servizi/eventi", System.currentTimeMillis());
			PendingIntent pi=PendingIntent.getActivity(MapsContextMonitor.this, 0, intentMapsActivity, 0);
			//******Notification end
			
			

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			}
		};
		return 0;
	}
}
