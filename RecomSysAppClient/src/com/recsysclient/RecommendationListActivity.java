package com.recsysclient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.recsysclient.R;
import com.recsysclient.entity.Evento;
import com.recsysclient.entity.Servizio;
import com.recsysclient.nfc.NfcReader;
import com.recsysclient.service.ContextMonitorService;
import com.recsysclient.utility.AppCommonVar;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RecommendationListActivity extends Activity {
	
	public static final String NFC_TRIGGER = "com.recsysclient.RecommendationListActivity";
	
	private Bundle extras;
	private List<Evento> lista_eventi;
	private List<Servizio> lista_servizi;
	
	private ListView listViewEventi;
	private ListView listViewServizi;
	
	private TextView label_lista_eventi_tv;
	private TextView label_lista_servizi_tv;
	
	//Se una richiesta è esplicita vengono visualizzati tutti gli eventi/servizi suggeriti dal sistema,
	//anche quelli già visualizzati in precedenza 
	private boolean is_explicit_request = true;
	
	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("RecommendationListActivity", "onCreate: --------------------------------------------");
		
		//impedisco l'invio di altre richieste al server
		AppCommonVar.setInvia_richieste(false);
		
		Intent intent = getIntent();
		resolveIntent(intent);
		Log.i("RecommendationListActivity", "resolveIntent: ACTION INTENT:" + intent.getAction());
		
		//imposto il contentView dell'Activity caricando il layout "eventi_list.xml"
		setContentView(R.layout.eventi_list);
		
		//recupero la lista degli eventi
		if(is_explicit_request){
			//richiesta esplicita
			Log.i("RecommendationListActivity", "RICHIESTA ESPLICITA");
			lista_eventi = AppCommonVar.getLista_eventi();
			lista_servizi = AppCommonVar.getLista_servizi();
			
			
			if( (lista_eventi==null || lista_eventi.size()==0) && (lista_servizi==null || lista_servizi.size()==0)){
//				Toast.makeText(this, "Nessun suggerimento", Toast.LENGTH_SHORT).show();
				//FIXME non viene più avviata la main activity
//				Intent mainIntent = new Intent(this,MainActivity.class);
//				startActivity(mainIntent);
				this.finish();
			}
			
		}
		else{
			//richiesta implicita
			Log.i("RecommendationListActivity", "RICHIESTA IMPLICITA");
			lista_eventi = AppCommonVar.getLista_eventi_filtrata();
			lista_servizi = AppCommonVar.getLista_servizi();
			if( (lista_eventi==null || lista_eventi.size()==0) && (lista_servizi==null || lista_servizi.size()==0)){
				this.finish();
			}
		}
		
		
		//ordino le liste in base allo score(decrescente)
		if(lista_eventi!=null){
			Collections.sort(lista_eventi);
			for(Evento e:lista_eventi)
				System.out.println(e.get_nomeEvento() + ":" + e.get_score());
		}
		if(lista_servizi!=null){
			Collections.sort(lista_servizi);
			for(Servizio s:lista_servizi)
				System.out.println(s.get_nomeServizio() + ":" + s.get_score());
		}			
			
		
		//imposto l'adapter per la visualizzaione della lista EVENTI
		EventListAdapter eventListAdapter = new EventListAdapter(this);
		eventListAdapter.setEventAndServiceList(lista_eventi);
		
		listViewEventi = (ListView)findViewById(R.id.eventList);
		listViewEventi.setAdapter(eventListAdapter);
		listViewEventi.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
				//TODO - intent esplicito per avviare LettureActivity
				
				//Ricavo l'azione selezionata
				Evento evento = lista_eventi.get(position);
				
				Intent intent = new Intent(RecommendationListActivity.this, InfoEventActivity.class);
				intent.putExtra("uri_evento", evento.get_URI_Individuo_Ontologia());
				
				startActivity(intent);				
			}
			
		});
		
		
		
		//imposto l'adapter per la visualizzaione della lista SERVIZI
		ServiceListAdapter serviceListAdapter = new ServiceListAdapter(this);

		serviceListAdapter.setServiceList(lista_servizi);
		
		listViewServizi = (ListView)findViewById(R.id.serviceList);
		listViewServizi.setAdapter(serviceListAdapter);
		listViewServizi.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
				//TODO - intent esplicito per avviare LettureActivity
				
				//Ricavo l'azione selezionata
				Servizio servizio = lista_servizi.get(position);
				Log.i("RecommendationListActivity.onCreate(...).new OnItemClickListener() {...}",
						"onItemClick: Servizio:"+ servizio.toString());
				Intent intent = new Intent(RecommendationListActivity.this, InfoServiceActivity.class);
				intent.putExtra("uri_servizio", servizio.get_URI_Individuo_Ontologia());
				
				startActivity(intent);				
			}
			
		});
		
		if(lista_servizi!=null){
			for(Servizio s:lista_servizi){
				Log.i("RecommendationListActivity", "Servizio:"+s.get_nomeServizio()+"; score:"+s.get_score());
			}
		}
		
		if(lista_eventi!=null){
			for(Evento e:lista_eventi){
				Log.i("RecommendationListActivity", "Evento:"+e.get_nomeEvento()+"; score:" + e.get_score());
			}
		}
						
	}
	
	@Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
    }
	
	
	private void resolveIntent(Intent intent) {
		//Se il servizio è avviato e l'intent è di tipo ACTION_NDEF_DISCOVERED allora gestisci richiesta al server
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())){
			if(isServiceRunning(ContextMonitorService.class.getCanonicalName())){
				//l'attività è stata avviata dalla lettura di un TAG NFC
				String idTagNfc = NfcReader.getInstance().getIdTagNfc(intent);
				
				if(idTagNfc.equalsIgnoreCase("")){
					Toast.makeText(this, "NFC Non valido", Toast.LENGTH_SHORT).show();
				    Log.i("RecommendationListActivity", "resolveIntent: NFC Non valido");
				    Intent mainIntent = new Intent(this,MainActivity.class);
					startActivity(mainIntent);
					this.finish();
				    
				}
				else{
					
	//				//Se non avviato, il servizio ContextMonitorService verrà avviato 
	//				avviaServizio();
					
					//Broadcast Intent destinato al ContextMonitorService in modo che possa inviare un messaggio al server
					Intent b_intent = new Intent(NFC_TRIGGER);
					b_intent.putExtra("idTagNfc", idTagNfc);
					sendBroadcast(b_intent);
					
					//La richiesta tramite Tag NFC viene considerata esplicita
					is_explicit_request = true;
					
					Toast.makeText(this, "Id tag NFC:" + idTagNfc, Toast.LENGTH_SHORT).show();
					Log.i("RecommendationListActivity", "resolveIntent: Id tag NFC:" + idTagNfc);
					
				}
			}
			else{
				Toast.makeText(this, "Servizio non avviato o tag NFC Non valido", Toast.LENGTH_SHORT).show();
				Log.i("RecommendationListActivity", "resolveIntent: servizio non avviato o tag nfc non valido");
				Intent mainIntent = new Intent(this,MainActivity.class);
				startActivity(mainIntent);
				this.finish();
				
			}
				
		}
		else{
			
			Bundle extras = intent.getExtras();
			if(extras!=null && extras.containsKey("is_explicit_request")){
				is_explicit_request = extras.getBoolean("is_explicit_request");
			}
		}
		
	}

	 
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("RecommendationListActivity", "onResume: --------------------------------------------");
		
		//aggiorno le liste eventi e serivizi
		lista_eventi = AppCommonVar.getLista_eventi();
		lista_servizi = AppCommonVar.getLista_servizi();
		
		
		label_lista_eventi_tv = (TextView)findViewById(R.id.label_titolo_lista_eventi);
		label_lista_servizi_tv = (TextView)findViewById(R.id.label_titolo_lista_servizi);
		if(lista_eventi==null || lista_eventi.size()==0)
			label_lista_eventi_tv.setText("Nessun Evento da suggerire");
		else
			label_lista_eventi_tv.setText(R.string.titolo_lista_eventi);
		
		if(lista_servizi==null || lista_servizi.size()==0)
			label_lista_servizi_tv.setText("Nessun Servizio da suggerire");
		else
			label_lista_servizi_tv.setText(R.string.titolo_lista_servizi);
		
		//ordino le liste in base allo score(decrescente)
		if(lista_eventi!=null)
			Collections.sort(lista_eventi);
		if(lista_servizi!=null)
			Collections.sort(lista_servizi);
		
		((EventListAdapter)listViewEventi.getAdapter()).updateListaEventi(lista_eventi);
		((ServiceListAdapter)listViewServizi.getAdapter()).updateListaServizi(lista_servizi);
		//impedisco l'invio di altre richieste al server
		
		AppCommonVar.setInvia_richieste(false);
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
//		AppCommonVar.resetResulList();
		AppCommonVar.setInvia_richieste(true);
	}
	
	protected void avviaServizio() {
		//TODO avviare servizio registrazione
		Intent intent = new Intent(this,ContextMonitorService.class);
		
		if(!isServiceRunning(ContextMonitorService.class.getCanonicalName())){
			Log.i("MainActivity", "avviaServizio: servizio non in esecuzione...sta per essere avviato");
			
			//autorizzo l'invio di messagi al server
			intent.putExtra("msg_server_attivi", true);
			startService(intent);
		}
		else
			Log.i("MainActivity", "avviaServizio: servizio già avviato... non verrà avviato nuovamente");
 }
 
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
 
}
