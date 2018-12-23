package com.nkyrim.itapp.ui.info;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ToxicBakery.viewpager.transforms.TabletTransformer;
import com.bumptech.glide.Glide;
import com.nkyrim.itapp.R;
import com.nkyrim.itapp.ui.util.base.BaseActivity;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import me.relex.circleindicator.CircleIndicator;

public class PhotoActivity extends BaseActivity {
	@BindView(R.id.pager) ViewPager pager;
	@BindView(R.id.indicator) CircleIndicator ind;
	private Timer timer;

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_photos;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create the adapter that will return a fragment for each content view
		ContentPagerAdapter pagerAdapter = new ContentPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager, attaching the adapter
		pager.setAdapter(pagerAdapter);
		pager.setPageTransformer(true, new TabletTransformer());
		ind.setViewPager(pager);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu_photos, menu);
		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(timer != null) timer.cancel();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle item selection
		switch (item.getItemId()) {
			case R.id.action_play:
				if(timer != null) {
					item.setIcon(R.drawable.ic_play);
					item.setTitle(R.string.play);
					timer.cancel();
					timer = null;
				} else {
					item.setIcon(R.drawable.ic_pause);
					item.setTitle(R.string.pause);
					timer = new Timer();
					timer.scheduleAtFixedRate(new TimerTask() {
						@Override
						public void run() {
							runOnUiThread(() -> {
								int ci = pager.getCurrentItem();
								if(ci + 1 < pager.getAdapter().getCount()) pager.setCurrentItem(ci + 1, true);
								else pager.setCurrentItem(0, true);
							});
						}
					}, 0, 3000);
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public static class ImageViewFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_imageview, container, false);
			String url = getArguments().getString("resource");
			String title = getArguments().getString("title");
			ImageView image = ((ImageView) view.findViewById(R.id.image));
			TextView tv = ((TextView) view.findViewById(R.id.tv1));
			tv.setText(title);
			Glide.with(this)
				 .load(url)
				 .into(image);

			return view;
		}
	}

	private class ContentPagerAdapter extends FragmentPagerAdapter {

		private String[] urls = getResources().getStringArray(R.array.photo_urls);
		private String[] titles = getResources().getStringArray(R.array.photo_titles);

		public ContentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return urls.length;
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = new ImageViewFragment();
			Bundle args = new Bundle();
			args.putString("resource", urls[i]);
			args.putString("title", titles[i]);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public CharSequence getPageTitle(int i) {
			return titles[i];
		}
	}
}
