package com.nkyrim.itapp.ui.thesis;

import android.os.Bundle;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.Thesis;
import com.nkyrim.itapp.ui.util.base.BaseActivity;

import pocketknife.BindExtra;

public class ThesisDetailActivity extends BaseActivity {
	private ThesisDetailFragment detail;

	@BindExtra Thesis thesis;

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_thesis_detail;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// If we are in two-pane layout mode, this activity is no longer necessary
		if(getResources().getBoolean(R.bool.has_two_panes)) {
			finish();
			return;
		}

		detail = (ThesisDetailFragment) getSupportFragmentManager().findFragmentById(R.id.frag_detail);

		if(savedInstanceState == null) {
			if(thesis != null) show(thesis);
		}
	}

	private void show(Thesis t) {
		detail.showContent(t);
	}
}
