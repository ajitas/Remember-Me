package com.lvcheck.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Home extends Activity {
	
	private WifiManager manager;
	private ListView networkList;
	private ArrayAdapter<String> adapter;
	private KeyguardManager mKeyGuardManager;
	private KeyguardLock mLock;
	private KeyguardManager mKeyGuardManager1;
	private KeyguardLock mLock1;
	private String[] wifiArray;
	private ArrayList<String> selectedItems;
	ToggleButton toggleButton;
	private NotificationManager mNotificationManager = null;
    private static final int NOTIFY_ME_ID = 1337;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		
		super.onCreate(savedInstanceState);
		
		manager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
		if(manager.isWifiEnabled()) {
			setContentView(R.layout.main);
			
			mKeyGuardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
	        mLock = mKeyGuardManager.newKeyguardLock(KEYGUARD_SERVICE);
	        
	      //Get only configured networks, so previously saved far away networks appear
			List<WifiConfiguration> configs = manager.getConfiguredNetworks();
	        List<String> wificonfigs = new ArrayList<String>();
	        WifiInfo wifi_info = manager.getConnectionInfo();
            final String current_SSID = wifi_info.getSSID();
	        
	        for (WifiConfiguration config : configs) {
	            String SSID = config.SSID;
	            SSID = SSID.replaceAll("^\"", "");
	            SSID = SSID.replaceAll("\"$", "");
	            wificonfigs.add(SSID);
	        }
	        wifiArray = new String[wificonfigs.size()];
	        wificonfigs.toArray(wifiArray);
	       // String[] some ={"Baliga", "PCC", "City"};
	        networkList = (ListView)findViewById(R.id.lvCheckBox);
			networkList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			adapter = new MyAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, wifiArray);
			networkList.setAdapter(adapter);
			Toast.makeText(Home.this, "You are now connected to WiFi: "+ current_SSID, Toast.LENGTH_LONG).show();
			getCheckedNetworks();
			
			//startService();
			//Start service on item click
			networkList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int pos, long arg3) {
					
					SparseBooleanArray checked = networkList.getCheckedItemPositions();
			        selectedItems = new ArrayList<String>();
			        for (int i = 0; i < checked.size(); i++) {
			            // Item position in adapter
			            int position = checked.keyAt(i);
			            // Add if it is checked i.e == TRUE!
						if (checked.valueAt(i)) {
							selectedItems.add(adapter.getItem(position));
							 }
			        }
			        
			        String[] outputStrArr = new String[selectedItems.size()];
			        
			        for (int i = 0; i < selectedItems.size(); i++) {
			            outputStrArr[i] = selectedItems.get(i);
			            //Toast.makeText(Home.this, "Selected new myArr is "+ outputStrArr[i], Toast.LENGTH_SHORT).show();
			        }
			        
					String s1 = ((TextView) networkList.getChildAt(pos)).getText().toString();
					
					SharedPreferences wifiSettings = getSharedPreferences("mypref", MODE_PRIVATE);
					SharedPreferences.Editor wifiEditor = wifiSettings.edit();
					wifiEditor.clear();
					wifiEditor.putInt("Arrsize" +"_size", outputStrArr.length);
					  for(int i=0;i<outputStrArr.length;i++)
						  wifiEditor.putString("Arrval" + "_" + i, outputStrArr[i]);
					wifiEditor.commit();
					
					if(!networkList.isItemChecked(pos) && s1.equals(current_SSID)) {
						
						saveCheckedNetworks();
						//System.exit(0);
						Toast.makeText(Home.this, "Phone is now locked!", Toast.LENGTH_LONG).show();
						mLock.reenableKeyguard();
						mLock=null;
						mNotificationManager.cancel(NOTIFY_ME_ID);
						stopService();
						
					}
					
					else if(networkList.isItemChecked(pos) && s1.equals(current_SSID)) {
						
						mKeyGuardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
				        mLock = mKeyGuardManager.newKeyguardLock(KEYGUARD_SERVICE);
				        
						saveCheckedNetworks();
				        mLock.disableKeyguard();
				        showIcon();
				        Toast.makeText(Home.this, "Phone is now unlocked!", Toast.LENGTH_LONG).show(); 
					}
			    }
				
			});
			
		}
		
		else {
			setContentView(R.layout.togglebutton);
			
			mKeyGuardManager1 = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
	        mLock1 = mKeyGuardManager1.newKeyguardLock(KEYGUARD_SERVICE);
	        
			toggleButton = (ToggleButton)super.findViewById(R.id.toggle_device_admin);
	        toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton comp, boolean isChecked) {
					if(isChecked) {
						mKeyGuardManager1 = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
				        mLock1 = mKeyGuardManager1.newKeyguardLock(KEYGUARD_SERVICE);
				        
						mLock1.disableKeyguard();
						showIcon();
						
						Toast.makeText(getApplicationContext(), "Phone is now unlocked!", Toast.LENGTH_LONG).show();
					}
					else {
						mLock1.reenableKeyguard();
						mLock1=null;
						mNotificationManager.cancel(NOTIFY_ME_ID);
						Toast.makeText(getApplicationContext(), "Phone is now locked!", Toast.LENGTH_LONG).show();
					}
				} 
			});
			Toast.makeText(this, R.string.wifi_is_not_enabled_msg, Toast.LENGTH_LONG).show();
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
	
	public void onPause() {
    	super.onPause();
    	 
    }
	
	public void startService() {
		Intent intent = new Intent(this, MyService.class);
        startService(intent);
	}
	
	public void stopService() {
		Intent intent = new Intent(this, MyService.class);
        stopService(intent);
	}
	
	public void getCheckedNetworks() {
		//Get saved networks from preferences
		SharedPreferences sharedPreferences = getSharedPreferences(getClass().getName(), 0);
	    Map<String, ?> sharedPreferencesMap = sharedPreferences.getAll();
	    for(String key : sharedPreferencesMap.keySet()) {
	        int i = 0;
	        for(String network : wifiArray) {
	            if(network.equals(key)) {
	                networkList.setItemChecked(i, true);
	                break;
	            }
	            i++;
	        }
	    }	
	}

	public void saveCheckedNetworks() {
		SharedPreferences settings = getSharedPreferences(getClass().getName(), 0);
		SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        for(int i = 0; i < wifiArray.length; i++) {
            if(networkList.isItemChecked(i)) {
                editor.putBoolean(wifiArray[(int) i], true);
                
            }
        }
        editor.commit();
    }
	
	//Menu button shows shutdown app option
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			case R.id.shutDown:
				stopService(new Intent(this, MyService.class));
				return true;
			case R.id.help:
				showhelp();
			default:
				return super.onOptionsItemSelected(item);
			}
		}
		
		public void showhelp()
		{
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setMessage("When connected to WiFi:\nFrom the list of the networks shown,select your preferred network(s) by checking it.\nYour phone keyguard " +
			"will automatically get disabled when connected to any of your preferred networks.\nYou can also remove any network from your preferred list by unchecking it.\n\nWhen not connected to WiFi:\nTap the lock picture " +
			"to enable or disable your phone keyguard\n\nPressing 'Stop App' will stop the functionality of the application.\nNotification will be shown on the status bar whenever phone keyguard is disabled");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}});
			alertDialog.show();
			
		}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

}