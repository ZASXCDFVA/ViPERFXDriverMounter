package me.llun.v4amounter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import me.llun.v4amounter.shared.StatusUtils;
import me.llun.v4amounter.ui.TaskRunner;

public class MountActivity extends AppCompatActivity implements TaskRunner.Callback {
	private TextView mountStatus;
	private TextView driverStatus;
	private Button applyButton;
	private ProgressBar progressBar;
	private boolean lastMountedStatus = false;
	private String lastOutput = "";
	private final DialogInterface.OnClickListener dialogCopyLogClickedListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int p2) {
			ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			if (clipboard != null) {
				clipboard.setPrimaryClip(ClipData.newPlainText("viperfx_mounter", lastOutput));
				Toast.makeText(MountActivity.this, R.string.log_copied, Toast.LENGTH_LONG).show();
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		applyButton = findViewById(R.id.apply);
		progressBar = findViewById(R.id.progress_bar);
		mountStatus = findViewById(R.id.mount_status);
		driverStatus = findViewById(R.id.driver_status);
	}

	@Override
	protected void onStart() {
		super.onStart();

		applyButton.setEnabled(false);
		progressBar.setVisibility(View.VISIBLE);

		new TaskRunner(this, this, TaskRunner.REFRESH).start();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onTaskFinished(final int request, final int errorCode, String output, final boolean isDriverMounted, final boolean isFriverLoaded) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				applyButton.setEnabled(true);
				progressBar.setVisibility(View.INVISIBLE);

				mountStatus.setText(isDriverMounted ? R.string.mounted : R.string.unmounted);
				driverStatus.setText(isFriverLoaded ? R.string.loaded : R.string.unloaded);

				applyButton.setText(isDriverMounted ? R.string.umount_and_restart_media : R.string.mount_and_restart_media);

				if (request == TaskRunner.MOUNT && errorCode != StatusUtils.SUCCESS) {
					new AlertDialog.Builder(MountActivity.this).
							setCancelable(false).
							setTitle(R.string.error_raise).
							setMessage(StatusUtils.getErrorMessageResource(errorCode)).
							setNegativeButton(R.string.ojbk, null).
							setNeutralButton(R.string.copy_log, dialogCopyLogClickedListener).
							create().show();
				}
			}
		});

		this.lastMountedStatus = isDriverMounted;
		this.lastOutput = output;
	}

	public void onApplyButtonClicked(View v) {
		applyButton.setEnabled(false);
		progressBar.setVisibility(View.VISIBLE);

		if (lastMountedStatus)
			new TaskRunner(this, this, TaskRunner.UMOUNT).start();
		else
			new TaskRunner(this, this, TaskRunner.MOUNT).start();
	}

	public void onSettingButtonClicked(View v) {
		Intent intent = new Intent(this, MountPreferenceActivity.class);

		startActivity(intent);
	}
}
