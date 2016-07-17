package com.sohaildani.remindme;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.sohaildani.remindme.model.Alarm;
import com.sohaildani.remindme.model.AlarmMsg;
import com.sohaildani.remindme.model.AlarmTime;
import com.sohaildani.remindme.model.DbHelper;
public class AddAlarmActivity extends Activity {
	
//	private static final String TAG = "AddAlarmActivity";
	
	private EditText msgEdit;
	private CheckBox soundCb;
	private DatePicker datePicker;
	private TimePicker timePicker;
	private TextView fromdateText;
	private TextView todateText;
	private TextView attimeText;
	
	private ViewSwitcher vs;
	private RadioGroup rg;
	private RelativeLayout rl3;
	private RelativeLayout rl4;
	
	private Spinner spinner1;
	private Spinner spinner2;
	private Spinner spinner3;
	
	private EditText minsEdit;
	private EditText hoursEdit;
	private EditText daysEdit;
	private EditText monthsEdit;
	private EditText yearsEdit;
	
	private SQLiteDatabase db;
	
	private static final int DIALOG_FROMDATE = 1;
	private static final int DIALOG_TODATE = 2;
	private static final int DIALOG_ATTIME = 3;
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat();
	
	private AdapterView.OnItemSelectedListener spinnerListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
        	if (spinner1.getSelectedItemPosition() > 0 && spinner2.getSelectedItemPosition() > 0)
        		spinner1.setSelection(0);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
        }
    };
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("New Reminder");
        setContentView(R.layout.add);
        findViews();
        db = RemindMe.db;
        
        rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId) {
				case R.id.radio0:
					rl3.setVisibility(View.VISIBLE);
					rl4.setVisibility(View.GONE);
					break;
				case R.id.radio1:
					rl4.setVisibility(View.VISIBLE);
					rl3.setVisibility(View.GONE);					
					break;					
				}
			}
		});
        
        spinner1.setOnItemSelectedListener(spinnerListener);
        spinner2.setOnItemSelectedListener(spinnerListener);
    }
    
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("vs", vs.getDisplayedChild());
//		outState.putInt("date", datePicker.getDayOfMonth());
//		outState.putInt("month", datePicker.getMonth());
//		outState.putInt("year", datePicker.getYear());
		outState.putInt("hour", timePicker.getCurrentHour());
//		outState.putInt("min", timePicker.getCurrentMinute());
		outState.putCharSequence("fromdate", fromdateText.getText());
		outState.putCharSequence("todate", todateText.getText());
		outState.putCharSequence("attime", attimeText.getText());
	}	

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);
		vs.setDisplayedChild(state.getInt("vs"));
//		datePicker.updateDate(state.getInt("year"), state.getInt("month"), state.getInt("date"));
		timePicker.setCurrentHour(state.getInt("hour"));
