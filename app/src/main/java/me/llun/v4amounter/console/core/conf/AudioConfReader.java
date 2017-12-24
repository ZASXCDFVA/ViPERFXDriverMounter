package me.llun.v4amounter.console.core.conf;

import java.io.FileReader;
import java.io.FilterReader;
import java.io.IOException;
import java.util.Locale;

public class AudioConfReader extends FilterReader {
	private StringBuilder buffer = new StringBuilder();
	private LineCounter lc = new LineCounter();

	public AudioConfReader(FileReader reader) {
		super(reader);
	}

	public static boolean isAsciiCharacter(int ch) {
		return ch > 0 && ch <= 126;
	}

	public String readNext() throws IOException {
		int readCharacter;
		boolean skipLine = false;

		if (buffer.length() != 0)
			return takeBufferData();

		while ((readCharacter = read()) > 0) {
			lc.pushCharacter();
			if (readCharacter == '\n')
				lc.pushLine();

			if (skipLine) {
				if (readCharacter == '\n')
					skipLine = false;
				continue;
			}

			if (!isAsciiCharacter(readCharacter)) {
				buffer.append((char) readCharacter);
				continue;
			}

			if (buffer.length() != 0) {
				switch (readCharacter) {
					case '{':
					case '}':
						return takeBufferData(Character.toString((char) readCharacter));
					case '#':
						skipLine = true;
						return takeBufferData();
					case '\n':
					case '\t':
					case '\r':
					case ' ':
						return takeBufferData();
					default:
						buffer.append((char) readCharacter);
				}
			} else {
				switch (readCharacter) {
					case '{':
					case '}':
						return Character.toString((char) readCharacter);
					case '#':
						skipLine = true;
						break;
					case '\n':
					case '\t':
					case '\r':
					case ' ':
						break;
					default:
						buffer.append((char) readCharacter);
				}
			}
		}

		/*
		if ( buffer.length() != 0 )
			return takeBufferData();

		int ch = 0;
		while ((ch = this.read()) > 0) {
			if ( !isAsciiCharacter(ch) ) {
				buffer.append(ch);
				continue;
			}

			if ( isAsciiVisibleCharacter(ch) ) {
				if ( ch == '#' ) {
					while ((ch = this.read()) != '\n')
						if ( ch < 0 )
							break;
					continue;
				}
				else if ( ch == '{' || ch == '}' ) {
					if ( buffer.length() != 0 ) {
						return takeBufferData(Character.toString((char)ch));
					}
					else {
						return Character.toString((char)ch);
					}
				}
				else {
					buffer.append((char)ch);
				}
			}
			else {
				if ( buffer.length() != 0 )
					return takeBufferData();
				continue;
			}
		}*/

		return null;
	}

	public String getLineCode() {
		return String.format(Locale.getDefault() ,"%d:%d", lc.currentLine, lc.currentCharactor);
	}

	private String takeBufferData() {
		String result = buffer.toString();
		buffer.delete(0, buffer.length());
		return result;
	}

	private String takeBufferData(String reset) {
		String result = buffer.toString();
		buffer.delete(0, buffer.length());
		buffer.append(reset);
		return result;
	}

	private class LineCounter {
		public int currentLine = 1;
		public int currentCharactor = 0;

		public void pushLine() {
			currentLine += 1;
			currentCharactor = 0;
		}

		public void pushCharacter() {
			currentCharactor += 1;
		}
	}
}
