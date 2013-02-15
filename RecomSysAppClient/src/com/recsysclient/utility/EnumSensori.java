package com.recsysclient.utility;

public enum EnumSensori {

	KEY_LINEAR_ACCELEROMETER_SENSOR(0x0001),
	KEY_PROXYMITY_SENSOR(0x0002),
	KEY_ORIENTATION_SENSOR(0x0004),
	KEY_LIGHT_SENSOR(0x0008);
	
	private int key_sensor_type;
	
	private EnumSensori(int key_sensor_type) {
		this.key_sensor_type = key_sensor_type;
	}
	
	
}
