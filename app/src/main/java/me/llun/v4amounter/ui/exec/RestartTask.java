package me.llun.v4amounter.ui.exec;

import android.content.Context;

import java.io.IOException;

import me.llun.v4amounter.ui.exec.tools.SuShell;

public class RestartTask {
	private RestartTask() {
	}

	public static void start(Context context) {
		SuShell shell = new SuShell();

		shell.putCommand("export CLASSPATH=$CLASSPATH:" + context.getPackageCodePath());
		shell.putCommand("app_process / " + me.llun.v4amounter.console.Restart.class.getName());

		try {
			shell.execWithMountMaster();
		} catch (IOException | InterruptedException e) {
		}
	}
}
