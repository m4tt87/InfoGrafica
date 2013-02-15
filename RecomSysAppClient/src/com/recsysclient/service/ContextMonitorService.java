package com.recsysclient.service;


import java.util.Timer;
import java.util.TimerTask;

import com.recsysclient.R;
import com.recsysclient.MainActivity;
import com.recsysclient.RecommendationListActivity;
import com.recsysclient.entity.StatoContesto;
import com.recsysclient.messaging.MessageManager;
import com.recsysclient.network.HttpMessageClient;
import com.recsysclient.network.IMessageClient;
import com.recsysclient.network.ServiceSocket;
import com.recsysclient.utility.AppCommonVar;
import com.recsysclient.utility.AppDictionary;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class ContextMonitorService extends Service {

	public static final String CONTEXT_UPDATE = "com.recsysclient.service.ContextMonitorService";
	
	//classe che monitora i sensori, lo stato del telefono e la posizione 
	private StatusDetector _statusDetector;
	

	//gestisce il task (task scheduler)
	private Timer _timer;

	//intervallo di campionamento
	private int _interval_ms;

	//ritardo prima della lettura (in genere = 0)
	private int _delay;
	
	private IMessageClient _httpClient;
	
	private Notification notification;
	
	private boolean msg_server_attivi;
	
	private boolean voce_attiva;
	
	private boolean isNfcBroadcastReceiver_registered = false;
	
	private BroadcastReceiver NfcBroadcastReceiver;
	
	/**
	 * @see android.app.Service#onBind(Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Put your code here
		return null;
	}
	
	//Richiamato una sola volta, appena il servizio viene creato
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		_interval_ms = 4000;
		
		_statusDetector =  StatusDetector.getInstance(this);
		
		//creo il socket
		 _httpClient = HttpMessageClient.getInstance();
		 
		 NfcBroadcastReceiver = new BroadcastReceiver() {
	        	
	        	@Override
	            public void onReceive(Context context, Intent intent) {
	        		
	        		if (intent.getAction().equals(RecommendationListActivity.NFC_TRIGGER)) {
	        			
	        			Bundle extras = intent.getExtras();
	        			
	        			if(extras!= null){
	        				String idTagNfc = extras.getString("idTagNfc");
	        				if(!idTagNfc.equalsIgnoreCase("")){
	        					StatoContesto statoContesto = _statusDetector.calcolaStatoContesto();
	        					statoContesto.setIdLocationNfcTag(idTagNfc);
	        					
	        					//**** INVIO RICHIESTA AL SERVER *******
	        					boolean has_nuovi_elementi = _statusDetector.sendNewRequest(true);
	        					if(has_nuovi_elementi){
	        						//intent utilizzato richiamare l'Activity che visualizza la lista di nuovi eventi/servizi
	        						Intent intent_recommendationList = new Intent(ContextMonitorService.this, RecommendationListActivity.class);
	        						
//	        						AppCommonVar.setInvia_richieste(false);
	        						
	        						intent_recommendationList.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        						intent_recommendationList.putExtra("is_explicit_request", true);
	        						statoContesto.setIdLocationNfcTag("null");
	        						startActivity(intent_recommendationList);				
	        					}
	        					else{
	        						Log.i("ContextMonitorService.onStartCommand(...).new TimerTask() {...}",
	        						"run: ------------- NON ci sono nuovi eventi e servizi");
	        					}
	        				}
	        			}
	        		} 
	        	}
	       };
	       
	      
	       
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		//Registro il broadcast receiver che intercetta l'evento di lettura del tag
		IntentFilter filter = new IntentFilter(RecommendationListActivity.NFC_TRIGGER);
	    this.registerReceiver(NfcBroadcastReceiver, filter);
	    Log.i("ContextMonitorService", "onStartCommand: NfcBroadcastReceiver registered!"); //Cancellami
	    isNfcBroadcastReceiver_registered = true;
		
		Bundle extras = intent.getExtras();
		
		msg_server_attivi = extras.getBoolean("msg_server_attivi");
		
		voce_attiva = extras.getBoolean("voce_attiva");
		
		AppCommonVar.setInvia_richieste(msg_server_attivi);
		
        _statusDetector.startMonitoring();		
			
		/*task che aggiorna la finestra dei campioni "sampleWindow" con i dati relativi a:
			- sensori
			- posizione
			- stato di utilizzo del telefono
		  e calcola lo stato di moto
		*/
		TimerTask task = new TimerTask() {
			
			//Broadcast Intent
			Intent b_intent = new Intent(CONTEXT_UPDATE);
			
			//intent utilizzato richiamare l'Activity che visualizza la lista di nuovi eventi/servizi
			Intent intent_recommendationList = new Intent(ContextMonitorService.this, RecommendationListActivity.class);
			
			//******Notification start
			NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			Notification new_notification = new Notification(R.drawable.applogo2, "Nuovi servizi/eventi", System.currentTimeMillis());
			PendingIntent pi=PendingIntent.getActivity(ContextMonitorService.this, 0, intent_recommendationList, 0);
			//******Notification end
			
			@Override
			public void run() {
				long current_timestamp = System.currentTimeMillis();
				StatoContesto _statoContesto;
				
				boolean has_nuovi_elementi = false;
				
				if(AppCommonVar.isInvia_richieste() || voce_attiva){
					//VENGONO CALCOLATI I PARAMETRI CHE IDENTIFICANO LO STATO DEL CONTESTO ATTUALE
					_statoContesto = _statusDetector.calcolaStatoContesto();
					
					//-----------------------------------------------------------------------------------
					//inserisco parametri nel bundle del broadcast Intent (destinato alla MainActivity)
					//-----------------------------------------------------------------------------------
					
					//se has_nuovi_elementi = TRUE allora vuol dire che ci sono nuovi eventi e/o servizi da proporre
					b_intent.putExtra("has_nuovi_elementi", has_nuovi_elementi);
					
					b_intent.putExtra(AppDictionary.LOCATION_PROVIDER,_statusDetector.getLoactionProvider());		//String
					b_intent.putExtra(AppDictionary.GPS_STATUS,_statusDetector.getGpsStatus());		//float
					b_intent.putExtra(AppDictionary.MOTION_STATE,_statoContesto.getId_stato_moto());		//int
					b_intent.putExtra(AppDictionary.DEVICE_STATE,_statoContesto.getStato_device()); 	//int
					float[] coords = { _statoContesto.getLatitudine(), _statoContesto.getLongitudine(), _statoContesto.getAltitudine()};
					b_intent.putExtra(AppDictionary.COORD_STATE,coords);		//float[]   
					b_intent.putExtra(AppDictionary.ACCURACY_STATE,_statoContesto.getAccuratezza());	//float
					b_intent.putExtra(AppDictionary.SPEED_STATE,_statoContesto.getVelocita());	//float
					
					
									
		            sendBroadcast(b_intent);
		            
		            if(AppCommonVar.isInvia_richieste()){
						//**** INVIO RICHIESTA "IMPLICITA" AL SERVER *******
						has_nuovi_elementi = _statusDetector.sendNewRequest(msg_server_attivi);
						if(has_nuovi_elementi){ //ci sono nuovi elementi
							//RICHIESTA IMPLICITA
							
	//						AppCommonVar.setInvia_richieste(false);
							
							
							//esplicito che non si tratta di una richiesta esplicita, in modo da filtrare i risultati 
							//da visualizzare (servizi/eventi)
							intent_recommendationList.putExtra("is_explicit_request", false);
							
	//						intent_recommendationList.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	//						startActivity(intent_recommendationList);
							
							//FIXME righe da cancellare, perchè avviano direttamente l'activity che visualizza la lista dei suggerimenti
						    Intent intent_view_list = new Intent(ContextMonitorService.this, RecommendationListActivity.class);
						    intent_view_list.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						    startActivity(intent_view_list);
						    //---------------------
						    
							//******Notification start
							intent_recommendationList.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
						    new_notification.setLatestEventInfo(ContextMonitorService.this, "Recommendation System", "Context monitoring...", pi);
						    new_notification.flags|=Notification.FLAG_AUTO_CANCEL;
						    new_notification.defaults |= Notification.DEFAULT_VIBRATE;
						    long[] vibrate = {0,100,200,300};
						    new_notification.vibrate = vibrate;
	//						startForeground(2, new_notification);
						    notificationManager.notify(2,new_notification);
							//******Notification end
						    
						    
							
						}
						else{
							Log.i("ContextMonitorService.onStartCommand(...).new TimerTask() {...}",
							"run: ------------- NON ci sono nuovi eventi e servizi");
						}
		            }
				}
				
				
	           
			}
		};
		_timer = new Timer();
		
		if(_interval_ms<=0) 
			_interval_ms = 4000;
		if(_delay<0)
			_delay=0;
		
		_timer.scheduleAtFixedRate(task, _delay, _interval_ms);
	
		//NOTIFICATION -------------------- notifica che comparirà nella topbar
		notification = new Notification(R.drawable.ic_launcher, "Servizio di raccomandazione attivo", System.currentTimeMillis());
		Intent i=new Intent(this, MainActivity.class);
		
	    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    
	    PendingIntent pi=PendingIntent.getActivity(this, 0, i, 0);
	      
	    notification.setLatestEventInfo(this, "Recommendation System", "Context monitoring...", pi);
	    notification.flags|=Notification.FLAG_AUTO_CANCEL;
		startForeground(1, notification);
		
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		_statusDetector.stopMonitoring();
		
		if(isNfcBroadcastReceiver_registered){
			unregisterReceiver(NfcBroadcastReceiver);
			isNfcBroadcastReceiver_registered = false;
		}
		
		//fermo il thread
		_timer.cancel();
		_timer = null;
	}

}
