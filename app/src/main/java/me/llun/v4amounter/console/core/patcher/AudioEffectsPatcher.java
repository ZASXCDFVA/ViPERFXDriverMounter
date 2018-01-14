package me.llun.v4amounter.console.core.patcher;

import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import me.llun.v4amounter.console.core.conf.AudioConfParser;

/**
 * Created by null on 17-12-27.
 *
 */

public abstract class AudioEffectsPatcher {
	public abstract void putEffect(String name, String library, String libraryPath, String uuid, String soundFxDirectory);
	public abstract void removeEffects(String ...uuid);
	public abstract void removeRootNodes(String ...excludes);
	public abstract void write(String output) throws Exception;

	public static AudioEffectsPatcher load(String path) throws IOException, AudioConfParser.FormatException, ParserConfigurationException, SAXException {
		if ( path.endsWith(".conf") )
			return new ConfAudioEffectsPatcher(path);
		else if ( path.endsWith(".xml") )
			return new XmlAudioEffectsPatcher(path);

		throw new IOException("unsupported audio effects configure file format.");
	}

	public static AudioEffectsPatcher create(String sourcePath) throws IOException, AudioConfParser.FormatException, ParserConfigurationException, SAXException {
		if ( sourcePath.endsWith(".conf") )
			return new ConfAudioEffectsPatcher();
		else if ( sourcePath.endsWith(".xml") )
			return new XmlAudioEffectsPatcher();

		throw new IOException("unsupported audio effects configure file format.");
	}
}
