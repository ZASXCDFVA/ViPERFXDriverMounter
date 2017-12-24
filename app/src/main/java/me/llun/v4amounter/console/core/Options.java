package me.llun.v4amounter.console.core;

import java.util.HashSet;
import java.util.LinkedList;

public class Options {
	private HashSet<String> options;
	private LinkedList<String> data;

	private Options() {
		options = new HashSet<>();
		data = new LinkedList<>();
	}

	public static Options parseArguments(String[] args) {
		Options result = new Options();

		for (String arg : args) {
			if (arg.startsWith("--"))
				result.options.add(arg);
			else
				result.data.add(arg);
		}

		return result;
	}

	public <T extends Object> T checkOption(String opt, T exist, T unexist) {
		if (options.remove(opt))
			return exist;
		return unexist;
	}

	public String readData() throws OptionParseException {
		String result = data.pollFirst();
		if (result == null)
			throw new OptionParseException("Need more arguments.");
		return result;
	}

	public void finishParse() throws OptionParseException {
		if (options.isEmpty() && data.isEmpty())
			return;
		throw new OptionParseException("Too more argument." + "options_length=" + options.size() + " datas_length=" + data.size());
	}

	public static class OptionParseException extends Exception {
		public OptionParseException(String s) {
			super(s);
		}
	}
}
