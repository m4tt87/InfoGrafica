package com.recsysclient.profile;

import com.recsysclient.entity.Evento;
import com.recsysclient.entity.Servizio;
import com.recsysclient.messaging.MessageManager;
import com.recsysclient.network.HttpMessageClient;
import com.recsysclient.network.ServiceSocket;
import com.recsysclient.utility.AppCommonVar;

public class ProfileManager {

	private MessageManager _messageManager;
	
	public ProfileManager(){
		_messageManager = new MessageManager();
	}
	
	
	/**Invia un msg xml al server con il livello di preferenza del servizio;
	 * Il server provvede ad aggiornare i livelli di interesse relativi al servizio
	 * (e quindi il profilo utente).
	 * 
	 * @param servizio
	 * @return score del servizio (media dei livelli di interesse relativi al servizio)
	 */
	public float updateLivelloInteresseServizio(Servizio servizio){
		float score =0;
		String xmlMessage = _messageManager.getServicePreferenceXmlMessage(servizio, AppCommonVar.get_uri_utente());
		score = HttpMessageClient.getInstance().sendPreferenceXmlMessage(xmlMessage);
		return score;
	}
	
	/**Invia un msg xml al server con il livello di preferenza dell'evento;
	 * Il server provvede ad aggiornare i livelli di interesse relativi all'evento
	 * (e quindi il profilo utente).
	 * 
	 * @param evento
	 * @return score dell'evento (media dei livelli di interesse relativi all'evento)
	 */
	public float updateLivelloIntereseEvento(Evento evento){
		float score =0;
		String xmlMessage = _messageManager.getEventPreferenceXmlMessage(evento, AppCommonVar.get_uri_utente());
		score  = HttpMessageClient.getInstance().sendPreferenceXmlMessage(xmlMessage);
		return score;
	}
}
