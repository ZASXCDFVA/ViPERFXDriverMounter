package me.llun.v4amounter.ui.exec.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class SuShell {
	private StringBuilder builder = new StringBuilder();

	public SuShell() {
	}

	public void putCommand(String command) {
		builder.append(command);
		builder.append('\n');
	}

	public LinkedList<String> execWithMountMaster() throws IOException, InterruptedException {
		Process process = new ProcessBuilder().
				command(this.findSuCommand()).
				redirectErrorStream(true).
				start();

		process.getOutputStream().write(builder.toString().getBytes());
		process.getOutputStream().write("exit\n".getBytes());
		process.getOutputStream().flush();

		LinkedList<String> result = new LinkedList<>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

		for (String line = reader.readLine(); line != null; line = reader.readLine())
			result.add(line);

		return result;
	}

	private String[] findSuCommand() {
		String[] result = new String[]{"su"};

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new ProcessBuilder().
							redirectErrorStream(true).
							command("su", "--help").
							start().
							getInputStream()));

			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				if (line.contains("--mount-master")) {
					result = new String[]{"su", "--mount-master"};
					break;
				}
			}
		} catch (IOException e) {
		}

		return result;
	}
}
