package com.recsysclient;

import java.util.List;

import com.recsysclient.R;
import com.recsysclient.entity.Evento;
import com.recsysclient.entity.Servizio;
import com.recsysclient.profile.ProfileManager;
import com.recsysclient.utility.AppCommonVar;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class InfoServiceActivity extends Activity {
	private static final int DIALOG_PROGRESS_ID_1 = 1;
	
	private TextView tv_nome_servizio;
	private TextView tv_descrizione;
		
	private Button b_avvia;
	
	private Bundle extras;
	
	private Servizio servizio;
	
	private ProfileManager profileManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//impedisco l'invio di altre richieste al server
		AppCommonVar.setInvia_richieste(false);
		
		//il profile manager si occupa di aggiornare il livello di interesse relativo all'evento, se specificato dall'utente
		profileManager = new ProfileManager();
		
		setContentView(R.layout.info_service_layout);
		
		extras = getIntent().getExtras();
		
		String uri_servizio = extras.getString("uri_servizio");
		
		
		
		servizio =  AppCommonVar.get_table_servizi_suggeriti().get(uri_servizio);
		
		
		tv_nome_servizio =(TextView)findViewById(R.id.nome_servizio_tv);
		tv_descrizione =(TextView)findViewById(R.id.descrizione_servizio_tv);
		
		
		
		if(servizio!=null){
			tv_nome_servizio.setText(servizio.get_nomeServizio());
			tv_descrizione.setText(servizio.get_descrizione());
		}
		
		
		
		b_avvia = (Button)findViewById(R.id.button_lancia_servizio);
		b_avvia.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(InfoServiceActivity.this, "Servizio avviato", Toast.LENGTH_SHORT).show();
				//FIXME inserire routine per avviare il servizio e inviare il messaggio che incremeneta il livello di preferenza
				//il seguente metodo richiede che venga implementato il metodo onPrepareDialog
				showDialog(DIALOG_PROGRESS_ID_1);
				
				if(servizio.get_tipoServizio().equalsIgnoreCase("app")){
//					Intent mainIntent = new Intent(Intent.ACTION_MAIN);
//					mainIntent.setPackage(servizio.get_URI_Servizio());//.setClassName("air.it.pazlab.leccecittawireless", "com.app.mainActivity");
					
					PackageManager pm = getPackageManager();
					Intent mainIntent = pm.getLaunchIntentForPackage(servizio.get_URI_Servizio());
					
					try{
						startActivity(mainIntent);
					    }
					catch (Exception e) {
						Intent marketIntent = new Intent(Intent.ACTION_VIEW);

						marketIntent.setData(Uri.parse("market://details?id="+servizio.get_URI_Servizio()));
						startActivity(marketIntent);
					}

				}
				else if(servizio.get_tipoServizio().equalsIgnoreCase("web")){
					Log.i("InfoServiceActivity.onCreate(...).new OnClickListener() {...}",
					"onClick: avvio Link web");
					Intent webIntent =new Intent(Intent.ACTION_VIEW, Uri.parse(servizio.get_URI_Servizio()));
					try{
						startActivity(webIntent);
					}
					catch (Exception e) {
						Toast.makeText(InfoServiceActivity.this, "Link non valido", Toast.LENGTH_SHORT).show();
					}

				}
				
			}
		});
		
				
	}
	
	@Override
	/**
	 * Android richiama questo metodo ogni volta che deve creare una finestra di dialogo.
	 * La finestra che deve essere creata è identificata dal parametro id.
	 * All'interno di tale metodo dobbiamo riconoscere l’identificativo (id) fornito, costruire la finestra di dialogo
	 * corrispondente e restituirla sotto forma di oggetto android.app.Dialog.
	 * In breve, si utilizza un costrutto switch per associare degli oggetti Dialog ai loro corrispettivi identificativi 
	 * numerici.
	 * 
	 */
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
		case DIALOG_PROGRESS_ID_1:
			dialog = ProgressDialog.show(this, "Attendere", "...");
			break;
		default:
			dialog = null;
			break;
		}
		return dialog;
	}
	
	@Override
	/**Dopo onCreateDialog(), la specifica finestra di dialogo dovrebbe essere stata creata. Android prima di mostrarla 
	 * richiama onPrepareDialog(), un metodo di Activity.
	 * I due parametri corrispondono, rispettivamente, all’identificativo della finestra e all’oggetto Dialog 
	 * costruito nel passaggio precedente dal metodo onCreateDialog().
	 * Anche in questo caso si utilizza un costrutto switch per associare degli oggetti Dialog ai loro corrispettivi identificativi 
	 * numerici.
	 */
	protected void onPrepareDialog(int id, Dialog dialog){
		switch(id){
		case DIALOG_PROGRESS_ID_1:
			//creo un thread eseguito in parallelo
			Thread thread = new Thread(new Runnable(){
				@Override
				public void run() {
					servizio.set_livello_preferenza(1);
					float score = profileManager.updateLivelloInteresseServizio(servizio);
					servizio.set_score(score);
					AppCommonVar.updateScoreServizio(servizio.get_URI_Individuo_Ontologia(), score);
					
					//al termine del thread chiudi la finestra di dialogo
					dismissDialog(DIALOG_PROGRESS_ID_1);
				}
			});
			thread.start();
			break;
		
		
		default:
				break;
		}
	}
	
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//riabilito l'invio delle richieste al server
		AppCommonVar.setInvia_richieste(true);
	}
}