//		timePicker.setCurrentMinute(state.getInt("min"));
		fromdateText.setText(state.getCharSequence("fromdate"));
		todateText.setText(state.getCharSequence("todate"));
		attimeText.setText(state.getCharSequence("attime"));
	}    
        
	@Override
	protected void onResume() {
		super.onResume();
		sdf.applyPattern(RemindMe.getDateFormat());
	}

	private void findViews() {
		msgEdit = (EditText) findViewById(R.id.msg_et);
		soundCb = (CheckBox) findViewById(R.id.sound_cb);
		datePicker = (DatePicker) findViewById(R.id.datePicker);
		timePicker = (TimePicker) findViewById(R.id.timePicker);
		fromdateText = (TextView) findViewById(R.id.fromdate_tv);
		todateText = (TextView) findViewById(R.id.todate_tv);
		attimeText = (TextView) findViewById(R.id.attime_tv);
        vs = (ViewSwitcher) findViewById(R.id.view_switcher);
        rg = (RadioGroup) findViewById(R.id.radioGroup);
        rl3 = (RelativeLayout) findViewById(R.id.relativeLayout3);
        rl4 = (RelativeLayout) findViewById(R.id.relativeLayout4);
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner3 = (Spinner) findViewById(R.id.spinner3);
        
        minsEdit = (EditText) findViewById(R.id.mins_et);
        hoursEdit = (EditText) findViewById(R.id.hours_et);
        daysEdit = (EditText) findViewById(R.id.days_et);
        monthsEdit = (EditText) findViewById(R.id.months_et);
        yearsEdit = (EditText) findViewById(R.id.years_et);
	}
	
	private boolean validate() {
		if (TextUtils.isEmpty(msgEdit.getText())) {
			msgEdit.requestFocus();
			Toast.makeText(this, "Enter a message", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (vs.getDisplayedChild() == 1) {
			if (TextUtils.isEmpty(fromdateText.getText())) {
				Toast.makeText(this, "Specify from date", Toast.LENGTH_SHORT).show();
				return false;
			}
			if (TextUtils.isEmpty(todateText.getText())) {
				Toast.makeText(this, "Specify to date", Toast.LENGTH_SHORT).show();
				return false;
			}
			try {
				if (sdf.parse(fromdateText.getText().toString()).after(sdf.parse(todateText.getText().toString()))) {
					Toast.makeText(this, "From date is after To date!", Toast.LENGTH_SHORT).show();
					return false;
				}
			} catch (ParseException e) {}			
			if (TextUtils.isEmpty(attimeText.getText())) {
				Toast.makeText(this, "Specify time", Toast.LENGTH_SHORT).show();
				return false;
			}			
		}
		return true;
	}
	
	/* Save */
	public void create(View v) {
		if (!validate()) return;
		
		Alarm alarm = new Alarm();
		alarm.setName(msgEdit.getText().toString());
		alarm.setSound(soundCb.isChecked());
		AlarmTime alarmTime = new AlarmTime();
		long alarmId = 0;
		
		switch(vs.getDisplayedChild()) {
		case 0: //one time
			alarm.setFromDate(DbHelper.getDateStr(datePicker.getYear(), datePicker.getMonth()+1, datePicker.getDayOfMonth()));
			alarmId = alarm.persist(db);
			
			alarmTime.setAt(DbHelper.getTimeStr(timePicker.getCurrentHour(), timePicker.getCurrentMinute()));
			alarmTime.setAlarmId(alarmId);
			alarmTime.persist(db);
			break;
			
		case 1: //repeating
			alarm.setFromDate(Util.toPersistentDate(fromdateText.getText().toString(), sdf));
			alarm.setToDate(Util.toPersistentDate(todateText.getText().toString(), sdf));
			switch(rg.getCheckedRadioButtonId()) {
			case R.id.radio0: //rule
				alarm.setRule(Util.concat(spinner1.getSelectedItemPosition(), " ", 
											spinner2.getSelectedItemPosition(), " ", 
											spinner3.getSelectedItemPosition()));
				break;
			case R.id.radio1: //interval
				alarm.setInterval(Util.concat(minsEdit.getText(), " ", 
								hoursEdit.getText(), " ", 
								daysEdit.getText(), " ", 
								monthsEdit.getText(), " ", 
								yearsEdit.getText()));
				break;						
			}					
			alarmId = alarm.persist(db);
			
			alarmTime.setAt(Util.toPersistentTime(attimeText.getText().toString()));
			alarmTime.setAlarmId(alarmId);
			alarmTime.persist(db);					
			break;				
		}
		
		Intent service = new Intent(this, AlarmService.class);
		service.putExtra(AlarmMsg.COL_ALARMID, String.valueOf(alarmId));
		service.setAction(AlarmService.POPULATE);
		startService(service);

		finish();
	}
    
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.toggleButton1:
			vs.showNext();
			break;
			
		case R.id.fromdate_tv:
		case R.id.fromdate_lb:
			showDialog(DIALOG_FROMDATE);
			break;
			
		case R.id.todate_tv:
		case R.id.todate_lb:
			showDialog(DIALOG_TODATE);
			break;
			
		case R.id.attime_tv:
		case R.id.attime_lb:
			showDialog(DIALOG_ATTIME);
			break;			
		}
	}

	@Override
	protected Dialog onCreateDialog(final int id) {
		Calendar cal = Calendar.getInstance();
		switch(id) {
		case DIALOG_ATTIME:
			TimePickerDialog.OnTimeSetListener mTimeSetListener =
			    new TimePickerDialog.OnTimeSetListener() {
			        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						attimeText.setText(Util.getActualTime(hourOfDay, minute));
			        }
			    };
			return new TimePickerDialog(this, mTimeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), RemindMe.is24Hours());
		
		case DIALOG_FROMDATE:
		case DIALOG_TODATE:
			DatePickerDialog.OnDateSetListener dateListener =
			    new DatePickerDialog.OnDateSetListener() {
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						String txt = DbHelper.getDateStr(year, monthOfYear+1, dayOfMonth);
						try {
							txt = sdf.format(DbHelper.sdf.parse(txt));
						} catch (ParseException e) {}
						
						if (id == DIALOG_FROMDATE) {
							fromdateText.setText(txt);
						} else if (id == DIALOG_TODATE) {
							todateText.setText(txt);
						}
					}
				};
			return new DatePickerDialog(this, dateListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
		}		
		
		return super.onCreateDialog(id);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		
		switch(id) {
		case DIALOG_ATTIME:
			if (!TextUtils.isEmpty(attimeText.getText())) {
				String[] arr = Util.toPersistentTime(attimeText.getText().toString()).split(":");
				((TimePickerDialog)dialog).updateTime(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
			}			
			break;
			
		case DIALOG_FROMDATE:
			if (!TextUtils.isEmpty(fromdateText.getText())) {
				String[] arr = Util.toPersistentDate(fromdateText.getText().toString(), sdf).split("-");
				((DatePickerDialog)dialog).updateDate(Integer.parseInt(arr[0]), Integer.parseInt(arr[1])-1, Integer.parseInt(arr[2]));
			}			
			break;
			
		case DIALOG_TODATE:
			if (!TextUtils.isEmpty(todateText.getText())) {
				String[] arr = Util.toPersistentDate(todateText.getText().toString(), sdf).split("-");
				((DatePickerDialog)dialog).updateDate(Integer.parseInt(arr[0]), Integer.parseInt(arr[1])-1, Integer.parseInt(arr[2]));
			}			
			break;
		}
	}	

}
