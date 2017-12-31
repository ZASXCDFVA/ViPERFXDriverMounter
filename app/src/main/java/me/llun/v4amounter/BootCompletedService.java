package me.llun.v4amounter;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import me.llun.v4amounter.shared.StatusUtils;
import me.llun.v4amounter.ui.TaskRunner;

public class BootCompletedService extends Service implements TaskRunner.Callback {
	private static final int NOTIFICATION_ID = 233;
	private TaskRunner runner;

	@Override
	public void onCreate() {
		super.onCreate();

		if (runner != null)
			return;

		this.initChannels();

		PendingIntent intent = TaskStackBuilder.create(this).addNextIntent(new Intent(this, MountPreferenceActivity.class)).addParentStack(MountPreferenceActivity.class).getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new NotificationCompat.Builder(this ,"default").
				setSmallIcon(R.mipmap.ic_launcher).
				setContentText(getString(R.string.mounting)).
				setContentTitle(getString(R.string.app_name)).
				setContentIntent(intent).
				setOngoing(true).
				setTicker(getString(R.string.mounting)).
				build();

		startForeground(NOTIFICATION_ID ,notification);

		runner = new TaskRunner(this, this, TaskRunner.MOUNT);
		runner.start();
	}

	@Override
	public void onTaskFinished(int request, int errorCode, String output, boolean isDriverMounted, boolean isXHiFiDriverLoaded, boolean isFxDriverLoaded) {
		if (request != TaskRunner.MOUNT)
			return;

		PendingIntent intent = TaskStackBuilder.create(this).addNextIntent(new Intent(this, MountPreferenceActivity.class)).addParentStack(MountPreferenceActivity.class).getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new NotificationCompat.Builder(this ,"default").
				setSmallIcon(R.mipmap.ic_launcher).
				setContentText(getString(errorCode == StatusUtils.SUCCESS ? R.string.mount_success : R.string.mount_failure)).
				setContentTitle(getString(R.string.app_name)).
				setContentIntent(intent).
				setTicker(getString(errorCode == StatusUtils.SUCCESS ? R.string.mount_success : R.string.mount_failure)).
				build();

		stopForeground(true);

		NotificationManagerCompat.from(this).notify(NOTIFICATION_ID ,notification);

		stopSelf();

		runner = null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_NOT_STICKY;
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return new Binder();
	}

	private void initChannels() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
			return;
		}
		NotificationManager notificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationChannel channel = new NotificationChannel("default",
				"Default",
				NotificationManager.IMPORTANCE_DEFAULT);
		if (notificationManager != null) {
			notificationManager.createNotificationChannel(channel);
		}
	}
}
