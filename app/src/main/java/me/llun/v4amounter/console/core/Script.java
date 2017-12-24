package me.llun.v4amounter.console.core;

import java.io.File;
import java.io.IOException;

import me.llun.v4amounter.console.core.conf.AudioConfParser;
import me.llun.v4amounter.console.core.utils.SELinux;
import me.llun.v4amounter.console.core.utils.ShUtils;
import me.llun.v4amounter.console.core.utils.Shell;
import me.llun.v4amounter.shared.GlobalProperty;
import me.llun.v4amounter.shared.StatusUtils;

public class Script {
	public static void mount(MountProperty property) throws IOException, AudioConfParser.FormatException, InterruptedException, Shell.ShellResult.ShellException {
		boolean hasSELinux = SystemUtils.hasSELinuxSupport();
		boolean hasSystemEtcEffects = SystemUtils.hasSystemEffects();
		boolean hasSystemVendorEffects = SystemUtils.hasSystemVendorEffects();
		boolean isEnforcing = SELinux.isEnforcing();
		boolean injectSuccess = false;
		AudioEffectsPatcher effectsPatcher;
		AudioPolicyPatcher policyPatcher;

		if (hasSELinux && isEnforcing)
			SELinux.setEnforcing(false);

		StatusUtils.printStatus(StatusUtils.CHECK_EFFECTS_CONF_FILE);
		if (!(hasSystemEtcEffects || hasSystemVendorEffects))
			throw new IOException("/system/etc/audio_effects.conf and /system/vendor/etc/audio_effects.conf invaild.");

		StatusUtils.printStatus(StatusUtils.CHECK_SOUNDFX);
		if (!SystemUtils.hasSystemSoundfx())
			throw new IOException("/system/lib/soundfx invalid.");

		StatusUtils.printStatus(StatusUtils.CHECK_PACKAGE);
		if (!new File(property.packagePath).exists())
			throw new IOException(property.packagePath + " not found.");

		new File(property.mountPoint).mkdirs();

		if (!property.useDiskMountPoint)
			Shell.run("mount -t tmpfs tmpfs " + property.mountPoint);

		if (property.trimUselessBlocks) {
			if (hasSystemEtcEffects) {
				StatusUtils.printStatus(StatusUtils.PATCH_ETC_EFFECTS);
				effectsPatcher = property.disableOtherEffects ? new AudioEffectsPatcher() : new AudioEffectsPatcher("/system/etc/audio_effects.conf");
				effectsPatcher.removeEffects(GlobalProperty.DEFAULT_VIPERFX_UUID);
				effectsPatcher.putEffect("viperfx", "viperfx_library", "/system/lib/soundfx/libviperfx.so", GlobalProperty.DEFAULT_VIPERFX_UUID);
				effectsPatcher.removeRootNodes("effects", "libraries");
				effectsPatcher.write(property.mountPoint + "/audio_effects.conf.etc");
			}

			if (hasSystemVendorEffects) {
				StatusUtils.printStatus(StatusUtils.PATCH_VENDOR_EFFECTS);
				effectsPatcher = property.disableOtherEffects ? new AudioEffectsPatcher() : new AudioEffectsPatcher("/system/vendor/etc/audio_effects.conf");
				effectsPatcher.removeEffects(GlobalProperty.DEFAULT_VIPERFX_UUID);
				effectsPatcher.putEffect("viperfx", "viperfx_library", "/system/lib/soundfx/libviperfx.so", GlobalProperty.DEFAULT_VIPERFX_UUID);
				effectsPatcher.removeRootNodes("effects", "libraries");
				effectsPatcher.write(property.mountPoint + "/audio_effects.conf.vendor");
			}
		} else {
			if (hasSystemEtcEffects) {
				StatusUtils.printStatus(StatusUtils.PATCH_ETC_EFFECTS);
				effectsPatcher = property.disableOtherEffects ? new AudioEffectsPatcher() : new AudioEffectsPatcher("/system/etc/audio_effects.conf");
				effectsPatcher.removeEffects(GlobalProperty.DEFAULT_VIPERFX_UUID);
				effectsPatcher.putEffect("viperfx", "viperfx_library", "/system/lib/soundfx/libviperfx.so", GlobalProperty.DEFAULT_VIPERFX_UUID);
				effectsPatcher.write(property.mountPoint + "/audio_effects.conf.etc");
			}

			if (hasSystemVendorEffects) {
				StatusUtils.printStatus(StatusUtils.PATCH_VENDOR_EFFECTS);
				effectsPatcher = property.disableOtherEffects ? new AudioEffectsPatcher() : new AudioEffectsPatcher("/system/vendor/etc/audio_effects.conf");
				effectsPatcher.removeEffects(GlobalProperty.DEFAULT_VIPERFX_UUID);
				effectsPatcher.putEffect("viperfx", "viperfx_library", "/system/lib/soundfx/libviperfx.so", GlobalProperty.DEFAULT_VIPERFX_UUID);
				effectsPatcher.write(property.mountPoint + "/audio_effects.conf.vendor");
			}
		}

		if (property.patchAudioPolicy) {
			policyPatcher = new AudioPolicyPatcher("/system/etc/audio_policy.conf");
			policyPatcher.removeNodeIfExisted("/audio_hw_modules/primary/outputs/deep_buffer");
			policyPatcher.write(property.mountPoint + "/audio_policy.conf");
		}

		StatusUtils.printStatus(StatusUtils.COPY_ORGIN_LIBRARIES);
		if (!property.disableOtherEffects)
			ShUtils.copyDirectory("/system/lib/soundfx", property.mountPoint + "/soundfx");

		StatusUtils.printStatus(StatusUtils.EXTRACT_LIBRARY);
		new File(property.mountPoint + "/soundfx").mkdirs();
		ShUtils.unzip(property.packagePath, property.libraryEntry, property.mountPoint + "/soundfx/libviperfx.so");

		Shell.run("chcon -R u:object_r:system_file:s0 " + property.mountPoint);
		Shell.run("chown -R root:root " + property.mountPoint);
		Shell.run("chmod -R 0755 " + property.mountPoint);

		if (hasSystemEtcEffects) {
			StatusUtils.printStatus(StatusUtils.MOUNT_ETC_EFFECTS);
			Shell.run("mount -o bind " + property.mountPoint + "/audio_effects.conf.etc /system/etc/audio_effects.conf").assertResult();
		}
		if (hasSystemVendorEffects) {
			StatusUtils.printStatus(StatusUtils.MOUNT_VENDOR_EFFECTS);
			Shell.run("mount -o bind " + property.mountPoint + "/audio_effects.conf.vendor /system/vendor/etc/audio_effects.conf").assertResult();
		}
		StatusUtils.printStatus(StatusUtils.MOUNT_LIBRARIES);
		Shell.run("mount -o bind " + property.mountPoint + "/soundfx /system/lib/soundfx").assertResult();

		Shell.run("mount -o bind " + property.mountPoint + "/audio_policy.conf /system/etc/audio_policy.conf");

		if (hasSELinux && isEnforcing && property.patchSELinuxPolicy) {
			injectSuccess = SELinux.policyInject("audioserver", "audioserver_tmpfs", "file", "read", "write", "execute")
					|| SELinux.policyInject("mediaserver", "mediaserver_tmpfs", "file", "read", "write", "execute");
		}

		Shell.run("umount " + property.mountPoint);

		if (hasSELinux && !property.disableSELinux && isEnforcing && injectSuccess)
			SELinux.setEnforcing(true);

		StatusUtils.printStatus(StatusUtils.RESTART_SYSTEM_SERVERS);
		Script.stop();
		Script.start();

		StatusUtils.printStatus(StatusUtils.SUCCESS);
	}

