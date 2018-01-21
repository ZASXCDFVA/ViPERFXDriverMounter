package me.llun.v4amounter.console.core;

import java.util.LinkedList;

public class MountProperty {
	public static final int MOUNT_POINT_MODE_TMPFS = 1;
	public static final int MOUNT_POINT_MODE_DISK = 2;

	public static final int SELINUX_MODE_DISABLE = 1;
	public static final int SELINUX_MODE_PATCH_POLICY = 2;

	public int mountPointMode = MOUNT_POINT_MODE_TMPFS;
	public int selinuxMode = SELINUX_MODE_PATCH_POLICY;

	public boolean disableOtherEffects = false;
	public boolean trimUselessBlocks = false;
	public boolean patchAudioPolicy = false;

	public LinkedList<Effect> effects = new LinkedList<>();

	public MountProperty(Options opt) throws Options.OptionParseException {
		while ( opt.hasNext() ) {
			String arg = opt.nextArgument();
			switch ( arg ) {
				case "--use-disk" :
					mountPointMode = MOUNT_POINT_MODE_DISK;
					break;
				case "--use-tmpfs" :
					mountPointMode = MOUNT_POINT_MODE_TMPFS;
					break;
				case "--add-effect" :
					this.addEffect(opt.nextArgument());
					break;
				case "--disable-other-effects" :
					disableOtherEffects = true;
					break;
				case "--patch-audio-policy" :
					patchAudioPolicy = true;
					break;
				case "--trim-useless-blocks" :
					trimUselessBlocks = true;
					break;
				case "--disable-selinux" :
					selinuxMode = SELINUX_MODE_DISABLE;
					break;
				case "--patch-selinux-policy" :
					selinuxMode = SELINUX_MODE_PATCH_POLICY;
					break;
				default:
					throw new Options.OptionParseException("Invalid argument " + arg + " .");
			}
		}
	}

	private void addEffect(String argument) throws Options.OptionParseException {
		String[] effectRaw = argument.split(":");
		if ( effectRaw.length != 7 )
			throw new Options.OptionParseException("Invalid argument " + argument + " .");

		Effect effect = new Effect();

		effect.name = effectRaw[0];
		effect.library = effectRaw[1];
		effect.libraryName = effectRaw[2];
		effect.uuid = effectRaw[3];
		effect.packageName = effectRaw[4];
		effect.packagePath = effectRaw[5];
		effect.packageLibraryEntry = effectRaw[6];

		effects.add(effect);
	}

	public static class Effect {
		public String name;
		public String library;
		public String libraryName;
		public String uuid;
		public String packageName;
		public String packagePath;
		public String packageLibraryEntry;
	}
}
