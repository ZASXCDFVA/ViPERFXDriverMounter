package me.llun.v4amounter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MountPreferenceActivity extends AppCompatActivity {
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
	}
}
