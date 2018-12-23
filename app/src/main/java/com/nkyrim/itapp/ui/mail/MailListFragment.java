package com.nkyrim.itapp.ui.mail;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.mail.Email;
import com.nkyrim.itapp.ui.util.base.BaseFragment;
import com.nkyrim.itapp.ui.util.BusEvents;
import com.nkyrim.itapp.ui.util.DividerItemDecoration;
import pocketknife.SaveState;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import tr.xip.errorview.ErrorView;

public class MailListFragment extends BaseFragment {
	private static final int LID_MAIL = 1;
	private static final String ARG_FOLDER = "ARG_FOLDER";
	// UI elements
	@BindView(R.id.list) RecyclerView listView;
	@BindView(R.id.container1) SwipeRefreshLayout swipeView;
	@BindView(R.id.error) ErrorView error;

	// fields
	@SaveState ArrayList<Email> list;
	@SaveState String folder;

	@Override
	public void onResume() {
		super.onResume();
		EventBus.getDefault().register(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mail_list, container, false);
		bindView(view);
		setHasOptionsMenu(true);

		// Setup components
		swipeView.setSize(SwipeRefreshLayout.LARGE);
		swipeView.setColorSchemeColors(getResources().getIntArray(R.array.loading));
		swipeView.setOnRefreshListener(() -> getContent(folder));
		listView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
		listView.setLayoutManager(new LinearLayoutManager(getActivity()));
		error.setOnRetryListener(() -> getContent(folder));

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if(savedInstanceState == null || list == null) {
			getContent("INBOX");
		} else if(getLoaderManager().getLoader(LID_MAIL) != null) {
			// if mail list loader present, reconnect
			if(folder != null) getContent(folder);
		} else showContent();
	}

	private void showContent() {
		MailAdapter adapter = new MailAdapter(list);
		listView.setAdapter(adapter);
		showProgress(false);
	}

	public void getContent(String folder) {
		showProgress(true);
		this.folder = folder;
		Bundle b = new Bundle();
		b.putString(ARG_FOLDER, folder);
		getLoaderManager().initLoader(LID_MAIL, b, new MailLoaderCallbacks());
	}

	private void showEmpty(String msg) {
		swipeView.setVisibility(View.GONE);
		error.setVisibility(View.VISIBLE);
		error.setSubtitle(msg);
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

	@Subscribe
	public void onEventMainThread(BusEvents.MailMoveEvent event) {
		if(event.success) getContent(folder);
		EventBus.getDefault().removeStickyEvent(event);
	}

	class MailLoaderCallbacks implements LoaderManager.LoaderCallbacks<ArrayList<Email>> {

		@Override
		public Loader<ArrayList<Email>> onCreateLoader(int i, Bundle args) {
			return new MailLoader(getActivity(), args.getString(ARG_FOLDER));
		}

		@Override
		public void onLoadFinished(Loader<ArrayList<Email>> loader, ArrayList<Email> emails) {
			getLoaderManager().destroyLoader(LID_MAIL);

			list = emails;
			if(list == null || list.isEmpty()) {
				showContent();
				showEmpty(null);
			} else {
				showContent();
			}
		}

		@Override
		public void onLoaderReset(Loader<ArrayList<Email>> arrayListLoader) {
			// list = null;
		}
	}

}