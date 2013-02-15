package com.recsysclient.utility;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import com.recsysclient.entity.Valore;
import com.recsysclient.entity.ValoreBooleano;
import com.recsysclient.entity.ValoreDecimale;
import com.recsysclient.entity.ValoreStringa;
import com.recsysclient.entity.ValoreVettore;

public class AppUtils {
	
	private static Hashtable<Integer,Valore> new_table;
	
	public static Hashtable<Integer,Valore> cloneHashTable(Hashtable<Integer,Valore> table){
		new_table = new Hashtable<Integer, Valore>();
		
		if(table.size()>0){
			Enumeration<Integer> keyEnumeration = table.keys();
			while(keyEnumeration.hasMoreElements()){
				int key = keyEnumeration.nextElement();
				Valore valore = table.get(key);
				
				switch (valore.getTipo()) {
					case Valore.VALORE_DECIMALE:
						new_table.put(key,((ValoreDecimale)valore).clone());
						break;
						
					case Valore.VALORE_BOOLEANO:
						new_table.put(key,((ValoreBooleano)valore).clone());
						break;
						
					case Valore.VALORE_STRINGA:
						new_table.put(key,((ValoreStringa)valore).clone());
						break;
					
					case Valore.VALORE_VETTORIALE:
						new_table.put(key,((ValoreVettore)valore).clone());
						break;
		
					default:
						break;
				}
			
			}
		}
		
		return new_table;
	}
}
