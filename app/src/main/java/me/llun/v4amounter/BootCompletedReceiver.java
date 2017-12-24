package me.llun.v4amounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BootCompletedReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
			return;

		SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

		if (defaultSharedPreferences.getBoolean("auto_mount_on_boot", false))
			context.startService(new Intent(context, BootCompletedService.class));
	}
}
