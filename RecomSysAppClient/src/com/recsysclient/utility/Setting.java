package com.recsysclient.utility;

//Contiene i principali parametri utili ad impostare l'applicazione client
public interface Setting {
	
	//intervallo di campionamento dei sensori, posizione, stato device [in millisecondi]
	static final int SAMPLE_INTERVAL_MS = 25;
	
	//intervallo per il calcolo dello stato del contesto [in millisecondi]
	static final int STATUS_DETECTING_INTERVAL_MS = 5000;
	
	//lunghezza finestra dei campioni = 2*(STATUS_DETECTING_INTERVAL_MS / SAMPLE_INTERVAL_MS)
	static final int SAMPLES_WINDOW_LENGHT = 20;
	
	//maps package util
	//intervallo di aggiornamento della posizione
	static final int POSITION_SAMPLE_INTERVAL_MS=300;
	
	//intervallo di richiesta nuovi POI
	static final int REQUIRES_POI_INTERVAL=120000;
	
	static final int MAXKM=6000;
	
	static final String BASEURL="http://api.geonames.org/findNearbyWikipediaJSON?username=m4tt&lang=it";
	
	static final String RADIUSSTRING="&radius=";
	static final int RADIUS = 10;
	
	static final String MAXROWSSTRING="&maxrows=";
	static final int MAXROWS = 500;

	static final String LATSTRING="&lat=";
	static final String LNGSTRING="&lng=";
	
	static final float REQUIRED_ACCURACY=1500f;

	static final int ALLOWEDLOSTMESSAGES = 20;
	
}
