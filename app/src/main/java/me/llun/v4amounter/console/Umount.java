package me.llun.v4amounter.console;

import me.llun.v4amounter.console.core.Script;

public class Umount {
	public static void main(String[] args) {
		try {
			Script.umount(args[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
