package me.llun.v4amounter.console;

import me.llun.v4amounter.console.core.MountProperty;
import me.llun.v4amounter.console.core.Options;
import me.llun.v4amounter.console.core.Script;
import me.llun.v4amounter.shared.StatusUtils;

public class Mount {
	public static void main(String[] args) {
		Options option = Options.parseArguments(args);
		MountProperty property = null;

		try {
			property = new MountProperty(option);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		try {
			StatusUtils.printStatus(StatusUtils.STARTED);
			Script.mount(property);
		} catch (Exception e) {
			Umount.main(new String[]{property.mountPoint});
			e.printStackTrace();
		}
	}
}
