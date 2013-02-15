package com.recsysclient;

import java.util.ArrayList;
import java.util.List;

import com.recsysclient.R;
import com.recsysclient.entity.Evento;
import com.recsysclient.entity.Servizio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class EventListAdapter  extends BaseAdapter {

	private Context _context;
	
	private List<Evento> _lista_eventi;

	public EventListAdapter(Context context){
		this._context = context; 
		_lista_eventi = new ArrayList<Evento>();
		
	}
	
	public void setEventAndServiceList(List<Evento> lista_eventi){
		if(lista_eventi!=null) _lista_eventi = lista_eventi;
		
	}
	
	@Override
	public int getCount() {
		return _lista_eventi.size();
	}

	@Override
	public Object getItem(int position) {
		return _lista_eventi.get(position);
	}

	@Override
	public long getItemId(int position) {
		return _lista_eventi.get(position).hashCode();
	}
	
	public void updateListaEventi(List<Evento> lista_eventi){
		this._lista_eventi = lista_eventi;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Evento evento = (Evento) getItem(position);
		
		/*convertView è la view relativa all'item, ovvero determina "come" verrà visualizzato l'item
		contentView in genere è una view generata da un LayoutInflater, il quale prende in input 
		un XML (che rappresenta il layout) e lo converte in un oggetto View (o ViewGroup)*/		 
		
		//Se convertView è null devo inizializzarlo
		if(convertView == null){
			LayoutInflater inflater = LayoutInflater.from(_context);
			convertView = inflater.inflate(R.layout.evento_item, null);
		}
		
		TextView tv_name = (TextView) convertView.findViewById(R.id.event_name);
		tv_name.setText(evento.get_nomeEvento());// + "("+ evento.get_score() + ")");
		
		return convertView;
	}
}
