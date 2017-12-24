package me.llun.v4amounter.console;

import me.llun.v4amounter.console.core.Script;

public class Restart {
	public static void main(String[] args) {
		try {
			Script.kill();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
