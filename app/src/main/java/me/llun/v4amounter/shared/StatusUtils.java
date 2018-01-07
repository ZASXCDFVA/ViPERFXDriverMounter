package me.llun.v4amounter.shared;

import me.llun.v4amounter.R;

public class StatusUtils {
	public static final int SUCCESS = 0;
	public static final int STARTED = 1;
	public static final int CHECK_EFFECTS_CONF_FILE = 2;
	public static final int CHECK_SOUNDFX = 3;
	public static final int CHECK_PACKAGE = 4;
	public static final int PATCH_EFFECTS_CONF = 5;
	public static final int COPY_ORIGIN_LIBRARIES = 6;
	public static final int EXTRACT_LIBRARY = 7;
	public static final int MOUNT_EFFECTS_FILES = 8;
	public static final int MOUNT_LIBRARIES = 9;
	public static final int RESTART_SYSTEM_SERVERS = 10;
	public static final int RUN_PROGRAM = -1;

	public static int getErrorMessageResource(int errno) {
		switch (errno) {
			case CHECK_EFFECTS_CONF_FILE:
				return R.string.error_lost_effects_conf_file;
			case CHECK_SOUNDFX:
				return R.string.error_lost_soundfx;
			case CHECK_PACKAGE:
				return R.string.error_lost_viperfx;
			case PATCH_EFFECTS_CONF:
				return R.string.error_patch_etc_effects;
			case COPY_ORIGIN_LIBRARIES:
				return R.string.error_copy_orgin_libraries;
			case EXTRACT_LIBRARY:
				return R.string.error_extract_library;
			case MOUNT_EFFECTS_FILES:
				return R.string.error_mount_etc_effects;
			case MOUNT_LIBRARIES:
				return R.string.error_mount_soundfx;
			case RUN_PROGRAM:
				return R.string.error_start_program;
		}

		return R.string.error_unknwen;
	}

	public static void printStatus(int statusCode) {
		String printString = "";

		switch (statusCode) {
			case STARTED:
				printString = "SCRIPT BEGIN";
				break;
			case CHECK_EFFECTS_CONF_FILE:
				printString = "Checking effects configure files.";
				break;
			case CHECK_SOUNDFX:
				printString = "Checking soundfx directories";
				break;
			case CHECK_PACKAGE:
				printString = "Checking packages";
				break;
			case PATCH_EFFECTS_CONF:
				printString = "Patching /system/etc/audio_effects.conf";
				break;
			case COPY_ORIGIN_LIBRARIES:
				printString = "Copying origin libraries";
				break;
			case EXTRACT_LIBRARY:
				printString = "Extracting libraries";
				break;
			case MOUNT_EFFECTS_FILES:
				printString = "Mounting effects configure";
				break;
			case MOUNT_LIBRARIES:
				printString = "Mounting soundfx directories";
				break;
			case RESTART_SYSTEM_SERVERS:
				printString = "Restarting system servers";
				break;
			case SUCCESS:
				printString = "SUCCESS";
				break;
		}

		System.out.println(String.format("[%X] %s", statusCode, printString));
	}

	public static void printExtraMessage(String msg) {
		System.out.print("---    ");
		System.out.println(msg);
	}
}
