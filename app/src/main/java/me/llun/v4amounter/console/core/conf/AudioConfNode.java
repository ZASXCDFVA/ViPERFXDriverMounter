package me.llun.v4amounter.console.core.conf;

import java.util.LinkedHashMap;
import java.util.Map;

public class AudioConfNode {
	private LinkedHashMap<String, AudioConfNode> children = new LinkedHashMap<>();
	private LinkedHashMap<String, String> values = new LinkedHashMap<>();
	private AudioConfNode parentNode = null;
	private String title;

	public AudioConfNode(String title) {
		this.title = title;
	}

	private static String[] parsePath(String path) {
		String[] nodes = path.split("[/\\\\.]");
		if (nodes.length > 1 && nodes[0].isEmpty()) {
			String[] result = new String[nodes.length - 1];
			System.arraycopy(nodes, 1, result, 0, result.length);
			return result;
		}
		return nodes;
	}

	public String getTitle() {
		return title;
	}

	public AudioConfNode getParent() {
		return parentNode;
	}

	protected void setParent(AudioConfNode node) {
		parentNode = node;
	}

	public AudioConfNode addChild(AudioConfNode node) {
		children.put(node.getTitle(), node);
		node.setParent(this);
		return node;
	}

	public AudioConfNode addValue(String title, String value) {
		values.put(title, value);
		return this;
	}

	public AudioConfNode removeChild(String title) {
		AudioConfNode result = children.remove(title);
		result.setParent(null);
		return result;
	}

	public AudioConfNode removeValue(String key) {
		values.remove(key);
		return this;
	}

	public AudioConfNode getChild(String key) {
		return children.get(key);
	}

	public String getValue(String key) {
		return values.get(key);
	}

	public Map<String, AudioConfNode> getChildren() {
		return children;
	}

	public Map<String, String> getValues() {
		return values;
	}

	public AudioConfNode findNodeByValue(String value) {
		if (values.values().contains(value))
			return this;
		for (AudioConfNode child : children.values()) {
			AudioConfNode result = child.findNodeByValue(value);
			if (result != null)
				return result;
		}

		return null;
	}

	public AudioConfNode findNodeByPath(String path) {
		return findNodeByPath(parsePath(path), 0);
	}

	public AudioConfNode findNodeByPath(String[] nodes, int index) {
		if (index >= nodes.length)
			return this;
		AudioConfNode node = children.get(nodes[index]);
		if (node != null)
			return node.findNodeByPath(nodes, index + 1);
		return null;
	}
}
