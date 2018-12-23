package com.nkyrim.itapp.ui.info;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.ui.util.base.BaseActivity;
import com.nkyrim.itapp.ui.util.AssetWebViewFragment;

import butterknife.BindView;

public class SupportActivity extends BaseActivity {
	@BindView(R.id.pager) ViewPager pager;
	@BindView(R.id.tabs) TabLayout tabs;

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_viewpager;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create the adapter that will return a fragment for each content view
		ContentPagerAdapter pagerAdapter = new ContentPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager, attaching the adapter
		pager.setAdapter(pagerAdapter);
		tabs.setupWithViewPager(pager);
	}

	private class ContentPagerAdapter extends FragmentPagerAdapter {
		private String[] filenames = getResources().getStringArray(R.array.support_filenames);
		private String[] titles = getResources().getStringArray(R.array.support_titles);

		public ContentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public Fragment getItem(int i) {
			return AssetWebViewFragment.newInstance(filenames[i]);
		}

		@Override
		public CharSequence getPageTitle(int i) {
			return titles[i];
		}
	}
}
