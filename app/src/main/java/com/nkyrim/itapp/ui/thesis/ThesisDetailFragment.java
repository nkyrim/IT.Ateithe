package com.nkyrim.itapp.ui.thesis;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.nkyrim.itapp.ItNet;
import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.Thesis;
import com.nkyrim.itapp.ui.util.base.BaseFragment;
import com.nkyrim.itapp.ui.util.TaskResult;
import com.nkyrim.itapp.util.Cons;
import com.nkyrim.itapp.util.Util;
import pocketknife.SaveState;

import butterknife.BindView;
import pocketknife.SaveState;

public class ThesisDetailFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<TaskResult<Thesis>> {
	private static final int LID_THESIS = 0;
	// UI elements
	@BindView(R.id.tv_title) TextView tvTitle;
	@BindView(R.id.tv_teacher) TextView tvTeacher;
	@BindView(R.id.tv_summary) TextView tvSummary;
	@BindView(R.id.tv_tags) TextView tvTags;
	@BindView(R.id.tv_submitted) TextView tvSubmitted;
	@BindView(R.id.tv_avail) TextView tvAvail;
	@BindView(R.id.tv_comp) TextView tvComp;
	@BindView(R.id.fab) FloatingActionButton fab;
	@BindView(R.id.prg) ProgressBar prgBar;
	@BindView(R.id.view_detail) View detailView;

	// fields
	@SaveState Thesis thesis;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_thesis_detail, container, false);
		bindView(view);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(thesis != null) showContent(thesis);
	}

	public void showContent(Thesis t) {
		showProgress(true);
		// just a way to see if data already retrieved
		if(t.getTitle() != null) {
			setContent(t);
		} else {
			Bundle args = new Bundle();
			args.putString("id", t.getDetailID());
			getLoaderManager().initLoader(LID_THESIS, args, this);
		}
	}

	private void setContent(Thesis t) {
		thesis = t;
		tvTitle.setText(thesis.getTitle());
		tvTeacher.setText(thesis.getTeacher());
		tvSummary.setText(thesis.getSummary());
		tvTags.setText(thesis.getTags());
		tvSubmitted.setText(thesis.getSubmitted());
		tvAvail.setText((thesis.isAvailable() ? getActivity().getString(R.string.yes) : getActivity().getString(R.string.no)));
		tvComp.setText((thesis.isCompleted() ? getActivity().getString(R.string.yes) : getActivity().getString(R.string.no)));
		if(thesis.getDocID() != null && !thesis.getDocID().isEmpty()) {
			fab.setOnClickListener(v -> ItNet.downloadHydra(Cons.URLS.HYDRA_THESIS_DOWNLOAD + thesis.getDocID()));
			fab.setVisibility(View.VISIBLE);
		}

		showProgress(false);
	}

	private void showProgress(boolean show) {
		prgBar.setVisibility(show ? View.VISIBLE : View.GONE);
		detailView.setVisibility(show ? View.GONE : View.VISIBLE);
		if(show) {
			tvTitle.setText("");
			fab.setVisibility(View.GONE);
		}
	}

	@Override
	public Loader<TaskResult<Thesis>> onCreateLoader(int id, Bundle args) {
		String tid = args.getString("id");
		return new ThesisDetailLoader(getActivity(), tid);
	}

	@Override
	public void onLoadFinished(Loader<TaskResult<Thesis>> loader, TaskResult<Thesis> result) {
		getLoaderManager().destroyLoader(LID_THESIS);

		if(result.isSuccessful()) setContent(result.getResult());
		else Util.showToast(getActivity(), result.getMessage());
	}

	@Override
	public void onLoaderReset(Loader<TaskResult<Thesis>> loader) {
		thesis = null;
	}

}
