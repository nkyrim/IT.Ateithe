package com.nkyrim.itapp.ui.info;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.ui.util.base.BaseActivity;

import butterknife.BindView;

@SuppressLint("SetJavaScriptEnabled")
public class ErasmusActivity extends BaseActivity {
	@BindView(R.id.web) WebView web;

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_webview;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		web.getSettings().setJavaScriptEnabled(true);
		web.loadUrl("file:///android_asset/" + "erasmus.html");
	}
}
