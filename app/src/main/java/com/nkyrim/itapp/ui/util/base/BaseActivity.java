package com.nkyrim.itapp.ui.util.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.nkyrim.itapp.R;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import pocketknife.PocketKnife;

/**
 * Base Activity that extends AppCompatActivity and provides basic configuration
 */
public abstract class BaseActivity extends AppCompatActivity {
	protected final String TAG = getClass().getSimpleName();

	// UI elements
	protected Toolbar toolbar;

	protected abstract int getLayoutResource();

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// make back/up simply terminate current activity
		if(item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@NonNull
	@Override
	public ActionBar getSupportActionBar() {
		//noinspection ConstantConditions
		return super.getSupportActionBar();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutResource());
		PocketKnife.restoreInstanceState(this, savedInstanceState);
		PocketKnife.bindExtras(this);
		ButterKnife.bind(this);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		if(toolbar != null) {
			setSupportActionBar(toolbar);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setHomeButtonEnabled(true);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		PocketKnife.saveInstanceState(this, outState);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
	}

	// Override start and finish methods to add transition animations
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	@Override
	public void startActivityForResult(Intent intent, int code) {
		super.startActivityForResult(intent, code);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);
	}
}
