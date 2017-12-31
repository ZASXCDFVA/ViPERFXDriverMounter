package me.llun.v4amounter.ui;

import android.content.Context;

import me.llun.v4amounter.ui.exec.MountTask;
import me.llun.v4amounter.ui.exec.RefreshTask;
import me.llun.v4amounter.ui.exec.RestartTask;
import me.llun.v4amounter.ui.exec.UmountTask;

public class TaskRunner extends Thread {
	public static final int MOUNT = 1;
	public static final int UMOUNT = 2;
	public static final int REFRESH = 3;
	private Context context;
	private Callback callback;
	private int request;

	public TaskRunner(Context context, Callback callback, int request) {
		this.context = context;
		this.callback = callback;
		this.request = request;

	}

	@Override
	public void run() {
		switch (request) {
			case MOUNT:
				this.mount();
				break;
			case UMOUNT:
				this.umount();
				break;
			case REFRESH:
				this.refresh();
				break;
		}
	}

	private void mount() {
		MountTask.MountResult mountResult = MountTask.start(context);
		RefreshTask.RefreshResult refreshResult = RefreshTask.start();

		if (refreshResult.isDriverLoaded ^ refreshResult.isDriverMounted) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ignored) {
			}
			RestartTask.start(context);
			refreshResult = RefreshTask.start();
		}

		callback.onTaskFinished(MOUNT, mountResult.errorCode, mountResult.output, refreshResult.isDriverMounted, refreshResult.isDriverLoaded);
	}

	private void umount() {
		new UmountTask(context).start();
		RefreshTask.RefreshResult refreshResult = RefreshTask.start();

		if (refreshResult.isDriverLoaded ^ refreshResult.isDriverMounted) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ignored) {
			}
			RestartTask.start(context);
			refreshResult = RefreshTask.start();
		}

		callback.onTaskFinished(UMOUNT, 0, "", refreshResult.isDriverMounted, refreshResult.isDriverLoaded);
	}

	private void refresh() {
		RefreshTask.RefreshResult r = RefreshTask.start();

		callback.onTaskFinished(REFRESH, 0, "", r.isDriverMounted, r.isDriverLoaded);
	}

	public interface Callback {
		void onTaskFinished(int request, int errorCode, String output, boolean isDriverMounted, boolean isDriverLoaded);
	}
}
