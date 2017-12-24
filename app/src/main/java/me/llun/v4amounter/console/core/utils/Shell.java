package me.llun.v4amounter.console.core.utils;

import java.io.IOException;
import java.io.InputStream;

public class Shell {
	public static ShellResult run(String command) throws IOException, InterruptedException {
		Process process = new ProcessBuilder()
				.command("sh", "-c", command)
				.redirectErrorStream(true)
				.start();

		return new ShellResult(command, process);
	}

	public static class ShellResult {
		private String command;
		private int resultCode;
		private String output;

		public ShellResult(String command, Process process) throws InterruptedException {
			this.command = command;

			StringBuilder builder = new StringBuilder();
			InputStream inputStream = process.getInputStream();

			try {
				for (int ch = inputStream.read(); ch > 0; ch = inputStream.read()) {
					if (ch == '\n')
						builder.append(" ");
					else
						builder.append((char) ch);
				}
			} catch (IOException e) {
			}

			this.output = builder.toString();
			this.resultCode = process.waitFor();
		}

		public int getResultCode() {
			return resultCode;
		}

		public String getCommand() {
			return command;
		}

		public String getOutput() {
			return output;
		}

		public void assertResult() throws ShellException {
			if (resultCode != 0)
				throw new ShellException(command, resultCode, output);
		}

		public boolean isSuccess() {
			return resultCode == 0;
		}

		public static class ShellException extends Exception {
			public ShellException(String command, int code, String output) {
				super("Run \"" + command + "\" Result = " + code + " Output = " + output);
			}
		}
	}
}
