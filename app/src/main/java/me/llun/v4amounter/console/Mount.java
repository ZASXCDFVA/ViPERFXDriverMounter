package me.llun.v4amounter.console;

import me.llun.v4amounter.console.core.MountProperty;
import me.llun.v4amounter.console.core.Options;
import me.llun.v4amounter.console.core.Script;
import me.llun.v4amounter.shared.GlobalProperty;
import me.llun.v4amounter.shared.StatusUtils;

public class Mount {
	public static void main(String[] args) {
		Options option = new Options(args);
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
			Umount.main(new String[]{property.mountPointMode == MountProperty.MOUNT_POINT_MODE_TMPFS ? GlobalProperty.DEFAULT_MOUNT_POINT_TMPFS : GlobalProperty.DEFAULT_MOUNT_POINT_DISK});
			e.printStackTrace();
		}
	}
}
