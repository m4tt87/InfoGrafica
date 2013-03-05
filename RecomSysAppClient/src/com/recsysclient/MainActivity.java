package com.recsysclient;


import java.util.Locale; 

import com.recsysclient.R;
import com.recsysclient.maps.MapsActivity;
import com.recsysclient.maps.businesslogic.BusinessLogic;
import com.recsysclient.network.ServiceSocket;
import com.recsysclient.service.ContextMonitorService;
import com.recsysclient.utility.AppDictionary;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * @author Claudio
 *
 */
public class MainActivity extends Activity {
	
//	private TextView tw_label_posizione;
//	private TextView tw_label_posizione_lat;
//	private TextView tw_label_posizione_long;
//	private TextView tw_label_velocita;
//	private TextView tw_label_direzione_moto;
//	private TextView tw_label_stato_moto;
//	private TextView tw_label_stato_uso;

	private TextView tw_LocationProvider;
	private TextView tw_stato_GPS;
	private TextView tw_posizione_lat;
	private TextView tw_posizione_long;
	private TextView tw_accuratezza;
	private TextView tw_avg_acc;
	private TextView tw_avg_gyr;
	private TextView tw_velocita;	
	private TextView tw_stato_moto;
	private TextView tw_stato_uso;
	
	private ToggleButton toggleButton;
	private ToggleButton toggleButton_msg;
	private ToggleButton toggleButton_voce;
	
	private boolean msg_server_attivi = false;
	private boolean voce_attiva = false;
	
	private BroadcastReceiver screenReceiver;
	private BroadcastReceiver serviceBroadcastReceiver;
	
	//TTS - Text To Speak
	private static final int TTS_CHECK_REQUEST_CODE = 1000;
	private TextToSpeech ttsEngine = null;
	private boolean ttsInited = false;
	
	private String xmlMessage="<messaggio vuoto>";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Verifica la disponibilità del TTS (Text To Speak).
		/*Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, TTS_CHECK_REQUEST_CODE);*/
        
        
		tw_LocationProvider = (TextView)findViewById(R.id.tw_LocationProvider);
        tw_stato_GPS = (TextView)findViewById(R.id.tw_GPS_status);
        tw_posizione_lat = (TextView) findViewById(R.id.tw_posizione_lat);
        tw_posizione_long = (TextView) findViewById(R.id.tw_posizione_long);
        tw_accuratezza = (TextView) findViewById(R.id.tw_accuratezza);
        tw_velocita = (TextView) findViewById(R.id.tw_velocita);
               
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        toggleButton.setOnCheckedChangeListener(buttonServiceListener);
        
        toggleButton_msg = (ToggleButton) findViewById(R.id.toggleButton2);
        toggleButton_msg.setOnCheckedChangeListener(buttonServerMsgListener);
        
        toggleButton_voce = (ToggleButton) findViewById(R.id.toggleButton3);
        toggleButton_voce.setOnCheckedChangeListener(buttonVoiceServiceListener);
        
