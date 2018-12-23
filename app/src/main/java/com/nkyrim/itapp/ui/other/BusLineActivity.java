package com.nkyrim.itapp.ui.other;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.ui.util.base.BaseActivity;
import com.nkyrim.itapp.util.Cons;

import butterknife.BindView;

/**
 * Bus line Activity, override onKey to allow going back in the WebView and prevent it from finishing the activity
 */
public class BusLineActivity extends BaseActivity {
	// UI elements
	@BindView(R.id.web) WebView web;
	@BindView(R.id.prg) ProgressBar prgBar;

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_bus_line;
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		web.getSettings().setJavaScriptEnabled(true);
		WebViewClient c = new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				prgBar.setVisibility(View.GONE);
			}
		};
		web.setWebViewClient(c);
		web.loadUrl(Cons.URLS.OASTH);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getAction() == KeyEvent.ACTION_DOWN) {
			if(keyCode == KeyEvent.KEYCODE_BACK) {
				if(web.canGoBack()) web.goBack();
				else finish();
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
