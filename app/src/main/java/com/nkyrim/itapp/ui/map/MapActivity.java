package com.nkyrim.itapp.ui.map;

import android.view.View;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.ui.util.base.BaseActivity;
import com.nkyrim.itapp.ui.util.BusEvents;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MapActivity extends BaseActivity {

	@Override
	protected void onResume() {
		super.onResume();
		EventBus.getDefault().register(this);
	}

	@Override
	protected int getLayoutResource() {
		return R.layout.map;
	}

	@Subscribe
	public void onEventMainThread(BusEvents.ShowDirectionsEvent event) {
		MapDirectionsFragment md = (MapDirectionsFragment) getSupportFragmentManager().findFragmentById(R.id.frag_directions);

		if(md != null && md.isInLayout()) {
			View fragDirCon = findViewById(R.id.container_frag_dir);
			fragDirCon.setVisibility(View.VISIBLE);
			md.showContent(event.route.getDirections());
		}
	}

}
