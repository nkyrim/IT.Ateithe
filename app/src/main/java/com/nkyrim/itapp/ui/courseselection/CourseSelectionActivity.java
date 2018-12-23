package com.nkyrim.itapp.ui.courseselection;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.courseselection.CourseSelection;
import com.nkyrim.itapp.settings.Settings;
import com.nkyrim.itapp.ui.util.base.BaseActivity;
import com.nkyrim.itapp.util.Util;
import pocketknife.SaveState;

import butterknife.BindView;
import pocketknife.SaveState;

public class CourseSelectionActivity extends BaseActivity {
	@BindView(R.id.tabs) TabLayout tabs;
	@BindView(R.id.pager) ViewPager pager;

	@SaveState CourseSelection selection;

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_viewpager;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_course_selection, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.action_save) {
			boolean success = Settings.storeSelectedCourses(selection);
			if(success) Util.showToast(this, R.string.save_success);
			else Util.showToast(this, R.string.save_fail);
			return true;
		} else if(item.getItemId() == R.id.action_restore) {
			selection = new CourseSelection();
			pager.setAdapter(new ContentPagerAdapter(getSupportFragmentManager(), selection));
			tabs.setupWithViewPager(pager);
			Util.showToast(this, R.string.restore);
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(savedInstanceState == null) selection = Settings.getSelectedCourses();
		pager.setAdapter(new ContentPagerAdapter(getSupportFragmentManager(), selection));
		tabs.setupWithViewPager(pager);
	}

	private static class ContentPagerAdapter extends FragmentStatePagerAdapter {
		private String[] semesters;
		private CourseSelection selection;

		public ContentPagerAdapter(FragmentManager fm, CourseSelection selection) {
			super(fm);
			semesters = Util.getStringArray(R.array.semesters);
			this.selection = selection;
		}

		@Override
		public int getCount() {
			return 7;
		}

		@Override
		public Fragment getItem(int i) {
			return CourseSelectionFragment.newInstance(i, selection);
		}

		@Override
		public CharSequence getPageTitle(int i) {
			return semesters[i];
		}
	}
}
