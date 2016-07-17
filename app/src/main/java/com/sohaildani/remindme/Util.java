package com.sohaildani.remindme;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.sohaildani.remindme.model.DbHelper;


public class Util {

	public static final StringBuilder sb = new StringBuilder();
	
	public static final double MIN = 60 * 1000.0;
	public static final double HOUR = 60 * MIN;
	public static final double DAY = 24 * HOUR;
	public static final double MONTH = 30 * DAY;
	public static final double YEAR = 365 * DAY;
	
	public static String concat(Object... objects) {
		sb.setLength(0);
		for (Object obj : objects) {
			sb.append(obj);
		}
		return sb.toString();
	}
	
	public static String listToCsv(List list) {
		if (list == null || list.isEmpty())
			return "";
		
		sb.setLength(0);
		for (Object obj : list) {
			sb.append(",").append(obj);
		}
		return sb.toString().substring(1);
	}
	
	public static List csvToList(String csv) {
		if (csv == null || "".equals(csv.trim()))
			return new ArrayList();
		
		return Arrays.asList(csv.split(","));
	}
	
	 public static void fixBackgroundRepeat(View view) {
	      Drawable bg = view.getBackground();
	      if(bg != null) {
	           if(bg instanceof BitmapDrawable) {
	                BitmapDrawable bmp = (BitmapDrawable) bg;
	                bmp.mutate(); // make sure that we aren't sharing state anymore
	                bmp.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
	           }
	      }
	 }
	 
	 public static String getRemainingTime(long time, long now) {
		 long diff = time - now;
		 if (diff < 0) return null;
		 
		 String txt = "";
		 double div = diff / DAY;
		 if (div >= 1) {
			 txt += ((int)div) + "d ";
			 diff = diff - ((int)div * (long)DAY);
		 }
		 div = diff / HOUR;
		 if (div >= 1) {
			 txt += ((int)div) + "h ";
			 diff = diff - ((int)div * (long)HOUR);
		 }
		 div = diff / MIN;
		 if (div >= 1) {
			 txt += ((int)div) + "m ";
			 diff = diff - ((int)div * (long)MIN);
		 }		 
		 
		 return txt;
	 }
	 
	 public static String getActualTime(int hour, int minute) {
		 if (RemindMe.is24Hours()) return DbHelper.getTimeStr(hour, minute);
		 
		 if (hour < 12) return DbHelper.getTimeStr(hour, minute) + " AM";
		 else if (hour == 12) return DbHelper.getTimeStr(12, minute) + " PM";
		 else return DbHelper.getTimeStr(hour-12, minute) + " PM";
	 }
	 
	 public static String toPersistentTime(String txt) {
		 if (txt.contains(" AM")) return txt.replace(" AM", "");
		 else if (txt.contains(" PM")) {
			 String[] tokens = txt.replace(" PM", "").split(":");
			 if ("12".equals(tokens[0])) return tokens[0] + ":" + tokens[1];
			 else return (Integer.parseInt(tokens[0])+12) + ":" + tokens[1];
		 } 
		 else return txt;
	 }
	 
	public static String toPersistentDate(String txt, SimpleDateFormat sdf) {
		try {
			return DbHelper.sdf.format(sdf.parse(txt));
		} catch (ParseException e) {}
		return txt;
	}

}
