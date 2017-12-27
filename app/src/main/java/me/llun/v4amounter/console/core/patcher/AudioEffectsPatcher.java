package me.llun.v4amounter.console.core.patcher;

import java.io.IOException;

import me.llun.v4amounter.console.core.conf.AudioConfParser;

/**
 * Created by null on 17-12-27.
 *
 */

public abstract class AudioEffectsPatcher {
	public abstract void putEffect(String name, String library, String libraryPath, String uuid);
	public abstract void removeEffects(String ...uuid);
	public abstract void removeRootNodes(String ...excludes);
	public abstract void write(String output) throws IOException;

	public static AudioEffectsPatcher load(String path) throws IOException, AudioConfParser.FormatException {
		if ( path.endsWith(".conf") )
			return new ConfAudioEffectsPatcher(path);

		throw new IOException("unsupported audio effects configure file format.");
	}
}
