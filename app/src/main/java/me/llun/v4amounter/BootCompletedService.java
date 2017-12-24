package me.llun.v4amounter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import me.llun.v4amounter.shared.StatusUtils;
import me.llun.v4amounter.ui.TaskRunner;

public class BootCompletedService extends Service implements TaskRunner.Callback {
	private static final int NOTIFICATION_ID = 233;
	private static final String NOTIFICATION_CHANNEL_ID = "BootCompleted";
	private TaskRunner runner;

	@Override
	public void onCreate() {
		super.onCreate();

		if (runner != null)
			return;

		PendingIntent intent = TaskStackBuilder.create(this).addNextIntent(new Intent(this, MountPreferenceActivity.class)).addParentStack(MountPreferenceActivity.class).getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new NotificationCompat.Builder(this ,NOTIFICATION_CHANNEL_ID).
				setSmallIcon(R.mipmap.ic_launcher).
				setContentText(getString(R.string.mounting)).
				setContentTitle(getString(R.string.app_name)).
				setContentIntent(intent).
				setOngoing(true).
				setTicker(getString(R.string.mounting)).
				build();

		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		if (nm != null) {
			nm.notify(NOTIFICATION_ID, notification);
		}

		runner = new TaskRunner(this, this, TaskRunner.MOUNT);
		runner.start();
	}

	@Override
	public void onTaskFinished(int request, int errorCode, String output, boolean mounted, boolean loaded) {
		if (request != TaskRunner.MOUNT)
			return;

		PendingIntent intent = TaskStackBuilder.create(this).addNextIntent(new Intent(this, MountPreferenceActivity.class)).addParentStack(MountPreferenceActivity.class).getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new NotificationCompat.Builder(this ,NOTIFICATION_CHANNEL_ID).
				setSmallIcon(R.mipmap.ic_launcher).
				setContentText(getString(errorCode == StatusUtils.SUCCESS ? R.string.mount_success : R.string.mount_failure)).
				setContentTitle(getString(R.string.app_name)).
				setContentIntent(intent).
				setTicker(getString(errorCode == StatusUtils.SUCCESS ? R.string.mount_success : R.string.mount_failure)).
				build();

		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		if (nm != null) {
			nm.notify(NOTIFICATION_ID, notification);
		}

		stopSelf();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent p1) {
		return new Binder();
	}
}
