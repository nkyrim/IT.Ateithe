package com.nkyrim.itapp.ui.staff;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.Staff;
import com.nkyrim.itapp.ui.util.PocketKnifeIntents;
import com.nkyrim.itapp.ui.util.base.BaseActivity;
import com.nkyrim.itapp.ui.util.BusEvents;
import pocketknife.SaveState;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class StaffActivity extends BaseActivity {
	private StaffDetailFragment detail;

	private boolean twoPane;
	@SaveState Staff lastStaff;

	@Override
	protected int getLayoutResource() {
		return R.layout.staff;
	}

	@Override
	public void onResume() {
		super.onResume();
		EventBus.getDefault().register(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		detail = (StaffDetailFragment) getSupportFragmentManager().findFragmentById(R.id.frag_detail);
		twoPane = getResources().getBoolean(R.bool.has_two_panes);
	}

	@Override
	protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// set latest staff shown if we go from 1pane to 2pane
		if(twoPane && lastStaff != null) {
			detail.showContent(lastStaff);
		}
	}

	@SuppressWarnings("unused")
	@Subscribe
	public void onEventMainThread(BusEvents.StaffSelectEvent event) {
		if(event.staff == null) return;

		lastStaff = event.staff;
		if(twoPane) detail.showContent(lastStaff);
		else {
			Intent intent = new PocketKnifeIntents(this).getStaffDetailActivity(lastStaff);
			startActivity(intent);
		}
	}

}
