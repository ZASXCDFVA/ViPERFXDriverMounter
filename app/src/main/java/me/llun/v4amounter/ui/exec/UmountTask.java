package me.llun.v4amounter.ui.exec;

import android.content.Context;

import java.io.IOException;

import me.llun.v4amounter.shared.GlobalProperty;
import me.llun.v4amounter.ui.exec.tools.SuShell;

public class UmountTask {
	private Context context;

	public UmountTask(Context context) {
		this.context = context;
	}

	public void start() {
		SuShell shell = new SuShell();

		shell.putCommand("export CLASSPATH=$CLASSPATH:" + context.getPackageCodePath());
		shell.putCommand("app_process / " + me.llun.v4amounter.console.Umount.class.getName() + " " + GlobalProperty.DEFAULT_MOUNT_POINT_TMPFS);

		try {
			shell.execWithMountMaster();
		} catch (IOException e) {
		} catch (InterruptedException e) {
		}
	}
}
