package com.recsysclient.nfc;

import java.io.UnsupportedEncodingException;

public class NfcUtils {

	private static NfcUtils instance = null;
	
	public static NfcUtils getInstance() {
		if(instance==null)
			instance=new NfcUtils();
		return instance;
	}
	
	public String getHexString(byte[] raw) {
		
		
		byte[] HEX_CHAR_TABLE = { (byte) '0', (byte) '1',
				(byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6',
				(byte) '7', (byte) '8', (byte) '9', (byte) 'A', (byte) 'B',
				(byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F' };
		
		byte[] hex = new byte[2 * raw.length];
		int index = 0;
		int pos = 0;

		for (byte b : raw) {
			if (pos >= raw.length)
				break;

			pos++;
			int v = b & 0xFF;
			hex[index++] = HEX_CHAR_TABLE[v >>> 4];
			hex[index++] = HEX_CHAR_TABLE[v & 0xF];
		}

		//return new String(hex);
		return hexToAscii(hex).trim();
	}
	
	
	public String hexToAscii(byte[] byteArray){
		String strAscii="";
		try {
			strAscii = new String(byteArray, "US-ASCII");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strAscii;
	}
	
	public String convertHexToString(String hex){
		 
		  StringBuilder sb = new StringBuilder();
		  StringBuilder temp = new StringBuilder();
	 
		  //49204c6f7665204a617661 split into two characters 49, 20, 4c...
		  for( int i=2; i<hex.length()-1; i+=2 ){
	 
		      //grab the hex in pairs
		      String output = hex.substring(i, (i + 2));
		      //convert hex to decimal
		      int decimal = Integer.parseInt(output, 16);
		      //convert the decimal to character
		      sb.append((char)decimal);
	 
		      temp.append(decimal);
		  }
		  System.out.println("Decimal : " + temp.toString());
	 
		  return sb.toString();
	  }
}
