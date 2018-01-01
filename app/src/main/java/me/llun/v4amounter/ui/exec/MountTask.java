package me.llun.v4amounter.ui.exec;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.llun.v4amounter.shared.GlobalProperty;
import me.llun.v4amounter.shared.StatusUtils;
import me.llun.v4amounter.ui.exec.tools.AssetsTools;
import me.llun.v4amounter.ui.exec.tools.SuShell;

public class MountTask {
	public static final String SELINUX_INJECT_POLICY = "1";
	public static final String SELINUX_DISABLE = "2";

	private MountTask() {
	}

	public static MountResult start(Context context) {
		extractSELinuxTools(context);

		LinkedList<String> output;
		SuShell shell = new SuShell();

		try {
			shell.putCommand("export CLASSPATH=$CLASSPATH:" + context.getPackageCodePath());
			shell.putCommand("export PATH=$PATH:" + context.getFilesDir().getAbsolutePath() + "/bin");

			shell.putCommand("app_process / " + me.llun.v4amounter.console.Mount.class.getName() + " " + buildCommonArgument(context) + buildDriverInformationArgument(context));

			output = shell.execWithMountMaster();
		} catch (Exception e) {
			return parseException(e);
		}

		return parseOutput(output);
	}

	private static void extractSELinuxTools(Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			for (String abi : Build.SUPPORTED_ABIS) {
				if (AssetsTools.extractAsset(context, "sepolicy-inject." + abi, context.getFilesDir().getAbsolutePath() + "/bin/sepolicy-inject", "0755", "0755"))
					return;
			}
		} else
			AssetsTools.extractAsset(context, "sepolicy-inject." + Build.CPU_ABI, context.getFilesDir().getAbsolutePath() + "/bin/sepolicy-inject", "0755", "0755");
	}


	private static String buildCommonArgument(Context context) {
		StringBuilder result = new StringBuilder();
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);

		result.append(preference.getBoolean("disable_other_effects", false) ? "--disable-other-effects " : "");
		result.append(preference.getBoolean("patch_audio_policy", true) ? "--patch-audio-policy " : "");
		result.append(preference.getBoolean("trim_useless_blocks", true) ? "--trim-useless-blocks " : "");
		result.append(preference.getBoolean("use_tmpfs", true) ? "--use-tmpfs " : "--use-disk ");

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

	private static String buildDriverInformationArgument(Context context) throws PackageManager.NameNotFoundException {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		StringBuilder builder = new StringBuilder();

		Set<String> versions = preferences.getStringSet("mount_version", new TreeSet<>(Arrays.asList(new String[]{"1"})));

		if (versions.contains("2")) {
			builder.append(" --add-effect v4a_fx:libv4a_fx:libv4a_fx.so:")
					.append(GlobalProperty.V4A_FX_UUID).append(":")
					.append(GlobalProperty.V4A_FX_PACKAGE_NAME).append(":")
					.append(findPackagePath(context, GlobalProperty.V4A_FX_PACKAGE_NAME)).append(":")
					.append(buildFxDriverPath());
		}

		if (versions.contains("1")) {
			builder.append(" --add-effect viperfx:libviperfx:libviperfx.so:")
					.append(GlobalProperty.VIPERFX_UUID).append(":")
					.append(GlobalProperty.VIPERFX_PACKAGE_NAME).append(":")
					.append(findPackagePath(context, GlobalProperty.VIPERFX_PACKAGE_NAME)).append(":")
					.append(buildFxDriverPath());
		}

		if (versions.contains("3")) {
			builder.append(" --add-effect v4a_xhifi:libv4a_xhifi:libv4a_xhifi.so:")
					.append(GlobalProperty.V4A_XHIFI_UUID).append(":")
					.append(GlobalProperty.V4A_XHIFI_PACKAGE_NAME).append(":")
					.append(findPackagePath(context, GlobalProperty.V4A_XHIFI_PACKAGE_NAME)).append(":")
					.append(buildXHiFiDriverPath());
		}

		return builder.toString();
	}

	private static String findPackagePath(Context context, String pack) throws PackageManager.NameNotFoundException {
		return context.getPackageManager().getPackageInfo(pack, 0).applicationInfo.sourceDir;
	}

	private static String buildFxDriverPath() {
		StringBuilder result = new StringBuilder();

		result.append("assets/libv4a_fx_");

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
			result.append("jb");
		else
			result.append("ics");

		result.append("_");

		switch (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? Build.SUPPORTED_ABIS[0] : Build.CPU_ABI) {
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

	private static String buildXHiFiDriverPath() {
		StringBuilder result = new StringBuilder();

		result.append("assets/libv4a_xhifi_ics_NEON.so");

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
			result.append(".jb");
		else
			result.append(".ics");

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

	private static MountResult parseException(Exception e) {
		StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));

		if ( e instanceof PackageManager.NameNotFoundException )
			return new MountResult(StatusUtils.CHECK_PACKAGE ,writer.toString());
		else if (e instanceof IOException || e instanceof InterruptedException )
			return new MountResult(StatusUtils.RUN_PROGRAM, writer.toString());

		return new MountResult(StatusUtils.RUN_PROGRAM ,writer.toString());
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
