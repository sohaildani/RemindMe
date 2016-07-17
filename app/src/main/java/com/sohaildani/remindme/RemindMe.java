package com.sohaildani.remindme;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.sohaildani.remindme.model.DbHelper;


public class RemindMe extends Application {
	
//	private static final String TAG = "RemindMe";
	
	public static DbHelper dbHelper;
	public static SQLiteDatabase db;
	public static SharedPreferences sp;
	
	public static final String TIME_OPTION = "time_option";
	public static final String DATE_RANGE = "date_range";
	public static final String DATE_FORMAT = "date_format";
	public static final String TIME_FORMAT = "time_format";
	public static final String VIBRATE_PREF = "vibrate_pref";
	public static final String RINGTONE_PREF = "ringtone_pref";
	
	public static final String DEFAULT_DATE_FORMAT = "yyyy-M-d";

	@Override
	public void onCreate() {
		super.onCreate();
		
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		
		dbHelper = new DbHelper(this);
		db = dbHelper.getWritableDatabase();		
	}
	
	public static boolean showRemainingTime() {
		return "1".equals(sp.getString(TIME_OPTION, "0"));
	}
	
	public static int getDateRange() {
		return Integer.parseInt(sp.getString(DATE_RANGE, "0"));
	}
	
	public static String getDateFormat() {
		return sp.getString(DATE_FORMAT, DEFAULT_DATE_FORMAT);
	}	
	
	public static boolean is24Hours() {
		return sp.getBoolean(TIME_FORMAT, true);
	}
	
	public static boolean isVibrate() {
		return sp.getBoolean(VIBRATE_PREF, true);
	}
	
	public static String getRingtone() {
		return sp.getString(RINGTONE_PREF, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI.toString());
	}

}
