package me.llun.v4amounter.console.core;

import me.llun.v4amounter.shared.GlobalProperty;

public class MountProperty {
	public String mountPoint;
	public String packagePath;
	public String libraryEntry;
	public boolean useDiskMountPoint;
	public boolean disableOtherEffects;
	public boolean trimUselessBlocks;
	public boolean patchAudioPolicy;
	public boolean disableSELinux;
	public boolean patchSELinuxPolicy;
	public MountProperty(Options opt) throws Options.OptionParseException {
		packagePath = opt.readData();
		libraryEntry = opt.readData();

		disableOtherEffects = opt.checkOption("--disable-other-effects", true, false);
		trimUselessBlocks = opt.checkOption("--trim-useless-blocks", true, false);

		patchAudioPolicy = opt.checkOption("--patch-audio-policy", true, false);
		disableSELinux = opt.checkOption("--disable-selinux", true, false);
		patchSELinuxPolicy = opt.checkOption("--patch-selinux-policy", true, false);
		useDiskMountPoint = opt.checkOption("--use-disk", true, false);

		mountPoint = useDiskMountPoint ? GlobalProperty.DEFAULT_MOUNT_POINT_DISK : GlobalProperty.DEFAULT_MOUNT_POINT_TMPFS;

		opt.finishParse();
	}
}
