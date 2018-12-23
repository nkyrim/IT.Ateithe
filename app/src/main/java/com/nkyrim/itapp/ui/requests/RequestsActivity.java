package com.nkyrim.itapp.ui.requests;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nkyrim.itapp.ItNet;
import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.student.PithiaRequest;
import com.nkyrim.itapp.ui.util.base.BaseActivity;
import com.nkyrim.itapp.ui.util.DividerItemDecoration;
import com.nkyrim.itapp.ui.util.base.BaseRecyclerAdapter;
import pocketknife.SaveState;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import tr.xip.errorview.ErrorView;

public class RequestsActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<ArrayList<PithiaRequest>> {
	private static final int LID_REQS = 0;
	@BindView(R.id.error) ErrorView error;
	@BindView(R.id.prg) View prog;
	@BindView(R.id.list) RecyclerView listView;

	@SaveState ArrayList<PithiaRequest> list;

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_requests;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		listView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
		listView.setLayoutManager(new LinearLayoutManager(this));
		error.setOnRetryListener(this::getContent);

		if(list == null) getContent();
		else showContent(list);
	}

	private void getContent() {
		showProgress(true);
		getSupportLoaderManager().initLoader(LID_REQS, null, this);
	}

	private void showProgress(boolean show) {
		error.setVisibility(View.GONE);
		prog.setVisibility(show ? View.VISIBLE : View.GONE);
		listView.setVisibility(show ? View.GONE : View.VISIBLE);
	}

	private void showEmpty(String msg) {
		error.setVisibility(View.VISIBLE);
		error.setSubtitle(msg);
		prog.setVisibility(View.GONE);
		listView.setVisibility(View.GONE);
	}

	private void showContent(ArrayList<PithiaRequest> requests) {
		if(requests == null || requests.isEmpty()) {
			showEmpty("");
		} else {
			showProgress(false);
			RequestAdapter adapter = new RequestAdapter(requests);
			listView.setAdapter(adapter);
		}
	}

	@Override
	public Loader<ArrayList<PithiaRequest>> onCreateLoader(int id, Bundle args) {
		return new RequestLoader(RequestsActivity.this);
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<PithiaRequest>> loader, ArrayList<PithiaRequest> data) {
		getSupportLoaderManager().destroyLoader(LID_REQS);
		list = data;
		showContent(list);
	}

	@Override
	public void onLoaderReset(Loader<ArrayList<PithiaRequest>> loader) {
		list = null;
	}

	class RequestAdapter extends BaseRecyclerAdapter<PithiaRequest, RequestAdapter.RequestViewHolder> {

		public RequestAdapter(ArrayList<PithiaRequest> dataset) {
			super(dataset);
		}

		@Override
		public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_request, parent, false);
			return new RequestViewHolder(v);
		}

		@Override
		public void onBindViewHolder(RequestViewHolder holder, int position) {
			holder.bind(dataset.get(position));
		}

		class RequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
			@BindView(R.id.tv_title) TextView tv1;
			@BindView(R.id.tv_date) TextView tv2;

			private PithiaRequest r;

			public RequestViewHolder(View v) {
				super(v);
				ButterKnife.bind(this, v);
				v.setOnClickListener(this);
			}

			public void bind(PithiaRequest r) {
				this.r = r;
				tv1.setText(String.format("%s %s", r.getSn(), r.getType()));
				tv2.setText(r.getDate());
			}

			public void onClick(View v) {
				if(r != null) ItNet.downloadPithia(r.getLink());
			}
		}
	}

}
