package me.llun.v4amounter.shared;

import me.llun.v4amounter.R;

public class StatusUtils {
	public static final int SUCCESS = 0;
	public static final int STARTED = 1;
	public static final int CHECK_EFFECTS_CONF_FILE = 2;
	public static final int CHECK_SOUNDFX = 3;
	public static final int CHECK_PACKAGE = 4;
	public static final int PATCH_ETC_EFFECTS = 5;
	public static final int PATCH_VENDOR_EFFECTS = 6;
	public static final int COPY_ORGIN_LIBRARIES = 7;
	public static final int EXTRACT_LIBRARY = 8;
	public static final int MOUNT_ETC_EFFECTS = 9;
	public static final int MOUNT_VENDOR_EFFECTS = 10;
	public static final int MOUNT_LIBRARIES = 11;
	public static final int RESTART_SYSTEM_SERVERS = 12;
	public static final int RUN_PROGRAM = 13;

	public static int getErrorMessageResource(int errno) {
		switch (errno) {
			case CHECK_EFFECTS_CONF_FILE:
				return R.string.error_lost_effects_conf_file;
			case CHECK_SOUNDFX:
				return R.string.error_lost_soundfx;
			case CHECK_PACKAGE:
				return R.string.error_lost_viperfx;
			case PATCH_ETC_EFFECTS:
				return R.string.error_patch_etc_effects;
			case PATCH_VENDOR_EFFECTS:
				return R.string.error_patch_vendor_effects;
			case COPY_ORGIN_LIBRARIES:
				return R.string.error_copy_orgin_libraries;
			case EXTRACT_LIBRARY:
				return R.string.error_extract_library;
			case MOUNT_ETC_EFFECTS:
				return R.string.error_mount_etc_effects;
			case MOUNT_VENDOR_EFFECTS:
				return R.string.error_mount_vendor_effects;
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
				printString = "Checking /system/etc/audio_effects.conf|/system/vendor/etc/audio_effects.conf";
				break;
			case CHECK_SOUNDFX:
				printString = "Checking /system/lib/soundfx";
				break;
			case CHECK_PACKAGE:
				printString = "Checking " + GlobalProperty.DEFAILT_VIPERFX_PACKAGE_NAME + " package";
				break;
			case PATCH_ETC_EFFECTS:
				printString = "Patching /system/etc/audio_effects.conf";
				break;
			case PATCH_VENDOR_EFFECTS:
				printString = "Patching /system/vendor/etc/audio_effects.conf";
				break;
			case COPY_ORGIN_LIBRARIES:
				printString = "Copying /system/lib/soundfx libraries";
				break;
			case EXTRACT_LIBRARY:
				printString = "Extracting " + GlobalProperty.DEFAILT_VIPERFX_PACKAGE_NAME + " library";
				break;
			case MOUNT_ETC_EFFECTS:
				printString = "Mounting /system/etc/audio_effects.conf";
				break;
			case MOUNT_VENDOR_EFFECTS:
				printString = "Mounting /system/vendor/etc/audio_effects.conf";
				break;
			case MOUNT_LIBRARIES:
				printString = "Mounting /system/lib/soundfx";
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
}
