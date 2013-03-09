package com.recsysclient.network;

public interface IMessageClient {

	
	
	
	/**
	 * Invia il messaggio al server e attende la risposta.
	 * Se la risposta contiene nuovi elementi da presentare (eventi e/o servizi) verrà restituito TRUE, altrimenti FALSE
	 * @param xmlMessage
	 * @return hasNewListElements
	 */
	
	
	public abstract boolean sendContextXmlMessage(String xmlMessage);

	public abstract float sendPreferenceXmlMessage(String xmlMessage);

	public abstract void closeClientConnection();

}