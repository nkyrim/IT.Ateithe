package com.nkyrim.itapp.ui.staff;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.Staff;
import com.nkyrim.itapp.ui.util.base.BaseFragment;
import com.nkyrim.itapp.ui.util.DividerItemDecoration;
import com.nkyrim.itapp.ui.util.TaskResult;
import pocketknife.SaveState;

import java.util.ArrayList;

import butterknife.BindView;
import tr.xip.errorview.ErrorView;

public class StaffListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<TaskResult<ArrayList<Staff>>> {
	private static final int LID_STAFF_LIST = 0;

	@BindView(R.id.list) RecyclerView listView;
	@BindView(R.id.container1) SwipeRefreshLayout swipeView;
	@BindView(R.id.error) ErrorView error;

	@SaveState ArrayList<Staff> list;
	private StaffAdapter adapter;

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_staff_list, menu);
		final MenuItem searchItem = menu.findItem(R.id.action_search);
		final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		searchView.setQueryHint(getString(R.string.search));

		// Set query listeners
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextChange(String query) {
				if(adapter != null) adapter.setFilter(query);
				return true;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				if(adapter != null) adapter.setFilter(query);
				searchView.clearFocus();
				return true;
			}
		});

		super.onCreateOptionsMenu(menu, inflater);
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
		listView.setLayoutManager(new LinearLayoutManager(getActivity()));
		listView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
		error.setOnRetryListener(this::getContent);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if(list == null || list.isEmpty()) getContent();
		else showContent();
	}

	private void getContent() {
		showProgress(true);
		getLoaderManager().initLoader(LID_STAFF_LIST, null, this);
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
		if(msg != null) error.setSubtitle(msg);
		else error.setSubtitle("");
		swipeView.setVisibility(View.GONE);
	}

	@Override
	public Loader<TaskResult<ArrayList<Staff>>> onCreateLoader(int id, Bundle args) {
		return new StaffListLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<TaskResult<ArrayList<Staff>>> loader, TaskResult<ArrayList<Staff>> result) {
		getLoaderManager().destroyLoader(LID_STAFF_LIST);
		showProgress(false);

		if(result.isSuccessful()) {
			list = result.getResult();

			if(list == null || list.isEmpty()) showEmpty(null);
			else showContent();
		} else {
			showEmpty(result.getMessage());
		}
	}

	private void showContent() {
		adapter = new StaffAdapter(list);
		listView.setAdapter(adapter);
	}

	@Override
	public void onLoaderReset(Loader loader) {
		list = null;
	}

}