        serviceBroadcastReceiver = new BroadcastReceiver() {
        	
        	boolean screenOn = true;
        	
        	@Override
            public void onReceive(Context context, Intent intent) {
//        		Log.i("MainActivity.onCreate(...).new BroadcastReceiver() {...}",
//						"onReceive: MainActivity Broadcast Receiver");
        		if (intent.getAction().equals(ContextMonitorService.CONTEXT_UPDATE)) {
        			String strNA = " - ";
        			Bundle extras = intent.getExtras();
        			if(extras!= null){
        				String locationProvider = extras.getString(AppDictionary.LOCATION_PROVIDER); 
        				float statoGPS = extras.getFloat(AppDictionary.GPS_STATUS);
        				int deviceUse = extras.getInt(AppDictionary.DEVICE_STATE);
        				int motionState_id = extras.getInt(AppDictionary.MOTION_STATE);
        				float[] coord = extras.getFloatArray(AppDictionary.COORD_STATE);
        				float accuratezza_coord = extras.getFloat(AppDictionary.ACCURACY_STATE);
        				float velocita = extras.getFloat(AppDictionary.SPEED_STATE);
        				
        				        				
        				boolean has_nuovi_eventi_servizi = extras.getBoolean("has_nuovi_elementi");
        					
        				xmlMessage = extras.getString("xml");
        				
        				tw_stato_GPS.setText(AppDictionary.getStringStatus((int)statoGPS));
        				tw_LocationProvider.setText(locationProvider);
        				if(coord[0]>=0){
        					tw_posizione_lat.setText(""+coord[0]);
        					tw_posizione_long.setText(""+coord[1]);
        				}
        				else{
        					tw_posizione_lat.setText(strNA);
        					tw_posizione_long.setText(strNA);
        				}

        				
        				
        				

        				if(accuratezza_coord>=0){
        					tw_accuratezza.setText(""+accuratezza_coord);
        				}
        				else{
        					tw_accuratezza.setText(strNA);
        				}
        				
        				if(velocita>=0){
        					tw_velocita.setText(""+velocita);
        				}
        				else{
        					tw_velocita.setText(strNA);
        				}

        				

        				String frase = AppDictionary.getFraseDaSintetizzare(motionState_id, deviceUse);
        				if(voce_attiva){
        					speak(frase);
//        					Log.i("MainActivity", "onCreate: VOCE_TIME:" + (System.currentTimeMillis()/1000));
        				}
        				
        				
//        				if(has_nuovi_eventi_servizi){
//        					Log.i("ContextMonitorService.onStartCommand(...).new TimerTask() {...}",
//        							"run: ------------- ci sono nuovi eventi e servizi");
//        					Intent intent2 = new Intent(MainActivity.this, RecommendationListActivity.class);
//        										
//        					startActivity(intent2);				
//        				}
//        				else{
//        					Log.i("ContextMonitorService.onStartCommand(...).new TimerTask() {...}",
//        					"run: ------------- NON ci sono nuovi eventi e servizi");
//        				}
        				
        			}
        		} else {}

        	}
       };
        
              
        IntentFilter filter2 = new IntentFilter(ContextMonitorService.CONTEXT_UPDATE);
        registerReceiver(serviceBroadcastReceiver, filter2);
        
    }
    
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == TTS_CHECK_REQUEST_CODE) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				// Ok, TTS disponibile.
				ttsEngine = new TextToSpeech(this,
						new TextToSpeech.OnInitListener() {
							@Override
							public void onInit(int status) {
								if (status == TextToSpeech.SUCCESS) {
									ttsInited = true;
								}
							}
						});
			} else {
				// TTS non disponibile, richiesta di installazione.
				Intent installIntent = new Intent();
				installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
		}
	}
    
    
        
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	if(isServiceRunning(ContextMonitorService.class.getCanonicalName())){
			toggleButton.setChecked(true);
		}
    	else{
			toggleButton.setChecked(false);
		}
    	super.onResume();
    	
    	
    }
    
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	unregisterReceiver(serviceBroadcastReceiver);
    	if (ttsInited) {
			ttsEngine.shutdown();
		}
    }
    
    protected void avviaServizio() {
		//TODO avviare servizio registrazione
    			
    	Intent intent = new Intent(this,MapsActivity.class);
    	startActivity(intent);
		/*Intent intent = new Intent(this,ContextMonitorService.class);
		intent.putExtra("msg_server_attivi", msg_server_attivi);
		intent.putExtra("voce_attiva", voce_attiva);
		if(!isServiceRunning(ContextMonitorService.class.getCanonicalName())){
			Log.i("MainActivity", "avviaServizio: servizio non in esecuzione...sta per essere avviato");
			startService(intent);
		}
		else
			Log.i("MainActivity", "avviaServizio: servizio già avviato... non verrà avviato nuovamente");*/
    }
    
    protected void arrestaServizio() {
		//TODO avviare servizio registrazione
		Intent intent = new Intent(this,ContextMonitorService.class);
		//intent.putExtra("key", value);
				
		stopService(intent);
    }
    
    OnCheckedChangeListener buttonServiceListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
				if(isChecked){
					Log.i("MainActivity.buttonRecListener.new OnCheckedChangeListener() {...}",
							"onCheckedChanged: Si avvierà il servizio...");
					toggleButton_msg.setEnabled(false);
					avviaServizio();
			}
			else{
				toggleButton_msg.setEnabled(true);
				arrestaServizio();				
			}
			
		}
	};
	
	OnCheckedChangeListener buttonVoiceServiceListener  = new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked){
						voce_attiva = true;
				}
				else{
					voce_attiva = false;		
				}
				
			}
		};
		
	OnCheckedChangeListener buttonServerMsgListener = new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked){
						msg_server_attivi = true;
				}
				else{
					msg_server_attivi = false;				
				}
				
			}
		};
	
	
	
	
	
	//determina se un servizio è in esecuzione
	/**
	 * @param completeServiceClassName: nome completo della classe Service (es. "com.mypackage.ServiceClassName")
	 * @return
	 */
	private boolean isServiceRunning(String completeServiceClassName) {
	    ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (completeServiceClassName.equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	
	/**
	 * metodo che permette al sintetizzatore vocale del telefono di generare una frase che descrive il contesto dell'utente
	 * relativo all'azione dell'utente rilevata, la sua posizione, l'uso corrente dello smartphone  
	 */
	private void speak(String frase) {
		// TTS inizializzato?
		if (!ttsInited) {
			return;
		}
		frase = frase.trim();
		if (frase.length() == 0) {
			return;
		}
		
		
		// Verifica che la lingua sia disponibile.
		int result = ttsEngine.setLanguage(Locale.ITALIAN);
		if (result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) {
			Toast.makeText(this, "Attenzione! Lingua non supportata dal sintetizzatore", Toast.LENGTH_LONG).show();
			return;
		}
		// Imposta pitch (da 0.5 a 2.0).
		ttsEngine.setPitch(1);
		// Imposta rate (da 0.5 a 2.0).
		ttsEngine.setSpeechRate(1.2f);
		// Pronuncia la frase.
		ttsEngine.speak(frase, TextToSpeech.QUEUE_ADD, null);
	}
}