package me.llun.v4amounter.console.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import me.llun.v4amounter.console.core.patcher.AudioEffectsPatcher;
import me.llun.v4amounter.console.core.utils.SELinux;
import me.llun.v4amounter.console.core.utils.ShUtils;
import me.llun.v4amounter.console.core.utils.Shell;
import me.llun.v4amounter.shared.GlobalProperty;
import me.llun.v4amounter.shared.StatusUtils;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Script {
	public static void mount(MountProperty property) throws Exception {
		boolean hasSELinux = SystemUtils.hasSELinuxSupport();
		boolean isEnforcing = SELinux.isEnforcing();
		boolean injectSuccess = false;

		String mountPoint = property.mountPointMode == MountProperty.MOUNT_POINT_MODE_TMPFS ? GlobalProperty.DEFAULT_MOUNT_POINT_TMPFS : GlobalProperty.DEFAULT_MOUNT_POINT_DISK;

		ScriptUtils.SoundFxDirectory[] soundFxDirectories = ScriptUtils.checkSoundFxDirectory(GlobalProperty.SOUNDFX_DIRECTORIES);
		ScriptUtils.AudioConfFile[] audioEffectsConfFiles = ScriptUtils.checkPatchFiles(mountPoint ,GlobalProperty.AUDIO_EFFECTS_CONF_FILES);

		if (hasSELinux && isEnforcing)
			SELinux.setEnforcing(false);

		StatusUtils.printStatus(StatusUtils.CHECK_EFFECTS_CONF_FILE);
		if ( audioEffectsConfFiles.length < 1 )
			throw new FileNotFoundException("Audio effects configure files not found.");

		StatusUtils.printStatus(StatusUtils.CHECK_SOUNDFX);
		if ( soundFxDirectories.length < 1 )
			throw new FileNotFoundException("Audio soundfx directory not found.");

		StatusUtils.printStatus(StatusUtils.CHECK_PACKAGE);
		if ( !ScriptUtils.checkPackageExisted(property.effects) )
			throw new FileNotFoundException("Has invalid package(s).");

		new File(mountPoint).mkdirs();

		if (property.mountPointMode == MountProperty.MOUNT_POINT_MODE_TMPFS)
			Shell.run("mount -t tmpfs tmpfs " + mountPoint);

		StatusUtils.printStatus(StatusUtils.PATCH_EFFECTS_CONF);
		if (property.trimUselessBlocks) {
			for ( ScriptUtils.AudioConfFile file : audioEffectsConfFiles ) {
				AudioEffectsPatcher patcher = property.disableOtherEffects ? AudioEffectsPatcher.create(file.sourceFile) : AudioEffectsPatcher.load(file.sourceFile);
				patcher.removeRootNodes("effects" ,"libraries");
				for ( MountProperty.Effect effect : property.effects ) {
					patcher.removeEffects(effect.uuid);
					patcher.putEffect(effect.name ,effect.library ,effect.libraryName ,effect.uuid ,soundFxDirectories[0].sourcePath);
				}
				patcher.write(mountPoint + "/" + file.generatedFile);
			}
		} else {
			for ( ScriptUtils.AudioConfFile file : audioEffectsConfFiles ) {
				AudioEffectsPatcher patcher = property.disableOtherEffects ? AudioEffectsPatcher.create(file.sourceFile) : AudioEffectsPatcher.load(file.sourceFile);
				for ( MountProperty.Effect effect : property.effects ) {
					patcher.removeEffects(effect.uuid);
					patcher.putEffect(effect.name ,effect.library ,effect.libraryName,effect.uuid ,soundFxDirectories[0].sourcePath);
				}
				patcher.write(mountPoint + "/" + file.generatedFile);
			}
		}

		if (property.patchAudioPolicy) {
			AudioPolicyPatcher policyPatcher = new AudioPolicyPatcher("/system/etc/audio_policy.conf");
			policyPatcher.removeNodeIfExisted("/audio_hw_modules/primary/outputs/deep_buffer");
			policyPatcher.write(mountPoint + "/audio_policy.conf");
		}

		new File(mountPoint + "/soundfx").mkdirs();

		if (!property.disableOtherEffects) {
			StatusUtils.printStatus(StatusUtils.COPY_ORIGIN_LIBRARIES);
			for ( ScriptUtils.SoundFxDirectory directory : soundFxDirectories ) {
				new File(mountPoint + File.separator + directory.generatedPath).mkdirs();
				ShUtils.copyDirectory(directory.sourcePath ,mountPoint + File.separator + directory.generatedPath);
			}
		}

		StatusUtils.printStatus(StatusUtils.EXTRACT_LIBRARY);
		new File(mountPoint + "/soundfx").mkdirs();
		for ( MountProperty.Effect effect : property.effects ) {
			for ( ScriptUtils.SoundFxDirectory directory : soundFxDirectories ) {
				new File(mountPoint + File.separator + directory.generatedPath).mkdirs();
				ShUtils.unzip(effect.packagePath ,effect.packageLibraryEntry ,mountPoint + File.separator + directory.generatedPath + File.separator + effect.libraryName);
			}
		}

		ShUtils.touch(mountPoint + "/prevent_access_file");

		Shell.run("chcon -R u:object_r:system_file:s0 " + mountPoint);
		Shell.run("chown -R root:root " + mountPoint);
		Shell.run("chmod -R 0755 " + mountPoint);
		Shell.run("chmod 000 " + mountPoint + "/prevent_access_file");

		StatusUtils.printStatus(StatusUtils.MOUNT_EFFECTS_FILES);
		for ( ScriptUtils.AudioConfFile file : audioEffectsConfFiles ) {
			Shell.run("mount -o bind " + mountPoint + "/" + file.generatedFile + " " + file.sourceFile).assertResult();
		}

		StatusUtils.printStatus(StatusUtils.MOUNT_LIBRARIES);
		for ( ScriptUtils.SoundFxDirectory directory : soundFxDirectories ) {
			Shell.run("mount -o bind " + mountPoint + File.separator + directory.generatedPath + " " + directory.sourcePath).assertResult();
		}

		Shell.run("mount -o bind " + mountPoint + "/audio_policy.conf /system/etc/audio_policy.conf");

		if (hasSELinux && isEnforcing && property.selinuxMode == MountProperty.SELINUX_MODE_PATCH_POLICY) {
			injectSuccess = SELinux.policyInject("audioserver", "audioserver_tmpfs", "file", "read", "write", "execute")
					|| SELinux.policyInject("mediaserver", "mediaserver_tmpfs", "file", "read", "write", "execute");
		}

		Shell.run("umount " + mountPoint);

		if (hasSELinux && property.selinuxMode != MountProperty.SELINUX_MODE_DISABLE && isEnforcing && injectSuccess)
			SELinux.setEnforcing(true);

		for ( MountProperty.Effect effect : property.effects ) {
			Shell.run("am force-stop " + effect.packageName);
		}

		StatusUtils.printStatus(StatusUtils.RESTART_SYSTEM_SERVERS);
		Script.stop();
		Script.start();

		StatusUtils.printStatus(StatusUtils.SUCCESS);
	}

	@SuppressWarnings("StatementWithEmptyBody")
	public static void umount(String mountPoint) throws InterruptedException, IOException {
		boolean hasSELinux = SystemUtils.hasSELinuxSupport();
		boolean isEnforcing = SELinux.isEnforcing();

		SELinux.setEnforcing(false);

		Script.stop();

		for ( String libraries : GlobalProperty.SOUNDFX_DIRECTORIES )
			while ( Shell.run("umount " + libraries).isSuccess() ) {}

		for ( String file : GlobalProperty.AUDIO_EFFECTS_CONF_FILES)
			while ( Shell.run("umount " + file).isSuccess() ) {}

		while (Shell.run("umount /system/etc/audio_policy.conf").isSuccess()) {}
		while (Shell.run("umount " + mountPoint).isSuccess()) {}

		Script.start();

		if (hasSELinux && isEnforcing)
			SELinux.setEnforcing(true);
	}

	public static void stop() throws InterruptedException, IOException {
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
