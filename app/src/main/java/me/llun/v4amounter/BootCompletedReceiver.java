package me.llun.v4amounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class BootCompletedReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
			return;

		SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

		if (defaultSharedPreferences.getBoolean("auto_mount_on_boot", false)) {
			Intent serviceIntent = new Intent(context ,BootCompletedService.class);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				context.startForegroundService(serviceIntent);
			}
			else {
				context.startService(serviceIntent);
			}
		}
	}
}
