package com.recsysclient.utility;

import java.util.Hashtable;

public class AppDictionary {

	//*************** SENSOR, LOCATION, DEVICE_STATUS VALUE KEY ****************
	//sono le "chiavi" che identificano, nell'Hashtable, i 'Valori' catturati nella fase di analisi dello stato
	
	//Id Sensori
	public static final int KEY_LINEAR_ACCELEROMETER_SENSOR = 0x0001;
	public static final int KEY_PROXYMITY_SENSOR = 0x0002;
	public static final int KEY_ORIENTATION_SENSOR = 0x0004;
	public static final int KEY_LIGHT_SENSOR = 0x0008;
	public static final int KEY_GYROSCOPE_SENSOR = 0x000F;
	
	//id Location element
	public static final int KEY_GPS_STATUS = 0x0011;
	public static final int KEY_LOCATION_COORD = 0x0012;
	public static final int KEY_LOCATION_SPEED = 0x0014;
	public static final int KEY_LOCATION_ACCURACY = 0x0018;
	
	//id Device Use
	public static final int KEY_DISPLAY_STATUS = 0x0021;
	
	//id Calculate Indicator
	public static final int KEY_LIN_ACC_SUM = 0x0041;
	public static final int KEY_GYROSCOPE_SUM = 0x0042;
	public static final Integer KEY_LIN_ACC_AVG = 0x0044;
	public static final Integer KEY_GYROSCOPE_AVG = 0x0048;
	public static final int KEY_LIN_ACC_MODULE = 0x0050;
	public static final int KEY_LIN_ACC_MODULE_AVG = 0x0051;
	public static final int KEY_LIN_ACC_DEV_STD = 0x0052;
	
	//***************** OUTPUT VALUE STATUS KEY *******************
	public static final int STATUS_KEY_LATITUDE = 0xF001;
	public static final int STATUS_KEY_LONGITUDE = 0xF002;
	public static final int STATUS_KEY_ALTITUDE = 0xF004;
	public static final int STATUS_KEY_ACCURACY = 0xF008;
	
	public static final int STATUS_KEY_TYPE_OF_PLACE = 0xF011;
	public static final int STATUS_KEY_CITY = 0xF012;
	public static final int STATUS_KEY_AREA = 0xF014;
	public static final int STATUS_KEY_STREET = 0xF018;
	public static final int STATUS_KEY_ADDRESS_NUMBER = 0xF01F;
	public static final int STATUS_KEY_BUILD = 0xF021;
	public static final int STATUS_KEY_FLOOR = 0xF022;
	public static final int STATUS_KEY_FLAT = 0xF024;
	public static final int STATUS_KEY_ROOM = 0xF028;
	
	public static final int STATUS_KEY_MOTION_STATE = 0xF041;
	public static final int STATUS_KEY_SPEED = 0xF042;
	
	public static final int STATUS_KEY_DEVICE_STATUS = 0xF044;
	public static final int STATUS_KEY_DEVICE_USE_TYPE = 0xF048;
	
	public static final int STATUS_KEY_LIGTH = 0xF081;
	public static final int STATUS_KEY_TEMPERATURE = 0xF082;
	public static final int STATUS_KEY_WEATHER = 0xF084;
	
	//**************** OUTPUT STATE ID ********************
	//id MotionState
	public static final int MOTION_SLEEP = 0x0041;
	public static final int MOTION_STILL = 0x0042; //Fermo
	public static final int MOTION_SIT = 0x0044; //Seduto
	public static final int MOTION_WALK = 0x0048;
	public static final int MOTION_RUN = 0x004F;
	public static final int MOTION_CYCLE = 0x0081;
	public static final int MOTION_CAR = 0x0082;
	public static final int MOTION_STILL_CAR = 0x0084;
	public static final int MOTION_NOT_AVAILABLE = 0x0088;
	
	//id device use
	public static final int DEVICE_IN_USE = 0x00F1;
	public static final int DEVICE_IN_POCKET = 0x00F2;
	public static final int DEVICE_PLACED = 0x00F4;
	public static final int DEVICE_NOT_IN_USE = 0x00F8;
	public static final int DEVICE_USE_NOT_AVAILABLE = 0x00FF;
	
	//id Location
	public static final int GPS_ON = 0x0101;
	public static final int GPS_OFF = 0x0102;
	public static final int GPS_NOT_AVAILABLE = 0x0104;
	
