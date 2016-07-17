package com.sohaildani.remindme.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sohaildani.remindme.Util;


public class AlarmMsg extends AbstractModel {
	
	public static final String TABLE_NAME = "alarmmsg";
	public static final String COL_ID = AbstractModel.COL_ID;
	public static final String COL_ALARMID = "alarm_id";
	public static final String COL_DATETIME = "datetime";
	public static final String COL_STATUS = "status";
	
	public static final String ACTIVE = "A";
	public static final String EXPIRED = "E";
	public static final String CANCELLED = "C";
	public static final String DEFERRED = "D";
	
	
	static String getSql() {
		return Util.concat("CREATE TABLE ", TABLE_NAME, " (",
				AbstractModel.getSql(),
				COL_ALARMID, " INTEGER, ",
				COL_DATETIME, " INTEGER, ",
				COL_STATUS, " TEXT",
				");");
	}
	
	long save(SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		cv.put(COL_ALARMID, alarmId);
		cv.put(COL_DATETIME, datetime);
		cv.put(COL_STATUS, status==null ? ACTIVE : status);
		
		return db.insert(TABLE_NAME, null, cv);
	}
	
	boolean update(SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		super.update(cv);
		if (alarmId > 0)
			cv.put(COL_ALARMID, alarmId);
		if (datetime > 0)
			cv.put(COL_DATETIME, datetime);
		if (status != null)
			cv.put(COL_STATUS, status);		
		
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
				datetime = cursor.getLong(cursor.getColumnIndex(COL_DATETIME));
				status = cursor.getString(cursor.getColumnIndex(COL_STATUS));
				return true;
			}
			return false;
		} finally {
			cursor.close();
		}
	}
	
	/**
	 * @param db
	 * @param args {alarmId, startTime, endTime, status, orderBy}
	 * @return cursor
	 */
	public static Cursor list(SQLiteDatabase db, String... args) {
		String[] columns = {COL_ID, COL_ALARMID, COL_DATETIME, COL_STATUS};
		String selection = "1 = 1";
		selection += (args!=null && args.length>0 && args[0]!=null) ? " AND "+COL_ALARMID+" = "+args[0] : "";
		selection += (args!=null && args.length>1 && args[1]!=null) ? " AND "+COL_DATETIME+" >= "+args[1] : "";
		selection += (args!=null && args.length>2 && args[2]!=null) ? " AND "+COL_DATETIME+" <= "+args[2] : "";
		selection += (args!=null && args.length>3 && args[3]!=null) ? " AND "+COL_STATUS+" = '"+args[3]+"'" : "";
		String orderBy = (args!=null && args.length>4) ? args[4] : COL_DATETIME+" DESC";
		
		return db.query(TABLE_NAME, columns, selection, null, null, null, orderBy);
	}
	
	public boolean delete(SQLiteDatabase db) {
		return db.delete(TABLE_NAME, COL_ID+" = ?", new String[]{String.valueOf(id)})
				== 1 ? true : false;
	}	
	
	//--------------------------------------------------------------------------

	private long alarmId;
	private long datetime;
	private String status;
	
	public void reset() {
		super.reset();
		alarmId = 0;
		datetime = 0;
		status = null;
	}

	public long getAlarmId() {
		return alarmId;
	}
	public void setAlarmId(long alarmId) {
		this.alarmId = alarmId;
	}
	public long getDateTime() {
		return datetime;
	}
	public void setDateTime(long datetime) {
		this.datetime = datetime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public AlarmMsg() {}
	
	public AlarmMsg(long id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		
		return id == ((AlarmMsg)obj).id;
	}
 
	@Override
	public int hashCode() {
		return 1;
	}	
	
}
