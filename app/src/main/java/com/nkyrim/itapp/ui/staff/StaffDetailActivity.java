package com.nkyrim.itapp.ui.staff;

import android.os.Bundle;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.Staff;
import com.nkyrim.itapp.ui.util.base.BaseActivity;

import pocketknife.BindExtra;

public class StaffDetailActivity extends BaseActivity {
	@BindExtra Staff staff;

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_staff_detail;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// If we are in two-pane layout mode, this activity is no longer necessary
		if(getResources().getBoolean(R.bool.has_two_panes)) {
			finish();
			return;
		}

		// send the staff member to show only on the first time the activity runs
		if(savedInstanceState == null) {
			StaffDetailFragment detail = (StaffDetailFragment) getSupportFragmentManager().findFragmentById(R.id.frag_detail);
			if(staff != null) detail.showContent(staff);
		}
	}

}