	//**************** KEY STRING *****************************
	public static final String MOTION_STATE = 	"MOTION_STATE";
	public static final String DEVICE_STATE = 	"DEVICE_STATE";
	public static final String COORD_STATE = 	"COORD";
	public static final String ACCURACY_STATE = "ACCURACY";
	public static final String SPEED_STATE = "SPEED";
	public static final String GPS_STATUS = 	"GPS_STATUS";
	public static final String LOCATION_PROVIDER = "LOCATION_PROVIDER";
	
	private static Hashtable<Integer, String> table_output_string_status;
	private static boolean isTableInitialized = false;
	
	
	public static String getStatoMotoForOntology(int motion_state){
		String str ="";
		
		switch (motion_state) {
		case MOTION_STILL:
		case MOTION_STILL_CAR:
		case MOTION_SIT:
			str = "fermo";
			break;
		case MOTION_CAR:
		case MOTION_WALK:
		case MOTION_CYCLE:
			str = "in movimento";
			break;
		default:
			str = "sconosciuto";
			break;
		}
		return str;
	}
	
	public static String getMezzoUtenteForOntology(int motion_state){
		String str ="";
		
		switch (motion_state) {
		
		case MOTION_CAR:
		case MOTION_STILL_CAR:
			str = "veicolo";
			break;
		case MOTION_WALK:
		case MOTION_RUN:
			str = "a piedi";
			break;			
		default:
			str = "sconosciuto";
			break;
		}
		return str;
	}
	
	
	public static String getStringStatus(int stateId){
		String strState = "";
		switch (stateId) {
		case MOTION_SLEEP:
			strState = STR_OUTPUT_MOTION_SLEEP;
			break;
			
		case MOTION_STILL:
			strState = STR_OUTPUT_MOTION_STILL;
			break;
			
		case MOTION_WALK:
			strState = STR_OUTPUT_MOTION_WALK;
			break;
			
		case MOTION_CAR:
			strState = STR_OUTPUT_MOTION_CAR;
			break;
			
		case MOTION_STILL_CAR:
			strState = STR_OUTPUT_MOTION_STILL_CAR;
			break;
			
		case MOTION_CYCLE:
			strState = STR_OUTPUT_MOTION_CYCLE;
			break;
			
		case MOTION_RUN:
			strState = STR_OUTPUT_MOTION_RUN;
			break;
			
		case MOTION_SIT:
			strState = STR_OUTPUT_MOTION_SIT;
			break;
			
		case MOTION_NOT_AVAILABLE:
			strState = STR_OUTPUT_MOTION_STATE_NOT_AVAILABLE;
			break;
			
		case DEVICE_IN_USE:
			strState = STR_OUTPUT_DEVICE_IN_USE;
			break;
			
		case DEVICE_NOT_IN_USE:
			strState = STR_OUTPUT_DEVICE_NOT_IN_USE;
			break;
			
		case DEVICE_IN_POCKET:
			strState = STR_OUTPUT_DEVICE_IN_POCKET;
			break;
			
		case DEVICE_PLACED:
			strState = STR_OUTPUT_DEVICE_PLACED;
			break;
			
		case DEVICE_USE_NOT_AVAILABLE:
			strState = STR_OUTPUT_DEVICE_USE_NOT_AVAILABLE;
			break;
			
		case GPS_NOT_AVAILABLE:
			strState = STR_OUTPUT_GPS_NOT_AVAILABLE;
			break;
			
		case GPS_ON:
			strState = STR_OUTPUT_GPS_ON;
			break;
			
		case GPS_OFF:
			strState = STR_OUTPUT_GPS_OFF;
			break;
			
		default:
			break;
		}
		
		
		return strState;
	}
	
