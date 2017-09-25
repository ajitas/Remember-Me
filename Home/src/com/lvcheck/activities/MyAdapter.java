package com.lvcheck.activities;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class MyAdapter<T> extends ArrayAdapter<T> {
	
	public MyAdapter(Context context, int textViewResourceId, T[] objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {  
		View view = super.getView(position, convertView, parent);  
		//TextView tv = (TextView) view;
		//else {
			//((TextView) networkList.getChildAt(pos)).setTextColor(Color.WHITE);
		//}
		if (position % 2 == 0) {
		    view.setBackgroundColor(Color.DKGRAY);  
			
		    
		} else {
		    view.setBackgroundColor(Color.TRANSPARENT);  
		}

		return view;  
		}

	

	

	}

		
