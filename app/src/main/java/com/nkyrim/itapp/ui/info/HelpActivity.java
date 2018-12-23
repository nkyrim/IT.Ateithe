package com.nkyrim.itapp.ui.info;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.ui.util.base.BaseActivity;

import butterknife.BindView;
import pocketknife.BindExtra;

@SuppressLint("SetJavaScriptEnabled")
public class HelpActivity extends BaseActivity {
	public final static String ARG_ANCHOR = "ARG_ANCHOR";
	@BindExtra(ARG_ANCHOR) String anchor;

	@BindView(R.id.web) WebView web;

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_webview;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String arg = getIntent().getExtras() != null ? anchor : null;
		String anchor = arg == null ? "" : "#" + arg;

		web.getSettings().setJavaScriptEnabled(true);
		web.loadUrl("file:///android_asset/" + "help.html" + anchor);
	}
}
