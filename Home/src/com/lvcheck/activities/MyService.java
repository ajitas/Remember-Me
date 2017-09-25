package com.lvcheck.activities;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.widget.Toast;

public class MyService extends Service {
	
	private KeyguardManager mKeyGuardManager;
	private WifiManager manager; 
	private KeyguardLock mLock;
	private NotificationManager mNotificationManager = null;
    private static final int NOTIFY_ME_ID=1337;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

 @Override
     public void onDestroy() 
     {
         super.onDestroy();   
         System.exit(0);
     }

@Override
     public void onStart(final Intent intent, int startId) 
     {
         mKeyGuardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
         mLock = mKeyGuardManager.newKeyguardLock(KEYGUARD_SERVICE);
         manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
         mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        
		                WifiInfo wifi_info = manager.getConnectionInfo();
		                String current_SSID = wifi_info.getSSID();
		                
		                if(manager.isWifiEnabled()) {
		                	
		                	SharedPreferences settings = getSharedPreferences("mypref", MODE_PRIVATE); 
		                    int size = settings.getInt("Arrsize" + "_size", 0);
		                    String array[] = new String[size];
		                    
		                    
		                    for(int i=0;i<size;i++) {
		                   	 
		                   	 
		                      array[i] = settings.getString("Arrval" + "_" + i, null);
		                      //Toast.makeText(getApplicationContext(), "Selected in newest service is "+ array[i], Toast.LENGTH_SHORT).show(); 
		                    
		                    
		                	
		                	if(array[i].equals(current_SSID)) {
		                        			mLock.disableKeyguard();
		                        			showIcon();
		                        			Toast.makeText(getApplicationContext(), "Phone is now unlocked!", Toast.LENGTH_LONG).show();
		                        			return;
		                        		}
		                        }
		                }
		         
		                else {
		                	
		                	Toast.makeText(getApplicationContext(), "Out of Wifi, Phone is now locked!", Toast.LENGTH_LONG).show();
		                	mNotificationManager.cancel(NOTIFY_ME_ID);
		                	onDestroy();
		                	return;
		                }
     }

	public void showIcon() {
 		
 		Notification notif = new Notification(
                 R.drawable.mylock,
                 "Phone Unlocked",
                 System.currentTimeMillis());

        Intent i=new Intent(this, Home.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                                      Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent detailsIntent =
                 PendingIntent.getActivity(this, 0,
                             i, PendingIntent.FLAG_CANCEL_CURRENT);

        CharSequence from = "Phone is unlocked!";
        CharSequence message = "Tap to go to the App";
        notif.setLatestEventInfo(this, from, message, detailsIntent);

        mNotificationManager.notify(NOTIFY_ME_ID, notif);
 	}

	

}