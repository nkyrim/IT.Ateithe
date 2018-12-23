package com.nkyrim.itapp.ui.thesis;

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
import com.nkyrim.itapp.domain.Thesis;
import com.nkyrim.itapp.ui.util.base.BaseFragment;
import com.nkyrim.itapp.ui.util.DividerItemDecoration;
import com.nkyrim.itapp.ui.util.TaskResult;
import pocketknife.SaveState;

import java.util.ArrayList;

import butterknife.BindView;
import tr.xip.errorview.ErrorView;

public class ThesisListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<TaskResult<ArrayList<Thesis>>> {
	private static final int LID_THESIS_LIST = 0;

	@BindView(R.id.list) RecyclerView listView;
	@BindView(R.id.container1) SwipeRefreshLayout swipeView;
	@BindView(R.id.error) ErrorView error;

	@SaveState ArrayList<Thesis> list;
	@SaveState int tagFilter = 0;

	private int[] tagFilterNums;

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_thesis_list, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle item selection
		switch (item.getItemId()) {
			case R.id.action_select_tags:
				if(list == null) return true;

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(R.string.select_tags)
					   .setSingleChoiceItems(R.array.thesis_tags, tagFilter, (dialog, which) -> {
						   tagFilter = which;
						   getContent();
						   dialog.dismiss();
					   });
				builder.create().show();

				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_bulletin_list, container, false);
		bindView(view);
		setHasOptionsMenu(true);

		swipeView.setSize(SwipeRefreshLayout.LARGE);
		swipeView.setColorSchemeColors(getResources().getIntArray(R.array.loading));
		swipeView.setOnRefreshListener(this::getContent);
		listView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
		listView.setLayoutManager(new LinearLayoutManager(getActivity()));
		error.setOnRetryListener(this::getContent);

		tagFilterNums = getActivity().getResources().getIntArray(R.array.thesis_tag_filters);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if(list == null || list.isEmpty()) {
			getContent();
		} else showContent();
	}

	private void showContent() {
		ThesisAdapter adapter = new ThesisAdapter(list);
		listView.setAdapter(adapter);
		showProgress(false);
	}

	private void getContent() {
		showProgress(true);
		getLoaderManager().initLoader(LID_THESIS_LIST, null, this);
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
	public Loader<TaskResult<ArrayList<Thesis>>> onCreateLoader(int id, Bundle args) {
		int filter = tagFilterNums[tagFilter];
		return new ThesisListLoader(getActivity(), filter);
	}

	@Override
	public void onLoadFinished(Loader<TaskResult<ArrayList<Thesis>>> loader, TaskResult<ArrayList<Thesis>> result) {
		getLoaderManager().destroyLoader(LID_THESIS_LIST);
		showProgress(false);

		if(result.isSuccessful()) {
			list = result.getResult();

			if(!list.isEmpty()) showContent();
			else showEmpty(null);

		} else {
			showEmpty(result.getMessage());
		}
	}

	@Override
	public void onLoaderReset(Loader loader) {
		// do nothing
	}

}
