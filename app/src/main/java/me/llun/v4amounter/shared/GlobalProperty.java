package me.llun.v4amounter.shared;

public class GlobalProperty {
	public static final String DEFAULT_MOUNT_POINT_TMPFS = "/data/local/v4a_mounter/tmpfs";
	public static final String DEFAULT_MOUNT_POINT_DISK = "/data/local/v4a_mounter/disk";

	public static final String[] AUDIO_EFFECTS_CONF_FILES = new String[] {
			"/system/etc/audio_effects.conf" ,
			"/system/etc/audio_effects.xml" ,
			"/system/vendor/etc/audio_effects.conf" ,
			"/system/vendor/etc/audio_effects.xml"
	};

	public static final String[] SOUNDFX_DIRECTORIES = new String[] {
			"/system/lib/soundfx" ,
			"/system/vendor/lib/soundfx"
	};

	public static final String VIPERFX_PACKAGE_NAME = "com.audlabs.viperfx";
	public static final String VIPERFX_UUID = "41d3c987-e6cf-11e3-a88a-11aba5d5c51b";

	public static final String V4A_FX_PACKAGE_NAME = "com.vipercn.viper4android_v2";
	public static final String V4A_FX_UUID = "41d3c987-e6cf-11e3-a88a-11aba5d5c51b";

	public static final String V4A_XHIFI_PACKAGE_NAME = "com.vipercn.viper4android.xhifi";
	public static final String V4A_XHIFI_UUID = "d92c3a90-3e26-11e2-a25f-0800200c9a66";
}
