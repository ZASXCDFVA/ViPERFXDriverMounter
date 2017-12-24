package me.llun.v4amounter.console.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ShUtils {
	public static void copyDirectory(String source, String target) throws IOException {
		File src = new File(source);
		File tar = new File(target);

		src.mkdir();
		tar.mkdir();

		for (File entry : src.listFiles()) {
			if (entry.isDirectory()) {
				copyDirectory(entry.getAbsolutePath(), target + "/" + entry.getName());
				continue;
			}

			File outputFile = new File(target + "/" + entry.getName());
			FileInputStream input = new FileInputStream(entry);
			FileOutputStream output = new FileOutputStream(outputFile);
			byte[] buffer = new byte[4096];
			int readSize = 0;

			while ((readSize = input.read(buffer)) > 0) {
				output.write(buffer, 0, readSize);
			}

			input.close();
			output.close();
		}
	}

	public static void unzip(String zip, String entry, String output) throws IOException {
		ZipFile zipFile = new ZipFile(zip);
		File outputFile = new File(output);
		FileOutputStream outputStream = new FileOutputStream(outputFile);
		ZipEntry zipEntry = zipFile.getEntry(entry);
		InputStream inputStream = zipFile.getInputStream(zipEntry);
		int readSize = 0;
		byte[] buffer = new byte[4096];

		while ((readSize = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, readSize);
		}

		inputStream.close();
		outputStream.close();
		zipEntry.clone();
		zipFile.close();
	}
}
