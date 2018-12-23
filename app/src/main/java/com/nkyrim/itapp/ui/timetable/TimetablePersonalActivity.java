package com.nkyrim.itapp.ui.timetable;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.courseselection.CourseSelection;
import com.nkyrim.itapp.domain.timetable.Timetable;
import com.nkyrim.itapp.settings.Settings;
import com.nkyrim.itapp.ui.util.base.BaseActivity;
import com.nkyrim.itapp.ui.util.TaskResult;

import butterknife.BindView;
import pocketknife.SaveState;
import tr.xip.errorview.ErrorView;

public class TimetablePersonalActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<TaskResult<Timetable>> {
	private static final int LID_PTT = 0;

	@BindView(R.id.error) ErrorView error;
	@BindView(R.id.prg) View prog;
	@BindView(R.id.pager) ViewPager content;
	@BindView(R.id.tabs) TabLayout tabs;
	@BindView(R.id.tvSem) TextView sem;

	@SaveState CourseSelection sel;
	@SaveState Timetable timetable;

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_timetable_personal;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_timetable, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if(id == R.id.action_refresh) {
			getContent();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		error.setOnRetryListener(this::getContent);

		if(savedInstanceState == null) {
			sel = Settings.getSelectedCourses();
			timetable = Settings.getTimetable();
		}
		if(timetable == null) getContent();
		else showContent(timetable);
	}

	private void getContent() {
		showProgress(true);
		if(sel.isEmpty()) {
			showEmpty(getString(R.string.error_no_courses));
			return;
		}

		// get timetable
		getSupportLoaderManager().initLoader(LID_PTT, null, this);
	}

	private void showContent(Timetable timetable) {
		if(timetable == null) {
			showEmpty(null);
		} else {
			sem.setText(timetable.getAcademicSemester());
			ContentPagerAdapter adapter = new ContentPagerAdapter(getSupportFragmentManager());
			content.setAdapter(adapter);
			tabs.setupWithViewPager(content);
			showProgress(false);
		}
	}

	private void showProgress(boolean show) {
		error.setVisibility(View.GONE);
		prog.setVisibility(show ? View.VISIBLE : View.GONE);
		content.setVisibility(show ? View.GONE : View.VISIBLE);
	}

	private void showEmpty(String msg) {
		error.setVisibility(View.VISIBLE);
		error.setSubtitle(msg);
		prog.setVisibility(View.GONE);
		content.setVisibility(View.GONE);
	}

	private class ContentPagerAdapter extends FragmentStatePagerAdapter {
		private String[] titles = getResources().getStringArray(R.array.days);

		public ContentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public int getCount() {
			return titles.length;
		}

		@Override
		public Fragment getItem(int i) {
			return TimetablePersonalFragment.newInstance(timetable, i, sel);
		}

		@Override
		public CharSequence getPageTitle(int i) {
			return titles[i];
		}
	}

	@Override
	public Loader<TaskResult<Timetable>> onCreateLoader(int id, Bundle args) {
		return new TimetableLoader(TimetablePersonalActivity.this);
	}

	@Override
	public void onLoadFinished(Loader<TaskResult<Timetable>> loader, TaskResult<Timetable> data) {
		getSupportLoaderManager().destroyLoader(LID_PTT);
		if(data.isSuccessful()){
			timetable = data.getResult();
			Settings.storeTimetable(timetable);
			showContent(timetable);
		}
		else showEmpty(data.getMessage());
	}

	@Override
	public void onLoaderReset(Loader<TaskResult<Timetable>> loader) {
		timetable = null;
	}
}
