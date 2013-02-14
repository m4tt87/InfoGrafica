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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class InfoEventActivity extends Activity {

	private static final int DIALOG_PROGRESS_ID_1 = 1;
	
	private TextView tv_nome_evento;
	private TextView tv_descrizione;
	private TextView tv_lista_servizi_label;
	private TextView tv_rating_label;
	
	private Button b_vote;
	
	private RatingBar ratingbar;
	private ListView listViewServizi;
	private List<Servizio> lista_servizi;
	
	private float feedback_value;
	
	private Bundle extras;
	
	private ProfileManager profileManager;
	
	private Evento evento;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//impedisco l'invio di altre richieste al server
		AppCommonVar.setInvia_richieste(false);
		
		//il profile manager si occupa di aggiornare il livello di interesse relativo all'evento, se specificato dall'utente
		profileManager = new ProfileManager();
		
		setContentView(R.layout.info_event_layout);
		
		extras = getIntent().getExtras();
		
		String uri_evento = extras.getString("uri_evento");
		
		
		
		evento =  AppCommonVar.get_table_eventi_suggeriti().get(uri_evento);
		
		tv_nome_evento =(TextView)findViewById(R.id.nome_evento_tv);
		tv_descrizione =(TextView)findViewById(R.id.descrizione_evento_tv);
		
		
		
		if(evento!=null){
			tv_nome_evento.setText(evento.get_nomeEvento());
			tv_descrizione.setText(evento.get_descrizione());
		}
		
		tv_rating_label = (TextView)findViewById(R.id.tv_rating_label);
		
		ratingbar = (RatingBar)findViewById(R.id.ratingBar1);
		
		feedback_value = ratingbar.getRating();
		evento.set_livello_preferenza((int)feedback_value-1);
		
		ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
			
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				
				feedback_value = rating;
				evento.set_livello_preferenza((int)feedback_value-1);
			}
		});
		
		
		b_vote = (Button)findViewById(R.id.button_vota_evento);
		b_vote.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(InfoEventActivity.this, "New Rating:"+feedback_value, Toast.LENGTH_SHORT).show();
				//FIXME inserire routine invio feedback
				//il seguente metodo richiede che venga implementato il metodo onPrepareDialog
				showDialog(DIALOG_PROGRESS_ID_1);
				
//				if(evento.get_has_livello_preferenza()){
					Log.i("InfoEventActivity.onCreate(...).new OnClickListener() {...}",
							"onClick: Nascondo ratingbar 1");
					tv_rating_label.setVisibility(View.INVISIBLE);
					ratingbar.setVisibility(View.INVISIBLE);
					b_vote.setVisibility(View.INVISIBLE);
//				}
			}
		});
		
		if(evento.get_has_livello_preferenza()){
			Log.i("InfoEventActivity", "onCreate: Nascondo rating bar 2");
			tv_rating_label.setVisibility(View.INVISIBLE);
			ratingbar.setVisibility(View.INVISIBLE);
			b_vote.setVisibility(View.INVISIBLE);
		}
		
		
		//imposto l'adapter per la visualizzaione della lista SERVIZI
		ServiceListAdapter serviceListAdapter = new ServiceListAdapter(this);

		lista_servizi = evento.get_lista_servizi();
		serviceListAdapter.setServiceList(lista_servizi);
		
		listViewServizi = (ListView)findViewById(R.id.serviceList);
		listViewServizi.setAdapter(serviceListAdapter);
		listViewServizi.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
				//TODO - intent esplicito per avviare LettureActivity
				
				//Ricavo l'azione selezionata
				Servizio servizio = lista_servizi.get(position);
				
				Intent intent = new Intent(InfoEventActivity.this, InfoServiceActivity.class);
				intent.putExtra("uri_servizio", servizio.get_URI_Individuo_Ontologia());
				
				startActivity(intent);				
			}
			
		});
		
		tv_lista_servizi_label = (TextView)findViewById(R.id.tv_lista_servizi_evento_label);
		
		if(lista_servizi==null || lista_servizi.size()==0){
			tv_lista_servizi_label.setVisibility(View.INVISIBLE);
		}
		else{
			tv_lista_servizi_label.setVisibility(View.VISIBLE);
		}
		
		
		
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
			dialog = ProgressDialog.show(this, "Attendere", "Invio in corso...");
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
					evento.set_livello_preferenza((int)feedback_value-1);
					float score = profileManager.updateLivelloIntereseEvento(evento);
					evento.set_score(score);
					evento.set_has_livello_preferenza(true);
					AppCommonVar.updateScoreEvento(evento.get_URI_Individuo_Ontologia(), score);
					
					
					
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
