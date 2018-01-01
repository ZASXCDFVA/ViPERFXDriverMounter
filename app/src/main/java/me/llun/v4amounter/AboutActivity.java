package me.llun.v4amounter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

/**
 * Created by null on 17-12-31.
 * About Activity
 */

public class AboutActivity extends AppCompatActivity {
	public static final int VERSION = 2;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		WebView webView = new WebView(this);
		webView.loadUrl("file:///android_asset/about.html");

		setContentView(webView);
	}
}
