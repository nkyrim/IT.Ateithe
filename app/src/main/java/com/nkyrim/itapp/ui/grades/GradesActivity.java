package com.nkyrim.itapp.ui.grades;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.student.GradesTotal;
import com.nkyrim.itapp.ui.util.base.BaseActivity;
import com.nkyrim.itapp.ui.util.TaskResult;
import com.nkyrim.itapp.util.Util;
import pocketknife.SaveState;

import butterknife.BindView;
import tr.xip.errorview.ErrorView;

public class GradesActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<TaskResult<GradesTotal>> {
	private final static int LID_GRADES = 0;

	@BindView(R.id.error) ErrorView error;
	@BindView(R.id.prg) View prog;
	@BindView(R.id.pager) ViewPager pager;
	@BindView(R.id.tabs) TabLayout tabs;

	@SaveState GradesTotal grades;

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_grades;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		error.setOnRetryListener(this::getContent);

		if(grades == null) getContent();
		else showContent(grades);
	}

	public void getContent() {
		showProgress(true);
		getSupportLoaderManager().initLoader(LID_GRADES, null, this);
	}

	private void showProgress(boolean show) {
		error.setVisibility(View.GONE);
		prog.setVisibility(show ? View.VISIBLE : View.GONE);
		pager.setVisibility(show ? View.GONE : View.VISIBLE);
	}

	private void showEmpty(String msg) {
		error.setVisibility(View.VISIBLE);
		error.setSubtitle(msg);
		prog.setVisibility(View.GONE);
		pager.setVisibility(View.GONE);
	}

	private void showContent(GradesTotal result) {
		grades = result;

		if(grades == null || grades.isEmpty()) {
			showEmpty("");
		} else {
			showProgress(false);
			// Create the adapter that will return a fragment for each content view
			ContentPagerAdapter pagerAdapter = new ContentPagerAdapter(getSupportFragmentManager());

			// Set up the ViewPager, attaching the adapter
			pager.setAdapter(pagerAdapter);
			tabs.setupWithViewPager(pager);
		}
	}

	@Override
	public Loader<TaskResult<GradesTotal>> onCreateLoader(int id, Bundle args) {
		return new GradeLoader(GradesActivity.this);
	}

	@Override
	public void onLoadFinished(Loader<TaskResult<GradesTotal>> loader, TaskResult<GradesTotal> result) {
		getSupportLoaderManager().destroyLoader(LID_GRADES);

		if(result.isSuccessful()) showContent(result.getResult());
		else showEmpty(result.getMessage());
	}

	@Override
	public void onLoaderReset(Loader<TaskResult<GradesTotal>> loader) {
		grades = null;
	}

	private class ContentPagerAdapter extends FragmentStatePagerAdapter {
		private String[] semesters;

		public ContentPagerAdapter(FragmentManager fm) {
			super(fm);
			semesters = Util.getStringArray(R.array.semesters);
		}

		@Override
		public int getCount() {
			return grades.size() + 1;
		}

		@Override
		public Fragment getItem(int i) {
			if(i == 0) return GradesMainFragment.newInstance(grades.getInfo());
			return GradesFragment.newInstance(grades.get(i - 1));
		}

		@Override
		public CharSequence getPageTitle(int i) {
			if(i == 0) return getString(R.string.home_grade);
			return semesters[i - 1];
		}
	}
}
