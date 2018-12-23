package com.nkyrim.itapp.ui.mail;

import android.os.Bundle;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.mail.Email;
import com.nkyrim.itapp.ui.util.base.BaseActivity;
import com.nkyrim.itapp.ui.util.BusEvents;

import pocketknife.BindExtra;
import pocketknife.SaveState;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MailDetailActivity extends BaseActivity {
	@BindExtra @SaveState Email mail;

	@Override
	public void onResume() {
		super.onResume();
		EventBus.getDefault().register(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// If we are in two-pane layout mode, this activity is no longer necessary
		if(getResources().getBoolean(R.bool.has_two_panes)) {
			finish();
			return;
		}

		if(savedInstanceState == null) {
			if(mail != null) showContent(mail);
		}
	}

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_mail_detail;
	}

	private void showContent(Email mail) {
		MailDetailFragment md = (MailDetailFragment) getSupportFragmentManager().findFragmentById(R.id.frag_detail);
		if(md != null && md.isInLayout()) md.showContent(mail);
	}

	@SuppressWarnings("unused")
	@Subscribe
	public void onCloseActivityEvent(BusEvents.CloseActivityEvent event) {
		if(event.close) finish();
	}
}
