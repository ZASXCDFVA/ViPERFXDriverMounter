package me.llun.v4amounter.console.core;

public class Options {
	private String[] arguments;
	private int currentPosition = 0;

	public Options(String[] args) {
		arguments = args;
	}

	public String nextArgument() throws OptionParseException {
		if ( arguments.length == currentPosition )
			throw new OptionParseException("Invalid argument.");

		return arguments[currentPosition++];
	}

	public boolean hasNext() {
		return arguments.length != currentPosition;
	}

	public static class OptionParseException extends Exception {
		public OptionParseException(String s) {
			super(s);
		}
	}
}
