package me.llun.v4amounter.ui.exec.tools;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AssetsTools {
	public static boolean extractAsset(Context context, String input, String output, String permission, String directoryPermission) {
		File outputFile = new File(output);

		if (outputFile.lastModified() > new File(context.getPackageCodePath()).lastModified())
			return true;

		if (outputFile.getParentFile().mkdirs()) {
			try {
				new ProcessBuilder("chmod", directoryPermission, outputFile.getParent()).start().waitFor();
			} catch (IOException | InterruptedException ignored) {
			}
		}

		try {
			InputStream inputStream = context.getAssets().open(input);
			OutputStream outputStream = new FileOutputStream(outputFile);
			byte[] buffer = new byte[1024];

			for (int readLength = inputStream.read(buffer); readLength > 0; readLength = inputStream.read(buffer)) {
				outputStream.write(buffer, 0, readLength);
			}

			inputStream.close();
			outputStream.close();
		} catch (IOException e) {
			return false;
		}

		try {
			new ProcessBuilder().command("chmod", permission, outputFile.getAbsolutePath()).start().waitFor();
		} catch (IOException | InterruptedException e) {
		}

		return true;
	}
}
