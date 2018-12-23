package com.nkyrim.itapp.ui.timetable;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ToxicBakery.viewpager.transforms.AccordionTransformer;
import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.timetable.Timetable;
import com.nkyrim.itapp.settings.Settings;
import com.nkyrim.itapp.ui.util.base.BaseActivity;
import com.nkyrim.itapp.ui.util.TaskResult;
import pocketknife.SaveState;

import butterknife.BindView;
import tr.xip.errorview.ErrorView;

public class TimetableActivity extends BaseActivity {
	private final static int LID_TT = 0;

	@BindView(R.id.error) ErrorView error;
	@BindView(R.id.prg) View prog;
	@BindView(R.id.pager) ViewPager pager;
	@BindView(R.id.tabs) TabLayout tabs;

	@SaveState int selected;
	@SaveState Timetable timetable;
	private ContentPagerAdapter adapter;

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_timetable;
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

		if(savedInstanceState == null) timetable = Settings.getTimetable();
		if(timetable == null) getContent();
		else showContent(timetable);
	}

	private void getContent() {
		showProgress(true);
		getSupportLoaderManager().initLoader(LID_TT, null, new TTloaderCallbacks());
	}

	public void showSemesters(View v) {
		if(timetable == null) return;
		new AlertDialog.Builder(this)
				.setTitle(R.string.select_semester)
				.setSingleChoiceItems(R.array.semesters, selected, (dialog, which) -> {
					selected = which;
					adapter.notifyDataSetChanged();
					dialog.dismiss();
				}).show();
	}

	private void showContent(Timetable timetable) {
		if(timetable == null) {
			showEmpty(null);
		} else {
			getSupportActionBar().setSubtitle(timetable.getAcademicSemester());
			adapter = new ContentPagerAdapter(getSupportFragmentManager());
			pager.setAdapter(adapter);
			tabs.setupWithViewPager(pager);
			pager.setPageTransformer(true, new AccordionTransformer());
			showProgress(false);
		}
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

	private class ContentPagerAdapter extends FragmentStatePagerAdapter {
		private String[] days = getResources().getStringArray(R.array.days);

		public ContentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public int getCount() {
			return days.length;
		}

		@Override
		public Fragment getItem(int i) {
			return TimetableFragment.newInstance(timetable.getSemesters().get(selected).get(i));
		}

		@Override
		public CharSequence getPageTitle(int i) {
			return days[i];
		}
	}

	private class TTloaderCallbacks implements LoaderManager.LoaderCallbacks<TaskResult<Timetable>> {

		@Override
		public Loader<TaskResult<Timetable>> onCreateLoader(int id, Bundle args) {
			return new TimetableLoader(TimetableActivity.this);
		}

		@Override
		public void onLoadFinished(Loader<TaskResult<Timetable>> loader, TaskResult<Timetable> data) {
			getSupportLoaderManager().destroyLoader(LID_TT);
			if(data.isSuccessful()) {
				timetable = data.getResult();
				Settings.storeTimetable(timetable);
				showContent(timetable);
			} else showEmpty(data.getMessage());
		}

		@Override
		public void onLoaderReset(Loader<TaskResult<Timetable>> loader) {
			timetable = null;
		}
	}

}
