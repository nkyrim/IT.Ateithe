package com.nkyrim.itapp.ui.bulletins;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.Bulletin;
import com.nkyrim.itapp.persistance.BulletinDbHelper;
import com.nkyrim.itapp.ui.util.base.BaseFragment;
import com.nkyrim.itapp.ui.util.DividerItemDecoration;
import com.nkyrim.itapp.ui.util.TaskResult;
import com.nkyrim.itapp.util.Logger;

import java.util.ArrayList;

import butterknife.BindView;
import pocketknife.SaveState;
import tr.xip.errorview.ErrorView;

public class BulletinListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<TaskResult<ArrayList<Bulletin>>> {
	private static final int LID_BULLETINS = 0;
	long time;

	@BindView(R.id.list) RecyclerView listView;
	@BindView(R.id.container1) SwipeRefreshLayout swipeView;
	@BindView(R.id.error) ErrorView error;

	@SaveState ArrayList<Bulletin> list;
	@SaveState int filter = -1;
	private BulletinAdapter adapter;

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_bulletin_list, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_filter:
				if(list == null || list.isEmpty()) return true;

				new AlertDialog.Builder(getActivity())
						.setTitle(R.string.select_board)
						.setSingleChoiceItems(R.array.bulletin_boards, filter, (dialog, which) -> {
							filter = which;
							setListFilter();
							dialog.dismiss();
						}).show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.fragment_bulletin_list, container, false);
		bindView(view);

		// Setup components
		swipeView.setSize(SwipeRefreshLayout.LARGE);
		swipeView.setColorSchemeColors(getResources().getIntArray(R.array.loading));
		swipeView.setOnRefreshListener(this::getContent);
		listView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
		listView.setLayoutManager(new LinearLayoutManager(getActivity()));
		error.setOnRetryListener(this::getContent);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if(list == null || list.isEmpty()) {
			//Show temporary list with previously stored records
			BulletinDbHelper db = new BulletinDbHelper(getActivity());
			ArrayList<Bulletin> bl = db.getBulletins();
			showContent(bl);
			//Then get new records
			getContent();
		} else showContent(list);
	}

	public void getContent() {
		time = System.currentTimeMillis();
		showProgress(true);
		getLoaderManager().initLoader(LID_BULLETINS, null, this);
	}

	private void showContent(ArrayList<Bulletin> list) {
		adapter = new BulletinAdapter(list);
		listView.setAdapter(adapter);
		setListFilter();
	}

	private void setListFilter() {
		if(filter == -1) return;

		String filter = "";
		String[] boards = getResources().getStringArray(R.array.bulletin_boards);
		if(this.filter != 0) {
			filter = boards[this.filter];
		}

		adapter.setFilter(filter);
		listView.scrollToPosition(0);
	}

	private void showProgress(boolean show) {
		error.setVisibility(View.GONE);
		if(show) {
			swipeView.setVisibility(View.VISIBLE);
			swipeView.post(() -> {
				if(swipeView != null) swipeView.setRefreshing(true);
			});
		} else {
			swipeView.post(() -> {
				if(swipeView != null) swipeView.setRefreshing(false);
			});
		}
	}

	private void showEmpty(String msg) {
		error.setVisibility(View.VISIBLE);
		error.setSubtitle(msg);
		listView.setAdapter(new BulletinAdapter(new ArrayList<>()));
		swipeView.setVisibility(View.GONE);
	}

	@Override
	public Loader<TaskResult<ArrayList<Bulletin>>> onCreateLoader(int id, Bundle args) {
		return new BulletinListLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<TaskResult<ArrayList<Bulletin>>> loader, TaskResult<ArrayList<Bulletin>> result) {
		getLoaderManager().destroyLoader(LID_BULLETINS);
		showProgress(false);

		if(result.isSuccessful()) {
			list = result.getResult();

			if(!list.isEmpty()) {
				// Store the 20 latest bulletins
				BulletinDbHelper db = new BulletinDbHelper(getActivity());
				db.insert(list);

				showContent(list);
			} else {
				showEmpty(null);
			}
		} else {
			showEmpty(result.getMessage());
		}

		Logger.i(TAG, String.valueOf(System.currentTimeMillis() - time));
	}

	@Override
	public void onLoaderReset(Loader loader) {
		// list = null;
	}
}
