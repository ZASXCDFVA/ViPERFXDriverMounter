package me.llun.v4amounter.console.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by null on 17-12-28.
 *
 *  Script Utils
 */

public class ScriptUtils {
	public static AudioConfFile[] checkPatchConfFiles(String mountPoint , String[] files) {
		ArrayList<AudioConfFile> result = new ArrayList<>();

		for ( final String f : files ) {
			if ( new File(f).isFile() )
				result.add(new AudioConfFile(){{sourceFile = f;generatedFile = generateFileNameFromPath(f);}});
		}

		return result.toArray(new AudioConfFile[0]);
	}

	public static SoundFxDirectory[] checkSoundFxDirectory(String[] directories) {
		ArrayList<SoundFxDirectory> result = new ArrayList<>();

		for ( final String d : directories ) {
			if ( new File(d).isDirectory() )
				result.add(new SoundFxDirectory(){{sourcePath = d;generatedPath = generateFileNameFromPath(d);}});
		}

		return result.toArray(new SoundFxDirectory[0]);
	}

	public static boolean checkPackageExisted(MountProperty.Effect effect) {
		return new File(effect.packagePath).isFile();
	}

	private static String generateFileNameFromPath(String path) {
		return new File(path).getAbsolutePath().replace('/' ,'_');
	}

	public static class AudioConfFile {
		public String sourceFile;
		public String generatedFile;
	}

	public static class SoundFxDirectory {
		public String sourcePath;
		public String generatedPath;
	}
}
