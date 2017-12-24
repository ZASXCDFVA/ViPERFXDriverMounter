package me.llun.v4amounter.console.core;

import java.io.FileOutputStream;
import java.io.IOException;

import me.llun.v4amounter.console.core.conf.AudioConfNode;
import me.llun.v4amounter.console.core.conf.AudioConfParser;

public class AudioPolicyPatcher {
	private AudioConfNode root = null;

	public AudioPolicyPatcher(String from) {
		try {
			root = AudioConfParser.parse(from);
		} catch (Exception e) {
			root = null;
		}
	}

	public void removeNodeIfExisted(String path) {
		if (root == null)
			return;

		AudioConfNode node = root.findNodeByPath(path);
		if (node == null || node.getParent() == null)
			return;
		node.getParent().removeChild(node.getTitle());
	}

	public void write(String to) throws IOException {
		if (root == null)
			return;

		FileOutputStream stream = new FileOutputStream(to);
		stream.write("# Automatically generated file.\n".getBytes());
		stream.write("# DONT EDIT this file, it will reset after reboot.\n\n".getBytes());
		AudioConfParser.writeToStream(root, stream);
		stream.close();
	}
}
