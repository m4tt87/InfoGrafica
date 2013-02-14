package com.recsysclient;

import java.util.ArrayList;
import java.util.List;

import com.recsysclient.R;
import com.recsysclient.entity.Servizio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;



public class ServiceListAdapter extends BaseAdapter {

	private Context _context;

	private List<Servizio> _lista_servizi;

	public ServiceListAdapter(Context context){
		this._context = context; 
		_lista_servizi = new ArrayList<Servizio>();

	}

	public void setServiceList(List<Servizio> lista_servizi){
		if(lista_servizi!=null) _lista_servizi = lista_servizi;

	}

	@Override
	public int getCount() {
		return _lista_servizi.size();
	}

	@Override
	public Object getItem(int position) {
		return _lista_servizi.get(position);
	}

	@Override
	public long getItemId(int position) {
		return _lista_servizi.get(position).hashCode();
	}

	public void updateListaServizi(List<Servizio> lista_servizi){
		this._lista_servizi = lista_servizi;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Servizio servizio = (Servizio) getItem(position);

		/*convertView è la view relativa all'item, ovvero determina "come" verrà visualizzato l'item
			contentView in genere è una view generata da un LayoutInflater, il quale prende in input 
			un XML (che rappresenta il layout) e lo converte in un oggetto View (o ViewGroup)*/		 

		//Se convertView è null devo inizializzarlo
		if(convertView == null){
			LayoutInflater inflater = LayoutInflater.from(_context);
			convertView = inflater.inflate(R.layout.evento_item, null);
		}

		TextView tv_name = (TextView) convertView.findViewById(R.id.event_name);
		tv_name.setText(servizio.get_nomeServizio()); //+ "("+ servizio.get_score() + ")");

		return convertView;
	}
}


