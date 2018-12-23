package com.nkyrim.itapp.ui.bulletins;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nkyrim.itapp.ItNet;
import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.Bulletin;
import com.nkyrim.itapp.ui.util.base.BaseFragment;
import com.nkyrim.itapp.util.Cons;

import butterknife.BindView;
import pocketknife.SaveState;

public class BulletinDetailFragment extends BaseFragment {
	@BindView(R.id.tv_author) TextView tvAuthor;
	@BindView(R.id.tv_date) TextView tvDate;
	@BindView(R.id.tv_msg) TextView tvText;
	@BindView(R.id.tv_title) TextView tvTitle;
	@BindView(R.id.fab) FloatingActionButton fab;

	@SaveState Bulletin bulletin;

	public void setBulletin(Bulletin bulletin) {
		this.bulletin = bulletin;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_bulletin_detail, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_share:
				if(bulletin == null) return true;

				String share = bulletin.getTitle() + "\n\n" +
						bulletin.getText() + "\n\n" +
						bulletin.getAuthor() + "\n" +
						bulletin.getDate() + "\n\n\n" +
						Cons.URLS.HYDRA_BASE + bulletin.getAttach();

				Intent shareIntent = new Intent(Intent.ACTION_SEND);
				shareIntent.setType("text/plain");
				shareIntent.putExtra(Intent.EXTRA_TEXT, share);
				startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_bulletin_detail, container, false);
		bindView(view);
		setHasOptionsMenu(true);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);
		if(bulletin != null) showContent(bulletin);
	}

	public void showContent(Bulletin b) {
		bulletin = b;

		tvAuthor.setText(bulletin.getAuthor());
		tvTitle.setText(bulletin.getTitle());
		tvText.setText(bulletin.getText());
		tvDate.setText(bulletin.getDate());
		if(bulletin.getAttach() != null) {
			fab.setVisibility(View.VISIBLE);
			fab.bringToFront();
			fab.setOnClickListener(v -> ItNet.downloadHydra(Cons.URLS.HYDRA_BASE + bulletin.getAttach()));
		} else {
			fab.setVisibility(View.GONE);
		}
	}

}
