package com.nkyrim.itapp.ui.thesis;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.Thesis;
import com.nkyrim.itapp.ui.util.PocketKnifeIntents;
import com.nkyrim.itapp.ui.util.base.BaseActivity;
import com.nkyrim.itapp.ui.util.BusEvents;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import pocketknife.SaveState;

public class ThesisActivity extends BaseActivity {
	private ThesisDetailFragment detail;

	private boolean twoPane;
	@SaveState Thesis lastThesis;

	@Override
	protected int getLayoutResource() {
		return R.layout.thesis;
	}

	@Override
	public void onResume() {
		super.onResume();
		EventBus.getDefault().register(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		detail = (ThesisDetailFragment) getSupportFragmentManager().findFragmentById(R.id.frag_detail);
		twoPane = getResources().getBoolean(R.bool.has_two_panes);
	}

	@Override
	protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// set latest bulletin shown if we go to 2pane layout after orientation change
		if(twoPane && lastThesis != null) {
			detail.showContent(lastThesis);
		}
	}

	@SuppressWarnings("unused")
	@Subscribe
	public void onEventMainThread(BusEvents.ThesisSelectEvent event) {
		if(event.thesis == null) return;

		lastThesis = event.thesis;
		if(twoPane) detail.showContent(lastThesis);
		else {
			Intent intent = new PocketKnifeIntents(this).getThesisDetailActivity(lastThesis);
			startActivity(intent);
		}
	}

}
