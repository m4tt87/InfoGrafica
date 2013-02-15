package com.recsysclient.device_use;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;

//Broadcast receiver che controlla lo stato dello schermo

public class ScreenReceiver extends BroadcastReceiver {
    
    // thanks Jason
    private byte screenEnabled = 1;
    
    public byte screenEnabled(){
    	return screenEnabled;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            //schermo SPENTO
            screenEnabled = 0;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            //schermo ACCESO
            screenEnabled = 1;
        }
    }

}