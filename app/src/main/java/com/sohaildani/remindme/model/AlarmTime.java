package com.sohaildani.remindme.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sohaildani.remindme.Util;


public class AlarmTime extends AbstractModel {
	
	public static final String TABLE_NAME = "alarmtime";
	public static final String COL_ID = AbstractModel.COL_ID;
	public static final String COL_ALARMID = "alarm_id";
	public static final String COL_AT = "at";
	
	static String getSql() {
		return Util.concat("CREATE TABLE ", TABLE_NAME, " (",
				AbstractModel.getSql(),
				COL_ALARMID, " INTEGER, ",
				COL_AT, " INTEGER",
				");");
	}
	
	long save(SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		cv.put(COL_ALARMID, alarmId);
		cv.put(COL_AT, at);
		
		return db.insert(TABLE_NAME, null, cv);
	}
	
	boolean update(SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		super.update(cv);
		if (alarmId > 0)
			cv.put(COL_ALARMID, alarmId);
		if (at != null)
			cv.put(COL_AT, at);		
		
		return db.update(TABLE_NAME, cv, COL_ID+" = ?", new String[]{String.valueOf(id)}) 
				== 1 ? true : false;
	}
	
	public boolean load(SQLiteDatabase db) {
		Cursor cursor = db.query(TABLE_NAME, null, COL_ID+" = ?", new String[]{String.valueOf(id)}, null, null, null);
		try {
			if (cursor.moveToFirst()) {
				reset();
				super.load(cursor);
				alarmId = cursor.getLong(cursor.getColumnIndex(COL_ALARMID));
				at = cursor.getString(cursor.getColumnIndex(COL_AT));
				return true;
			}
			return false;
		} finally {
			cursor.close();
		}
	}
	
	/**
	 * @param db
	 * @param args {alarmId, orderBy}
	 * @return cursor
	 */
	public static Cursor list(SQLiteDatabase db, String... args) {
		String[] columns = {COL_ID, COL_AT};
		String selection = "1 = 1";
		selection += (args!=null && args.length>0 && args[0]!=null) ? " AND "+COL_ALARMID+" = "+args[0] : "";
		String orderBy = (args!=null && args.length>1) ? args[1] : COL_AT+" DESC";
		
		return db.query(TABLE_NAME, columns, selection, null, null, null, orderBy);
	}
	
	public boolean delete(SQLiteDatabase db) {
		return db.delete(TABLE_NAME, COL_ID+" = ?", new String[]{String.valueOf(id)})
				== 1 ? true : false;
	}	
	
	//--------------------------------------------------------------------------

	private long alarmId;
	private String at;
	
	public void reset() {
		super.reset();
		alarmId = 0;
		at = null;
	}

	public long getAlarmId() {
		return alarmId;
	}
	public void setAlarmId(long alarmId) {
		this.alarmId = alarmId;
	}
	public String getAt() {
		return at;
	}
	public void setAt(String at) {
		this.at = at;
	}
	
	public AlarmTime() {}
	
	public AlarmTime(long id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		
		return id == ((AlarmTime)obj).id;
	}
 
	@Override
	public int hashCode() {
		return 1;
	}	

}
