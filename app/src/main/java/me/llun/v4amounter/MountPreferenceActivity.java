package me.llun.v4amounter;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.ArraySet;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import me.llun.v4amounter.shared.GlobalProperty;

public class MountPreferenceActivity extends AppCompatActivity {
	public static final int VERSION = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getFragmentManager().beginTransaction().
				replace(android.R.id.content, new MountPreferenceFragment()).
				commit();
	}

	public static class MountPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			addPreferencesFromResource(R.xml.mount_option);

			PreferenceManager.getDefaultSharedPreferences(this.getActivity()).registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onStart() {
			super.onStart();

			updateInstalledVersion();
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
			switch (key) {
				case "disable_other_effects":
					if (sp.getBoolean(key, false))
						Toast.makeText(this.getActivity(), getString(R.string.disable_other_effects_tips), Toast.LENGTH_LONG).show();
					break;
				case "trim_useless_blocks":
					if (sp.getBoolean(key, false))
						Toast.makeText(getActivity(), getString(R.string.trim_useless_block_tips), Toast.LENGTH_LONG).show();
					break;
			}
		}

		private void updateInstalledVersion() {
			MultiSelectListPreference preference = (MultiSelectListPreference) getPreferenceManager().findPreference("mount_version");
			SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();

			boolean viperFxInstalled = checkPackageInstalled(GlobalProperty.VIPERFX_PACKAGE_NAME);
			boolean viper4androidFxInstalled = checkPackageInstalled(GlobalProperty.V4A_FX_PACKAGE_NAME);
			boolean viper4androidXHiFiInstalled = checkPackageInstalled(GlobalProperty.V4A_XHIFI_PACKAGE_NAME);

			ArrayList<String> versions = new ArrayList<>();
			TreeSet<String> values = new TreeSet<>();
			TreeSet<String> availableVersion = new TreeSet<>();
			Set<String> selectedValues = sharedPreferences.getStringSet("mount_version" ,new TreeSet<String>());

			if ( viperFxInstalled ) {
				versions.add(getString(R.string.viper_fx));
				values.add("1");
			}
			if ( viper4androidFxInstalled ) {
				versions.add(getString(R.string.viper4android_fx));
				values.add("2");
			}
			if ( viper4androidXHiFiInstalled ) {
				versions.add(getString(R.string.viper4android_xhifi));
				values.add("3");
			}

			assert preference != null;
			preference.setEntries(versions.toArray(new String[0]));
			preference.setEntryValues(values.toArray(new String[0]));

			availableVersion.addAll(values);
			availableVersion.retainAll(selectedValues);

			if ( values.size() == 0 )
				Toast.makeText(getActivity() ,R.string.not_installed_any_viper_effects ,Toast.LENGTH_LONG).show();
			else if ( availableVersion.size() == 0 )
				availableVersion.add(values.first());

			sharedPreferences.edit().putStringSet("mount_version" ,availableVersion).apply();
		}

		private boolean checkPackageInstalled(String pack) {
			PackageManager packageManager = getActivity().getPackageManager();

			try {
				packageManager.getPackageInfo(pack ,0);
				return true;
			} catch (PackageManager.NameNotFoundException e) {
				return false;
			}
		}
	}
}
