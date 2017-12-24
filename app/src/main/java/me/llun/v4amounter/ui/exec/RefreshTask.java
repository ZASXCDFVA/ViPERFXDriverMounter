package me.llun.v4amounter.ui.exec;

import android.media.audiofx.AudioEffect;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.UUID;

import me.llun.v4amounter.shared.GlobalProperty;

public class RefreshTask {
	private RefreshTask() {
	}

	public static RefreshResult start() {
		RefreshResult result = new RefreshResult();

		try {
			BufferedReader reader = new BufferedReader(new FileReader("/proc/mounts"));
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				if (line.contains("/system/lib/soundfx"))
					result.isDriverMounted = true;
			}
			reader.close();
		} catch (Exception e) {
		}

		UUID uuid = UUID.fromString(GlobalProperty.DEFAULT_VIPERFX_UUID);
		AudioEffect.Descriptor[] effects = AudioEffect.queryEffects();
		if (effects != null) {
			for (AudioEffect.Descriptor descriptor : AudioEffect.queryEffects()) {
				if (uuid.equals(descriptor.uuid))
					result.isDriverLoaded = true;
			}
		}

		return result;
	}

	public static class RefreshResult {
		public boolean isDriverMounted = false;
		public boolean isDriverLoaded = false;
	}
}