	public static void umount(String mountPoint) throws InterruptedException, IOException {
		boolean hasSELinux = SystemUtils.hasSELinuxSupport();
		boolean isEnforcing = SELinux.isEnforcing();

		SELinux.setEnforcing(false);

		Script.stop();

		while (Shell.run("umount /system/etc/audio_effects.conf").isSuccess()) {
		}
		while (Shell.run("umount /system/vendor/etc/audio_effects.conf").isSuccess()) {
		}
		while (Shell.run("umount /system/etc/audio_policy.conf").isSuccess()) {
		}
		while (Shell.run("umount /system/lib/soundfx").isSuccess()) {
		}
		while (Shell.run("umount " + mountPoint).isSuccess()) {
		}

		Script.start();

		if (hasSELinux && isEnforcing)
			SELinux.setEnforcing(true);
	}

	public static void stop() throws InterruptedException, IOException {
		Shell.run("am force-stop " + GlobalProperty.DEFAILT_VIPERFX_PACKAGE_NAME);

		Shell.run("stop media");
		Thread.sleep(100);
		Shell.run("stop audioserver");
		Thread.sleep(100);

		SystemUtils.waitForServerStatus("audioserver", SystemUtils.SERVER_STATUS_STOPPED);
		SystemUtils.waitForServerStatus("media", SystemUtils.SERVER_STATUS_STOPPED);
	}

	public static void start() throws InterruptedException, IOException {
		Shell.run("start audioserver");
		Thread.sleep(100);
		Shell.run("start media");
		Thread.sleep(100);

		SystemUtils.waitForServerStatus("audioserver", SystemUtils.SERVER_STATUS_RUNNING);
		SystemUtils.waitForServerStatus("media", SystemUtils.SERVER_STATUS_RUNNING);
	}

	public static void kill() throws InterruptedException, IOException {
		Shell.run("kill -9 `pidof audioserver`");
		Shell.run("kill -9 `pidof mediaserver`");
	}
}
