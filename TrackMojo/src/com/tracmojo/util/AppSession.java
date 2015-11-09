package com.tracmojo.util;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSession {
	private SharedPreferences preferences;

	public AppSession(Context context) {
		// TODO Auto-generated constructor stub
		preferences = context.getSharedPreferences("tracmojo", context.MODE_PRIVATE);
	}
	
	public SharedPreferences getPreferences() {
		return preferences;
	}
}
