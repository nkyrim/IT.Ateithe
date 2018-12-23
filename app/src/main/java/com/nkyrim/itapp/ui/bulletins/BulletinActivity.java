package com.nkyrim.itapp.ui.bulletins;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.nkyrim.itapp.ItNet;
import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.Bulletin;
import com.nkyrim.itapp.ui.other.LoginActivity;
import com.nkyrim.itapp.ui.util.PocketKnifeIntents;
import com.nkyrim.itapp.ui.util.base.BaseActivity;
import com.nkyrim.itapp.ui.util.BusEvents;
import com.nkyrim.itapp.util.Cons;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import pocketknife.SaveState;

public class BulletinActivity extends BaseActivity {
	private static final int LOGIN_CODE = 0;
	private BulletinDetailFragment detail;
	private BulletinListFragment master;

	@SaveState Bulletin lastBulletin;
	private boolean twoPane;

	@Override
	protected int getLayoutResource() {
		return R.layout.bulletins;
	}

	@Override
	public void onResume() {
		super.onResume();
		EventBus.getDefault().register(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		detail = (BulletinDetailFragment) getSupportFragmentManager().findFragmentById(R.id.frag_detail);
		master = (BulletinListFragment) getSupportFragmentManager().findFragmentById(R.id.frag_master);
		twoPane = getResources().getBoolean(R.bool.has_two_panes);

		// check if account stored else start login activity
		if(!ItNet.isAccountStored(Cons.ACCOUNT_OPTION_HYDRA) && savedInstanceState == null) {
			Intent i = new PocketKnifeIntents(this).getLoginActivity(Cons.ACCOUNT_OPTION_HYDRA);
			startActivityForResult(i, LOGIN_CODE);
		}
	}

	@Override
	protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
		// set latest bulletin shown if we go to 2pane layout after orientation change
		// we do this after onCreate so the fragments will have been recreated
		if(twoPane && lastBulletin != null) detail.showContent(lastBulletin);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != LoginActivity.LOGIN_SUCCESS) finish();
		else master.getContent();
	}

	@Subscribe
	public void onEventMainThread(BusEvents.BulletinSelectEvent event) {
		if(event.bulletin == null) return;

		lastBulletin = event.bulletin;
		if(twoPane) detail.showContent(lastBulletin);
		else {
			Intent i = new PocketKnifeIntents(this).getBulletinDetailActivity(lastBulletin);
			startActivity(i);
		}
	}
}
