package com.lvcheck.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class WiFiReceiver extends BroadcastReceiver {
	
	public WiFiReceiver()
    {
        super();
       
    }
	
	@Override
	public void onReceive(Context c, Intent i) {
		
            Intent intent = new Intent(c, MyService.class);
            c.startService(intent);
	}
}
