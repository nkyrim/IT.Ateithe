package com.nkyrim.itapp.ui.bulletins;

import android.os.Bundle;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.Bulletin;
import com.nkyrim.itapp.ui.util.base.BaseActivity;

import pocketknife.BindExtra;

public class BulletinDetailActivity extends BaseActivity {
	@BindExtra Bulletin bulletin;

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_bulletin_detail;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// If we are in two-pane layout mode, this activity is no longer necessary
		if(getResources().getBoolean(R.bool.has_two_panes)) {
			finish();
			return;
		}

		// if first time, fragment will take from there
		if(savedInstanceState == null) {
			BulletinDetailFragment detail = (BulletinDetailFragment) getSupportFragmentManager().findFragmentById(R.id.frag_detail);
			if(bulletin != null) detail.showContent(bulletin);
		}
	}
}