	public static String getFraseDaSintetizzare(int motionState, int deviceUse){
		String frase1 = "";
		
		switch (motionState) {
		case MOTION_SLEEP:
			frase1 = "Stai dormendo";
			break;
			
		case MOTION_STILL:
			frase1 = "Sei Fermo";
			break;
			
		case MOTION_WALK:
			frase1 = "Stai camminando";
			break;
			
		case MOTION_CAR:
			frase1 = "Sei in auto";
			break;
			
		case MOTION_STILL_CAR:
			frase1 = "Sei fermo in auto";
			break;
			
		case MOTION_CYCLE:
			frase1 = "Sei in bicicletta";
			break;
			
		case MOTION_RUN:
			frase1 = "Stai correndo";
			break;
			
		case MOTION_SIT:
			frase1 = "Sei seduto";
			break;
			
		case MOTION_NOT_AVAILABLE:
			frase1 = "Azione non riconosciuta";
			break;
		default:
			break;
		}
		
		String frase2 = "";
		
		switch (deviceUse) {
		case DEVICE_IN_USE:
			frase2 = "stai usando il telefono.";
			break;
			
		case DEVICE_NOT_IN_USE:
			frase2 = "non stai usando il telefono.";
			break;
			
		case DEVICE_IN_POCKET:
			frase2 = "hai il telefono in tasca";
			break;
			
		case DEVICE_PLACED:
			frase2 = "il telefono è su un piano";
			break;
			
		case DEVICE_USE_NOT_AVAILABLE:
			frase2 = "utilizzo non determinato";
			break;
			default:
				break;
		}
			
		
		String congiunzione ="";
		if(frase1!="" && frase2!="") 
			congiunzione = " e ";
				
		String frase_completa = frase1+congiunzione+frase2;
		
		
		return frase_completa;
	}
	
		
	//**************** OUTPUT STATE STRING ********************
	//MotionState
	public static final String STR_OUTPUT_MOTION_SLEEP = "MOTION_SLEEP";
	public static final String STR_OUTPUT_MOTION_STILL = "MOTION_STILL"; //Fermo
	public static final String STR_OUTPUT_MOTION_SIT = "MOTION_SIT"; //Seduto
	public static final String STR_OUTPUT_MOTION_WALK = "MOTION_WALK";
	public static final String STR_OUTPUT_MOTION_RUN = "MOTION_RUN";
	public static final String STR_OUTPUT_MOTION_CYCLE = "MOTION_CYCLE";
	public static final String STR_OUTPUT_MOTION_CAR = "MOTION_CAR";
	public static final String STR_OUTPUT_MOTION_STILL_CAR = "MOTION_STILL_CAR";
	public static final String STR_OUTPUT_MOTION_STATE_NOT_AVAILABLE = "MOTION_NOT_AVAILABLE";
	
	//device use
	public static final String STR_OUTPUT_DEVICE_IN_USE = "DEVICE_IN_USE";
	public static final String STR_OUTPUT_DEVICE_IN_POCKET = "DEVICE_IN_POCKET";
	public static final String STR_OUTPUT_DEVICE_PLACED = "DEVICE_PLACED";
	public static final String STR_OUTPUT_DEVICE_NOT_IN_USE = "DEVICE_NOT_IN_USE"; 
	public static final String STR_OUTPUT_DEVICE_USE_NOT_AVAILABLE = "DEVICE_NOT_AVAILABLE"; 
	
	//GPS Status
	public static final String STR_OUTPUT_GPS_ON = "GPS ON";
	public static final String STR_OUTPUT_GPS_OFF = "GPS OFF";
	public static final String STR_OUTPUT_GPS_NOT_AVAILABLE = "GPS NOT AVAILABLE";
	
	//Maps dictionary
	public static final String LAT = "LAT";
	public static final String LNG = "LNG";
	public static final String BEARING = "BEARING";
	public static final String TILT = "TILT";
	public static final int DEFAULT_TILT=90;
	public static final String ZOOM = "ZOOM";
	public static final float DEFAULT_ZOOM=10;
	public static final String POSITION = "POSITION";
	public static final String POI = "POI";
	
	public static final String RETRIEVE_URI="http://api.geonames.org/findNearbyWikipedia?lat=40.35&lng=18.17&username=m4tt&lang=it&maxRows=500&radius=20";

	

	public static boolean existKey(int key){
		switch (key) {
			case STATUS_KEY_LATITUDE: return true;
			case STATUS_KEY_LONGITUDE: return true;
			case STATUS_KEY_ALTITUDE: return true;
			case STATUS_KEY_ACCURACY: return true;
			
			case STATUS_KEY_TYPE_OF_PLACE: return true;
			case STATUS_KEY_CITY: return true;
			case STATUS_KEY_AREA: return true;
			case STATUS_KEY_STREET: return true;
			case STATUS_KEY_ADDRESS_NUMBER: return true;
			case STATUS_KEY_BUILD: return true;
			case STATUS_KEY_FLOOR: return true;
			case STATUS_KEY_FLAT: return true;
			case STATUS_KEY_ROOM: return true;
			
			case STATUS_KEY_MOTION_STATE: return true;
			case STATUS_KEY_SPEED: return true;
			
			case STATUS_KEY_DEVICE_STATUS: return true;
			case STATUS_KEY_DEVICE_USE_TYPE: return true;
			
			case STATUS_KEY_LIGTH: return true;
			case STATUS_KEY_TEMPERATURE: return true;
			case STATUS_KEY_WEATHER: return true;
		}
		return false;

	}
	
}
