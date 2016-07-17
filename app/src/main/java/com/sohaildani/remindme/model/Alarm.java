package com.sohaildani.remindme.model;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sohaildani.remindme.Util;
public class Alarm extends AbstractModel {
	
	public static final String TABLE_NAME = "alarm";
	public static final String COL_ID = AbstractModel.COL_ID;
	public static final String COL_CREATEDTIME = "created_time";
	public static final String COL_MODIFIEDTIME = "modified_time";
	public static final String COL_NAME = "name";
	public static final String COL_FROMDATE = "from_date";
	public static final String COL_TODATE = "to_date";
	public static final String COL_RULE = "rule";
	public static final String COL_INTERVAL = "interval";
	public static final String COL_SOUND = "sound";
	
	public static final String HIGH = "H";
	public static final String MED = "M";
	public static final String LOW = "L"; 
	
	static String getSql() {
		return Util.concat("CREATE TABLE ", TABLE_NAME, " (",
				AbstractModel.getSql(),
				COL_CREATEDTIME, " INTEGER, ",
				COL_MODIFIEDTIME, " INTEGER, ",
				COL_NAME, " TEXT, ",
				COL_FROMDATE, " DATE, ",
				COL_TODATE, " DATE, ",
				COL_RULE, " TEXT, ",				
				COL_INTERVAL, " TEXT, ",
				COL_SOUND, " INTEGER",
				");");
	}
	
	long save(SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		long now = System.currentTimeMillis();
		cv.put(COL_CREATEDTIME, now);
		cv.put(COL_MODIFIEDTIME, now);
		cv.put(COL_NAME, name==null ? "" : name);
		cv.put(COL_FROMDATE, fromDate);
		cv.put(COL_TODATE, toDate);
		cv.put(COL_RULE, rule);
		cv.put(COL_INTERVAL, interval);
		cv.put(COL_SOUND, sound ? 1 : 0);
		
		return db.insert(TABLE_NAME, null, cv);
	}
	
	boolean update(SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		super.update(cv);
		cv.put(COL_MODIFIEDTIME, System.currentTimeMillis());
		if (name != null)
			cv.put(COL_NAME, name);
		if (fromDate != null)
			cv.put(COL_FROMDATE, fromDate);
		if (toDate != null)
			cv.put(COL_TODATE, toDate);
		if (rule != null)
			cv.put(COL_RULE, rule);
		if (interval != null)
			cv.put(COL_INTERVAL, interval);		
		if (sound != null)
			cv.put(COL_SOUND, sound ? 1 : 0);		
		
		return db.update(TABLE_NAME, cv, COL_ID+" = ?", new String[]{String.valueOf(id)}) 
				== 1 ? true : false;
	}
	
	public boolean load(SQLiteDatabase db) {
		Cursor cursor = db.query(TABLE_NAME, null, COL_ID+" = ?", new String[]{String.valueOf(id)}, null, null, null);
		try {
			if (cursor.moveToFirst()) {
				reset();
				super.load(cursor);
				createdTime = cursor.getLong(cursor.getColumnIndex(COL_CREATEDTIME));
				modifiedTime = cursor.getLong(cursor.getColumnIndex(COL_MODIFIEDTIME));
				name = cursor.getString(cursor.getColumnIndex(COL_NAME));
				fromDate = cursor.getString(cursor.getColumnIndex(COL_FROMDATE));
				toDate = cursor.getString(cursor.getColumnIndex(COL_TODATE));
				rule = cursor.getString(cursor.getColumnIndex(COL_RULE));
				interval = cursor.getString(cursor.getColumnIndex(COL_INTERVAL));
				sound = cursor.getInt(cursor.getColumnIndex(COL_SOUND)) == 1 ? true : false;
				return true;
			}
			return false;
		} finally {
			cursor.close();
		}
	}
	
	public static Cursor list(SQLiteDatabase db) {
		String[] columns = {COL_ID, COL_NAME};
		
		return db.query(TABLE_NAME, columns, null, null, null, null, COL_CREATEDTIME+" DESC");
	}
	
	public boolean delete(SQLiteDatabase db) {
		boolean status = false;
		String[] whereArgs = new String[]{String.valueOf(id)};

		db.beginTransaction();
        try {
			db.delete(AlarmTime.TABLE_NAME, AlarmTime.COL_ALARMID+" = ?", whereArgs);
			status = db.delete(TABLE_NAME, COL_ID+" = ?", whereArgs)
					== 1 ? true : false;
	        db.setTransactionSuccessful();
	    } catch (Exception e) {
	    } finally {
	    	db.endTransaction();
	    }
		return status;		
	}	
	
	//--------------------------------------------------------------------------

	private long createdTime;
	private long modifiedTime;
	private String name;
	private String fromDate;
	private String toDate;
	private String rule;
	private String interval;
	private Boolean sound = Boolean.FALSE;
	private List<AlarmTime> occurrences;
	
	public void reset() {
		super.reset();
		createdTime = 0;
		modifiedTime = 0;
		name = null;
		fromDate = null;
		toDate = null;
		rule = null;
		interval = null;
		sound = Boolean.FALSE;
		occurrences = null;
	}	

	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	public long getModifiedTime() {
		return modifiedTime;
	}
	public void setModifiedTime(long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
	}
	public String getInterval() {
		return interval;
	}
	public void setInterval(String interval) {
		this.interval = interval;
	}	
	public Boolean getSound() {
		return sound;
	}
	public void setSound(Boolean sound) {
		this.sound = sound;
	}	
	public List<AlarmTime> getOccurrences() {
		return occurrences;
	}
	public void setOccurrences(List<AlarmTime> occurrences) {
		this.occurrences = occurrences;
	}

	public Alarm() {}
	
	public Alarm(long id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		
		return id == ((Alarm)obj).id;
	}
 
	@Override
	public int hashCode() {
		return 1;
	}	

}
