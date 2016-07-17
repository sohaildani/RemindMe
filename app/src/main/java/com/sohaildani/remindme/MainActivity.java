package com.sohaildani.remindme;

import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

import com.sohaildani.remindme.model.Alarm;
import com.sohaildani.remindme.model.AlarmMsg;


public class MainActivity extends ListActivity {
	
//	private static final String TAG = "MainActivity";
	
	private TextView headingText;
	private TextView rangeText;
//	private ViewSwitcher vs;
	
	private SQLiteDatabase db;
	private Typeface font;
	private AlertDialog disclaimer;
	
	public final Calendar cal = Calendar.getInstance();
	public final Date dt = new Date();
	private String[] monthArr;
	
	private Alarm alarm = new Alarm();
	private AlarmMsg alarmMsg = new AlarmMsg();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        findViews();
        db = RemindMe.db;
//        font = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Semibold.ttf");
        headingText.setTypeface(font);
        monthArr = getResources().getStringArray(R.array.spinner3_arr);
        
        int r = RemindMe.getDateRange();
		switch(r) {
		case 3: // Yearly
			cal.set(Calendar.MONTH, 0);
			
		case 2: // Monthly
			cal.set(Calendar.DATE, 1);
			
		case 1: // Weekly
			if (r==1) cal.set(Calendar.DATE, cal.getFirstDayOfWeek());
			
		case 0: // Daily
	        cal.set(Calendar.HOUR_OF_DAY, 0);
	    	cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
		}
		
		registerForContextMenu(getListView());
		
    }    
    
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong("cal", cal.getTimeInMillis());
	}	

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);
		cal.setTimeInMillis(state.getLong("cal"));
	}

	private void findViews() {
		headingText = (TextView) findViewById(R.id.heading_tv);
		rangeText = (TextView) findViewById(R.id.range_tv);
//		vs = (ViewSwitcher) findViewById(R.id.view_switcher);
	}
	
	private String getRangeStr() {
		int date = cal.get(Calendar.DATE);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		dt.setTime(System.currentTimeMillis());
		
		switch(RemindMe.getDateRange()) {
		case 0: // Daily
			if (date==dt.getDate() && month==dt.getMonth() && year==dt.getYear()+1900) return "Today";
			else return date+" "+monthArr[month+1];
			
		case 1: // Weekly
			return date+" "+monthArr[month+1] + move(+1) + " - " + cal.get(Calendar.DATE)+" "+monthArr[cal.get(Calendar.MONTH)+1] + move(-1);
			
		case 2: // Monthly
			return monthArr[month+1]+" "+year;
			
		case 3: // Yearly
			return year+"";
		}
		return null;
	}
	
	private Cursor createCursor() {
		Cursor c = RemindMe.dbHelper.listNotifications(db, cal.getTimeInMillis()+move(+1), cal.getTimeInMillis()+move(-1));
		startManagingCursor(c);
		return c;
	}
    
    @Override
	protected void onResume() {
		super.onResume();

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(
				this, 
				R.layout.row, 
				createCursor(), 
				new String[]{Alarm.COL_NAME, AlarmMsg.COL_DATETIME, AlarmMsg.COL_DATETIME, AlarmMsg.COL_DATETIME, AlarmMsg.COL_DATETIME}, 
				new int[]{R.id.msg_tv, R.id.year_tv, R.id.month_tv, R.id.date_tv, R.id.time_tv});
		
		adapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				if (view.getId() == R.id.msg_tv) return false;

				TextView tv = (TextView)view;
				long time = cursor.getLong(columnIndex);
				dt.setTime(time);
				switch(view.getId()) {
				case R.id.year_tv:
					tv.setText(String.valueOf(dt.getYear()+1900));
					break;
				case R.id.month_tv:
					tv.setText(monthArr[dt.getMonth()+1]);
					break;
				case R.id.date_tv:
					tv.setText(String.valueOf(dt.getDate()));
					break;
				case R.id.time_tv:
					long now = System.currentTimeMillis();
					String txt = RemindMe.showRemainingTime() ? Util.getRemainingTime(time, now) : Util.getActualTime(dt.getHours(), dt.getMinutes());
					if (TextUtils.isEmpty(txt)) txt = Util.getActualTime(dt.getHours(), dt.getMinutes());
					tv.setText(txt);
					
					RelativeLayout parent = (RelativeLayout)tv.getParent();
					TextView tv2 = (TextView) parent.findViewById(R.id.msg_tv);
					if (time < now) tv2.setTextColor(Color.parseColor("#555555"));
					else tv2.setTextColor(Color.parseColor("#587498"));
					break;
				}
				return true;
			}
		});
		setListAdapter(adapter);
		
		rangeText.setText(getRangeStr());
	}
    
	private String move(int step) {
		switch(RemindMe.getDateRange()) {
		case 0:
			cal.add(Calendar.DATE, 1*step);
			break;		
		case 1:
			cal.add(Calendar.DATE, 7*step);
			break;
		case 2:
			cal.add(Calendar.MONTH, 1*step);
			break;
		case 3:
			cal.add(Calendar.YEAR, 1*step);
			break;			
		}
		return "";
	}    
    
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageButton1:
			startActivity(new Intent(this, SettingsActivity.class));
			break;		
		case R.id.imageButton2:
			startActivity(new Intent(this, AddAlarmActivity.class));
			break;
		case R.id.imageButton3:
			move(-1);
			rangeText.setText(getRangeStr());
			((SimpleCursorAdapter)getListAdapter()).changeCursor(createCursor());
			break;
		case R.id.imageButton4:
			move(+1);
			rangeText.setText(getRangeStr());
			((SimpleCursorAdapter)getListAdapter()).changeCursor(createCursor());
			break;			
