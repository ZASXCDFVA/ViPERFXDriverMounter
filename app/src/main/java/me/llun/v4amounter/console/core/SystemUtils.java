package me.llun.v4amounter.console.core;

import java.io.File;
import java.io.IOException;

import me.llun.v4amounter.console.core.utils.Shell;

public class SystemUtils {
	public static final int SERVER_STATUS_UNKNOWN = 0;
	public static final int SERVER_STATUS_RUNNING = 1;
	public static final int SERVER_STATUS_STOPPING = 2;
	public static final int SERVER_STATUS_STOPPED = 3;

	public static boolean hasSELinuxSupport() {
		return new File("/sys/fs/selinux/enforce").isFile();
	}

	public static boolean hasSystemEffects() {
		return new File("/system/etc/audio_effects.conf").isFile();
	}

	public static boolean hasSystemVendorEffects() {
		return new File("/system/vendor/etc/audio_effects.conf").isFile();
	}

	public static boolean hasSystemSoundfx() {
		return new File("/system/lib/soundfx").isDirectory();
	}

	public static int checkServerStatus(String name) throws IOException, InterruptedException {
		Shell.ShellResult result = Shell.run("getprop init.svc." + name);

		switch (result.getOutput().trim()) {
			case "running":
				return SERVER_STATUS_RUNNING;
			case "stopping":
				return SERVER_STATUS_STOPPING;
			case "stopped":
				return SERVER_STATUS_STOPPED;
		}

		return SERVER_STATUS_UNKNOWN;
	}

	public static void waitForServerStatus(String name, int status) throws InterruptedException, IOException {
		while (true) {
			Thread.sleep(100);
			int currentStatus = checkServerStatus(name);
			if (currentStatus == status || currentStatus == SERVER_STATUS_UNKNOWN)
				return;
		}
	}
}
