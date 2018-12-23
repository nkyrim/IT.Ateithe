package com.nkyrim.itapp.bcreceiveres;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nkyrim.itapp.services.BulletinService;
import com.nkyrim.itapp.settings.Key;
import com.nkyrim.itapp.settings.Settings;

/**
 * BroadcastReceiver for device boot. Used to reset the AlarmManager for the bulletin notifications.
 */
public class DeviceBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent i) {
		// Check if we have activated the notifications
		if (!Settings.getBoolean(Key.NOTIFICATIONS_ACTIVATION, true)) return;

		Intent intent = new Intent(context, BulletinService.class);
		PendingIntent pintent = PendingIntent.getService(context, 0, intent, 0);

		long timeout = Settings.getLong(Key.NOTIFICATIONS_INTERVAL, 3600000);
		AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeout, timeout, pintent);
	}
}
