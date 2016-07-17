package com.sohaildani.remindme;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.sohaildani.remindme.model.Alarm;
import com.sohaildani.remindme.model.AlarmMsg;

public class AlarmReceiver extends BroadcastReceiver {
	
	//private static final String TAG = "AlarmReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		long alarmMsgId = intent.getLongExtra(AlarmMsg.COL_ID, -1);
		long alarmId = intent.getLongExtra(AlarmMsg.COL_ALARMID, -1);
		
		AlarmMsg alarmMsg = new AlarmMsg(alarmMsgId);
		alarmMsg.setStatus(AlarmMsg.EXPIRED);
		alarmMsg.persist(RemindMe.db);
		
		Alarm alarm = new Alarm(alarmId);
		alarm.load(RemindMe.db);
		PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(), 0);
		NotificationCompat.Builder n = new NotificationCompat.Builder(context)
				.addAction(R.drawable.ic_launcher,"This is Title",pi)
				.setContentText("this is notification")
				;


		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(574,n.build());

	}

}
