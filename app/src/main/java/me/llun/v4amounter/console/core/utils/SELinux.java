package me.llun.v4amounter.console.core.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SELinux {
	public static boolean isEnforcing() {
		try {
			FileInputStream inputStream = new FileInputStream("/sys/fs/selinux/enforce");
			int statu = inputStream.read();
			inputStream.close();
			return statu == '1';
		} catch (IOException e) {
		}

		return false;
	}

	public static void setEnforcing(boolean enforce) {
		try {
			FileOutputStream outputStream = new FileOutputStream("/sys/fs/selinux/enforce");
			outputStream.write(enforce ? '1' : '0');
			outputStream.close();
		} catch (IOException e) {
		}
	}

	public static boolean policyInject(String source, String target, String type, String... permissions) throws InterruptedException, IOException {
		boolean result =
				Shell.run("supolicy --live \"allow " + source + " " + target + " " + type + " { " + concatString(permissions, ' ') + " }\" ").isSuccess() ||
						Shell.run("sepolicy --live \"allow " + source + " " + target + " " + type + " { " + concatString(permissions, ' ') + " }\" ").isSuccess() ||
						Shell.run("sepolicy-inject --live \"allow " + source + " " + target + " " + type + " { " + concatString(permissions, ' ') + " }\" ").isSuccess() ||
						Shell.run("sepolicy-inject -c " + type + " -s " + source + " -t " + target + " -p " + concatString(permissions, ',') + " --load").isSuccess();
		return result;
	}

	private static String concatString(String[] strs, char separate) {
		StringBuilder result = new StringBuilder();

		for (String str : strs) {
			result.append(str);
			result.append(separate);
		}

		result.deleteCharAt(result.length() - 1);

		return result.toString();
	}
}
