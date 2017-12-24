package me.llun.v4amounter.ui.exec;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.llun.v4amounter.shared.GlobalProperty;
import me.llun.v4amounter.shared.StatusUtils;
import me.llun.v4amounter.ui.exec.tools.AssetsTools;
import me.llun.v4amounter.ui.exec.tools.SuShell;

public class MountTask {
	public static final String SELINUX_NONE = "0";
	public static final String SELINUX_INJECT_POLICY = "1";
	public static final String SELINUX_DISABLE = "2";

	private MountTask() {
	}

	public static MountResult start(Context context) {
		extractSELinuxTools(context);

		SuShell shell = new SuShell();
		String viprtfxPath = findViPERFXPath(context);
		String commamd = "app_process / " + me.llun.v4amounter.console.Mount.class.getName() + " " + buildCommandArgument(context) + " " + viprtfxPath + " " + buildDriverPath();

		if (viprtfxPath.isEmpty()) {
			return new MountResult(StatusUtils.CHECK_PACKAGE, "");
		}

		shell.putCommand("export CLASSPATH=$CLASSPATH:" + context.getPackageCodePath());
		shell.putCommand("export PATH=$PATH:" + context.getFilesDir().getAbsolutePath() + "/bin");

		shell.putCommand(commamd);

		LinkedList<String> output = null;

		try {
			output = shell.execWithMountMaster();
		} catch (IOException | InterruptedException e) {
			return parseException(e);
		}

		return parseOutput(output);
	}

	private static void extractSELinuxTools(Context context) {
		for (String abi : Build.SUPPORTED_ABIS) {
			if (AssetsTools.extractAsset(context, "sepolicy-inject." + abi, context.getFilesDir().getAbsolutePath() + "/bin/sepolicy-inject", "0755", "0755"))
				return;
		}
	}


	private static String buildCommandArgument(Context context) {
		StringBuilder result = new StringBuilder();
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);

		result.append(preference.getBoolean("disable_other_effects", false) ? "--disable-other-effects " : "");
		result.append(preference.getBoolean("patch_audio_policy", true) ? "--patch-audio-policy " : "");
		result.append(preference.getBoolean("trim_useless_blocks", true) ? "--trim-useless-blocks " : "");
		result.append(preference.getBoolean("use_tmpfs", true) ? "" : "--use-disk ");

		switch (preference.getString("selinux_option", "1")) {
			case SELINUX_DISABLE:
				result.append("--disable-selinux ");
				break;
			case SELINUX_INJECT_POLICY:
				result.append("--patch-selinux-policy ");
				break;
		}

		return result.toString();
	}

	private static String findViPERFXPath(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(GlobalProperty.DEFAILT_VIPERFX_PACKAGE_NAME, 0).applicationInfo.sourceDir;
		} catch (PackageManager.NameNotFoundException e) {
		}

		return "";
	}

	private static String buildDriverPath() {
		StringBuilder result = new StringBuilder();

		result.append("assets/libv4a_fx_");

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
			result.append("jb");
		else
			result.append("ics");

		result.append("_");

		switch (Build.SUPPORTED_ABIS[0]) {
			case "x86":
			case "x64":
			case "x86_64":
				result.append("X86");
				break;
			default:
				result.append("NEON");
		}

		result.append(".so");

		return result.toString();
	}

	private static MountResult parseOutput(LinkedList<String> output) {
		Pattern pattern = Pattern.compile("^\\[([0-9a-fA-F]+)\\]");
		StringBuilder buffer = new StringBuilder();
		MountResult result = new MountResult(StatusUtils.RUN_PROGRAM, null);

		for (String str : output) {
			Matcher matcher = pattern.matcher(str);
			if (matcher.find())
				result.errorCode = Integer.parseInt(matcher.group(1), 16);
			buffer.append(str.replace("\t", "    "));
			buffer.append('\n');
		}

		result.output = buffer.toString();

		return result;
	}

	public static MountResult parseException(Exception e) {
		StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));

		return new MountResult(StatusUtils.RUN_PROGRAM, writer.toString());
	}

	public static class MountResult {
		public int errorCode;
		public String output;
		public MountResult(int errorCode, String output) {
			this.errorCode = errorCode;
			this.output = output;
		}
	}
}
