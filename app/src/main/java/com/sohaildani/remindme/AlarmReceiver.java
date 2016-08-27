package com.sohaildani.remindme;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.sohaildani.remindme.model.Alarm;
import com.sohaildani.remindme.model.AlarmMsg;

public class AlarmReceiver extends BroadcastReceiver {
	
	private static final String TAG = "AlarmReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		long alarmMsgId = intent.getLongExtra(AlarmMsg.COL_ID, -1);
		long alarmId = intent.getLongExtra(AlarmMsg.COL_ALARMID, -1);
		
		AlarmMsg alarmMsg = new AlarmMsg(alarmMsgId);
		alarmMsg.setStatus(AlarmMsg.EXPIRED);
		alarmMsg.persist(RemindMe.db);
		
		Alarm alarm = new Alarm(alarmId);
		alarm.load(RemindMe.db);
		alarm.getName();
		Uri sound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(), 0);
		Notification n = new NotificationCompat.Builder(context)
				.setContentTitle("Reminder")
				.setContentText("")
				.setSound(sound)
				.setContentIntent(pi)
				.addAction(0,"Take pills on time",pi)
				.setSmallIcon(R.drawable.ic_launcher)
					.build();
		if (RemindMe.isVibrate()) {
			n.defaults |= Notification.DEFAULT_VIBRATE;
		}
		if (alarm.getSound()) {
			n.sound = Uri.parse(RemindMe.getRingtone());
			n.defaults |= Notification.DEFAULT_SOUND;
		}
		n.flags |= Notification.FLAG_AUTO_CANCEL;
		nm.notify((int)alarmMsgId, n);
		nm.notify(1,n);

	}

}
