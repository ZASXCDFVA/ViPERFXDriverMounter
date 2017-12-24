package me.llun.v4amounter.console.core;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import me.llun.v4amounter.console.core.conf.AudioConfNode;
import me.llun.v4amounter.console.core.conf.AudioConfParser;

public class AudioEffectsPatcher {
	private AudioConfNode root = null;

	public AudioEffectsPatcher(String from) throws AudioConfParser.FormatException, IOException {
		root = AudioConfParser.parse(from);
	}

	public AudioEffectsPatcher() {
		root = new AudioConfNode("root");
	}

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

	public void removeEffects(String... uuids) {
		AudioConfNode node = root.findNodeByPath("/effects");
		if (node == null)
			return;

		Iterator<Map.Entry<String, AudioConfNode>> iterator = node.getChildren().entrySet().iterator();
		while (iterator.hasNext()) {
			AudioConfNode current = iterator.next().getValue();
			for (String uuid : uuids) {
				if (uuid.equals(current.getValue("uuid")))
					iterator.remove();
			}
		}
	}

	public void removeRootNodes(String... excludes) {
		Iterator<Map.Entry<String, AudioConfNode>> iterator = root.getChildren().entrySet().iterator();

		while (iterator.hasNext()) {
			String title = iterator.next().getValue().getTitle();
			boolean remove = true;

			for (String ex : excludes) {
				if (title.equals(ex))
					remove = false;
			}

			if (remove) {
				iterator.remove();
				System.out.println(title);
			}
		}
	}

	public void write(String to) throws IOException {
		FileOutputStream stream = new FileOutputStream(to);
		stream.write("# Automatically generated file.\n".getBytes());
		stream.write("# DONT EDIT this file, it will reset after reboot.\n\n".getBytes());
		AudioConfParser.writeToStream(root, stream);
		stream.close();
	}
}