/*		case R.id.toggleButton1:
			vs.showNext();
			break;*/
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == android.R.id.list) {
			getMenuInflater().inflate(R.menu.context_menu, menu);
			menu.setHeaderTitle("Choose an Option");
			menu.setHeaderIcon(R.drawable.ic_dialog_menu_generic);
			
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			alarmMsg.setId(info.id);
			alarmMsg.load(db);
			if (alarmMsg.getDateTime() < System.currentTimeMillis()) 
				menu.removeItem(R.id.menu_edit);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		boolean refresh = false;
		
		switch (item.getItemId()) {
		case R.id.menu_edit:
			alarmMsg.setId(info.id);
			alarmMsg.load(db);
			alarm.reset();
			alarm.setId(alarmMsg.getAlarmId());
			alarm.load(db);
			
			showDialog(R.id.menu_edit);
			break;
			
		case R.id.menu_delete:
			RemindMe.dbHelper.cancelNotification(db, info.id, false);
			refresh = true;
			
			Intent cancelThis = new Intent(this, AlarmService.class);
			cancelThis.putExtra(AlarmMsg.COL_ID, String.valueOf(info.id));
			cancelThis.setAction(AlarmService.CANCEL);
			startService(cancelThis);
			break;
			
		case R.id.menu_delete_repeating:
			alarmMsg.setId(info.id);
			alarmMsg.load(db);
			RemindMe.dbHelper.cancelNotification(db, alarmMsg.getAlarmId(), true);
			refresh = true;
			
			Intent cancelRepeating = new Intent(this, AlarmService.class);
			cancelRepeating.putExtra(AlarmMsg.COL_ALARMID, String.valueOf(alarmMsg.getAlarmId()));
			cancelRepeating.setAction(AlarmService.CANCEL);
			startService(cancelRepeating);
			break;
		}
		
		if (refresh) {
			SimpleCursorAdapter adapter = (SimpleCursorAdapter) getListAdapter();
	    	adapter.getCursor().requery();
	    	adapter.notifyDataSetChanged();
		}
		
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		openContextMenu(v);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case R.id.menu_edit:
			return new AlertDialog.Builder(this)
			   .setTitle("Edit")
			   .setView(getLayoutInflater().inflate(R.layout.edit, null))
		       .setCancelable(false)
		       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   Dialog d = (Dialog) dialog;
		        	   EditText msgEdit = (EditText) d.findViewById(R.id.msg_et);
		        	   CheckBox soundCb = (CheckBox) d.findViewById(R.id.sound_cb);
		        	   
		        	   alarm.setSound(soundCb.isChecked());
		        	   if (!TextUtils.isEmpty(msgEdit.getText())) {
							alarm.setName(msgEdit.getText().toString());
							alarm.persist(db);
			        	   
							SimpleCursorAdapter adapter = (SimpleCursorAdapter) getListAdapter();
							adapter.getCursor().requery();
							adapter.notifyDataSetChanged();
			        	   
		        	   } else {
		        		   Toast.makeText(MainActivity.this, "Enter a message", Toast.LENGTH_SHORT).show();
		        	   }
		           }
		       })
		       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       })
		       .create();
		}
		return super.onCreateDialog(id);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		switch (id) {
		case R.id.menu_edit:
			EditText msgEdit = (EditText) dialog.findViewById(R.id.msg_et);
			CheckBox soundCb = (CheckBox) dialog.findViewById(R.id.sound_cb);
			
			msgEdit.setText(alarm.getName());
			soundCb.setChecked(alarm.getSound());
			break;
		}
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
        if (getListAdapter().isEmpty()) {
        	menu.findItem(R.id.menu_delete_all).setEnabled(false);
        } else {
        	menu.findItem(R.id.menu_delete_all).setEnabled(true);
        }
		return true;
	}	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_delete_all:
			String startTime = cal.getTimeInMillis()+move(+1);
			String endTime = cal.getTimeInMillis()+move(-1);
			RemindMe.dbHelper.cancelNotification(db, startTime, endTime);
			
			Intent cancelAll = new Intent(this, AlarmService.class);
			cancelAll.putExtra(Alarm.COL_FROMDATE, startTime);
			cancelAll.putExtra(Alarm.COL_TODATE, endTime);
			cancelAll.setAction(AlarmService.CANCEL);
			startService(cancelAll);
			
			SimpleCursorAdapter adapter = (SimpleCursorAdapter) getListAdapter();
	    	adapter.getCursor().requery();
	    	adapter.notifyDataSetChanged();			
			return true;
		}		
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		if (disclaimer != null) 
			disclaimer.dismiss();
		super.onDestroy();
	}	
	
}