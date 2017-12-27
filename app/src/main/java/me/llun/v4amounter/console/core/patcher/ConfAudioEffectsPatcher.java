package me.llun.v4amounter.console.core.patcher;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import me.llun.v4amounter.console.core.conf.AudioConfNode;
import me.llun.v4amounter.console.core.conf.AudioConfParser;

/**
 * Created by null on 17-12-27.
 *
 */

public class ConfAudioEffectsPatcher extends AudioEffectsPatcher {
	public ConfAudioEffectsPatcher(String file) throws IOException, AudioConfParser.FormatException {
		root = AudioConfParser.parse(file);
	}

	@Override
	public void putEffect(String name, String library, String libraryPath, String uuid) {
		AudioConfNode libraries = root.findNodeByPath("/libraries");
		if (libraries == null) {
			libraries = new AudioConfNode("libraries");
			root.addChild(libraries);
		}

		AudioConfNode effects = root.findNodeByPath("/effects");
		if (effects == null) {
			effects = new AudioConfNode("effects");
			root.addChild(effects);
		}

		libraries.addChild(new AudioConfNode(library)).addValue("path", libraryPath);
		effects.addChild(new AudioConfNode(name)).addValue("library", library).addValue("uuid", uuid);
	}

	@Override
	public void removeEffects(String ...uuid) {
		AudioConfNode node = root.findNodeByPath("/effects");
		if (node == null)
			return;

		Iterator<Map.Entry<String, AudioConfNode>> iterator = node.getChildren().entrySet().iterator();
		while (iterator.hasNext()) {
			AudioConfNode current = iterator.next().getValue();
			for (String u : uuid) {
				if (u.equals(current.getValue("uuid")))
					iterator.remove();
			}
		}
	}

	@Override
	public void removeRootNodes(String ...excludes) {
		Iterator<Map.Entry<String, AudioConfNode>> iterator = root.getChildren().entrySet().iterator();

		while (iterator.hasNext()) {
			String title = iterator.next().getValue().getTitle();
			boolean remove = true;

			for (String ex : excludes) {
				if (title.equals(ex))
					remove = false;
			}

			if (remove)
				iterator.remove();
		}
	}

	@Override
	public void write(String output) throws IOException {
		FileOutputStream stream = new FileOutputStream(output);
		stream.write("# Automatically generated file.\n".getBytes());
		stream.write("# DONT EDIT this file, it will reset after reboot.\n\n".getBytes());
		AudioConfParser.writeToStream(root, stream);
		stream.close();
	}

	private AudioConfNode root;
}
