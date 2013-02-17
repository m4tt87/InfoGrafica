package com.recsysclient.maps.businesslogic;

import java.util.List;

import com.recsysclient.entity.PoI;

public class BusinessLogic {
	public static final String POSITION_UPDATE = "com.recsysclient.BusinessLogic.NEW_POSITION";
	public static final String MARKERS_UPDATE = "com.recsysclient.BusinessLogic.MARKERS_UPDATE";

	private boolean hasNewPoI=false;
	
	private List<PoI> returnedList;
	private List<PoI> filteredList;
	
	
	public void getPoIList(){
		//TODO utilizza il parser per andare a recuperare la lista completa dal file xml statico
		//
	}
	
	
}
