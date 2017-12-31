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
	public static AudioConfFile[] checkPatchFiles(String mountPoint ,String[] files) {
		ArrayList<AudioConfFile> result = new ArrayList<>();

		for ( final String f : files ) {
			if ( new File(f).isFile() )
				result.add(new AudioConfFile(){{sourceFile = f;generatedFile = String.valueOf(f.hashCode());}});
		}

		return result.toArray(new AudioConfFile[0]);
	}

	public static SoundFxDirectory[] checkSoundFxDirectory(String[] directories) {
		ArrayList<SoundFxDirectory> result = new ArrayList<>();

		for ( final String d : directories ) {
			if ( new File(d).isDirectory() )
				result.add(new SoundFxDirectory(){{sourcePath = d;generatedPath = String.valueOf(d.hashCode());}});
		}

		return result.toArray(new SoundFxDirectory[0]);
	}

	public static boolean checkPackageExisted(List<MountProperty.Effect> effectList) {
		for ( MountProperty.Effect e : effectList ) {
			if ( !new File(e.packagePath).isFile() )
				return false;
		}

		return true;
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
