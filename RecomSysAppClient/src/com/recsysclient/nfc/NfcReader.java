package com.recsysclient.nfc;


import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;

public class NfcReader {
	
	private final int TAG_MIFARE_ULTRA_LIGHT = 0;
	private final int TAG_MIFARE_CLASSIC = 1;
	private final int TAG_UNKNOWN = 2;
	private String idTagNfc="";
	
		
	private static NfcReader istanza = null;

	private NfcReader() {
	}

	// Metodo della classe impiegato per accedere al Singleton
	public static synchronized NfcReader getInstance() {
		if (istanza == null)
			istanza = new NfcReader();
		return istanza;
	}

	public String getIdTagNfc(Intent intent) {
		int sector = 0;
		int block = 0;
		int tagType = TAG_UNKNOWN;
		
		byte[] data;
		
		Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		
		for(String techName:tagFromIntent.getTechList()){
			
			if(techName.contains("MifareUltralight")){
				tagType = TAG_MIFARE_ULTRA_LIGHT;
				break;
			}
			else if(techName.contains("MifareClassic")){
				
				tagType = TAG_MIFARE_CLASSIC;
				break;
			}
			else{
				tagType = TAG_UNKNOWN;
				break;
			}
		}
		
		Log.i("Activity1", "resolveIntent: tipo tag:"+ tagType);
		
		switch (tagType) {
		case TAG_MIFARE_CLASSIC:
			MifareClassic mfc = MifareClassic.get(tagFromIntent);
			
			
			try { 
				
				mfc.connect();
				
					boolean auth = false;
					String cardData = null;
					
					auth = mfc.authenticateSectorWithKeyB(sector, MifareClassic.KEY_DEFAULT);
					
					if (auth) {
						data = mfc.readBlock(block);
						//data = mfc.readPages(block); 
						cardData = NfcUtils.getInstance().getHexString(data);
						if (cardData != null)
							idTagNfc = cardData;					
					}
					
					else
						Log.e("NfcReader", "getIdTagNfc: ERRORE! Non hai i permessi");
				
				
			} catch (IOException e) {
				Log.e("NfcReader", "resolveIntent: ERRORE!");
			}
			break;
		case TAG_MIFARE_ULTRA_LIGHT:
			
			MifareUltralight mfc_ul = MifareUltralight.get(tagFromIntent);
			
			try { 
					mfc_ul.connect();
					
					boolean auth = false;
					String cardData = null;
					
					auth = true;// mfc.authenticateSectorWithKeyB(sector, MifareClassic.KEY_DEFAULT);
					
					if (auth) {
						data = mfc_ul.readPages(0x06);
						
						
						cardData = NfcUtils.getInstance().getHexString(data);
						
						if (cardData != null)
							idTagNfc = cardData;						
					}
					
					else
						Log.e("NfcReader", "getIdTagNfc: ERRORE! Non hai i permessi");
				
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("NfcReader", "resolveIntent: ERRORE!");
			}
			break;
		
		default:
			
			break;
		}
		
		
		byte[] b;
		try {
			b = idTagNfc.getBytes("UTF-8");
		
			idTagNfc = new String(b, "US-ASCII");
			Log.i("NfcReader", "getIdTagNfc: provvisorio:"+ idTagNfc);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return convertHexToString(idTagNfc);
	}

	public String convertHexToString(String hex){
		 
		  StringBuilder sb = new StringBuilder();
		  StringBuilder temp = new StringBuilder();
	 
		  //49204c6f7665204a617661 split into two characters 49, 20, 4c...
		  for( int i=2; i<hex.length()-1; i+=2 ){
	 
		      //grab the hex in pairs
		      String output = hex.substring(i, (i + 2));
		      if(output.equalsIgnoreCase("FE"))
		    	  break;
		      //convert hex to decimal
		      int decimal = Integer.parseInt(output, 16);
		      //convert the decimal to character
		      sb.append((char)decimal);
	 
		      temp.append(decimal);
		  }
		  System.out.println("Decimal : " + temp.toString());
	 
		  return sb.toString().trim();
	  }
	
//	public String getIdTagNfc(Intent intent) {
//	byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID); // tag id
//	Parcelable[] raw;
//	raw =intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
//	NdefMessage[] rawMsgs;
//	NdefMessage msg = (NdefMessage) rawMsgs[0];
//	NdefRecord[] records = msg.getRecords();
//	String text = NdefHelper.parse(records[j]);
	
}
