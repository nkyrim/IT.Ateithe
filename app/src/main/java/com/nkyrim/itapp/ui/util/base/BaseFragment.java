package com.nkyrim.itapp.ui.util.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import pocketknife.PocketKnife;

/**
 * Base Fragment that adds basic functionality like view injection and state preservation
 */
public class BaseFragment extends Fragment {
	protected final String TAG = getClass().getSimpleName();
	protected Unbinder unbinder;

	@Override
	public void onPause() {
		super.onPause();
		if(EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PocketKnife.restoreInstanceState(this, savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		PocketKnife.saveInstanceState(this, outState);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}

	protected void bindView(View view) {
		unbinder = ButterKnife.bind(this, view);
	}
}
