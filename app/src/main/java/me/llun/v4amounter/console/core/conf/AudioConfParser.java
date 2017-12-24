package me.llun.v4amounter.console.core.conf;

import java.io.File;
import java.io.FileReader;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class AudioConfParser {
	public static AudioConfNode parse(String path) throws AudioConfParser.FormatException, IOException {
		return parse(new File(path));
	}

	public static AudioConfNode parse(File file) throws AudioConfParser.FormatException, IOException {
		AudioConfReader reader = new AudioConfReader(new FileReader(file));
		AudioConfNode result = parse(reader, file.getPath());
		reader.close();
		return result;
	}

	public static AudioConfNode parse(AudioConfReader reader, String filePath) throws IOException, AudioConfParser.FormatException {
		AudioConfNode root = new AudioConfNode("root");
		AudioConfNode current = root;
		String title = null;
		String str;

		while ((str = reader.readNext()) != null) {
			switch (str) {
				case "{":
					if (title == null)
						throw new FormatException(String.format("%s:%s Unexpected {", filePath, reader.getLineCode()));
					current = current.addChild(new AudioConfNode(title));
					title = null;
					break;
				case "}":
					current = current.getParent();
					if (current == null)
						throw new FormatException(String.format("%s:%s ,Unexpected }", filePath, reader.getLineCode()));
					break;
				default:
					if (title == null)
						title = str;
					else {
						current.addValue(title, str);
						title = null;
					}
			}
		}

		if (current != root)
			throw new FormatException(String.format("%s:%s Unexpected EOF", filePath, reader.getLineCode()));

		return root;
	}

	public static void writeToStream(AudioConfNode root, OutputStream output) throws IOException {
		writeToStream(root, new PaddingOutputStream(output), 0);
	}

	private static void writeToStream(AudioConfNode node, PaddingOutputStream output, int padding) throws IOException {
		for (AudioConfNode n : node.getChildren().values()) {
			output.println(n.getTitle() + " {", padding);
			writeToStream(n, output, padding + 1);
			output.println("}", padding);
		}
		for (Map.Entry<String, String> entry : node.getValues().entrySet()) {
			output.println(entry.getKey() + " " + entry.getValue(), padding);
		}
	}

	public static class FormatException extends Exception {
		public FormatException(String msg) {
			super(msg);
		}
	}

	public static class PaddingOutputStream extends FilterOutputStream {
		public PaddingOutputStream(OutputStream output) {
			super(output);
		}

		public void println(String string, int padding) throws IOException {
			this.print(string, padding);
			this.write('\n');
		}

		public void print(String string, int padding) throws IOException {
			for (int i = 0; i < padding; i++)
				this.write("    ".getBytes());
			this.write(string.getBytes());
		}
	}
}
