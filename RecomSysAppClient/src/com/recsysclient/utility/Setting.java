package com.recsysclient.utility;

//Contiene i principali parametri utili ad impostare l'applicazione client
public interface Setting {
	
	//intervallo di campionamento dei sensori, posizione, stato device [in millisecondi]
	static final int SAMPLE_INTERVAL_MS = 5;
	
	//intervallo per il calcolo dello stato del contesto [in millisecondi]
	static final int STATUS_DETECTING_INTERVAL_MS = 5000;
	
	//lunghezza finestra dei campioni = 2*(STATUS_DETECTING_INTERVAL_MS / SAMPLE_INTERVAL_MS)
	static final int SAMPLES_WINDOW_LENGHT = 15;
	
	// = POSITION_SAMPLE_INTERVAL_MS/SAMPLE_INTERVAL_MS
	static final int MAPS_SAMPLE_WIN_LENGHT = 20;
	
	//maps package util
	//intervallo di aggiornamento della posizione
	static final int POSITION_SAMPLE_INTERVAL_MS=100;
	
	//intervallo di richiesta nuovi POI
	static final int REQUIRES_POI_INTERVAL=120000;
	
	static final int MAXKM=15;
	
	
}
