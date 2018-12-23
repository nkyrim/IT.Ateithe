package com.nkyrim.itapp.ui.thesis;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.Thesis;
import com.nkyrim.itapp.ui.util.base.BaseRecyclerAdapter;
import com.nkyrim.itapp.ui.util.BusEvents;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import org.greenrobot.eventbus.EventBus;

class ThesisAdapter extends BaseRecyclerAdapter<Thesis, ThesisAdapter.BulletinViewHolder> {

	public ThesisAdapter(ArrayList<Thesis> dataset) {
		super(dataset);
	}

	// Create new views (invoked by the layout manager)
	@Override
	public BulletinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_thesis, parent, false);

		return new BulletinViewHolder(v);
	}

	// Replace the contents of a view (invoked by the layout manager)
	@Override
	public void onBindViewHolder(BulletinViewHolder holder, int position) {
		// - get element from your dataset at this position replace the contents of the view with that element
		Thesis b = dataset.get(position);
		holder.bind(b);
	}

	public class BulletinViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@BindView(R.id.tv_title) TextView tv1;
		@BindView(R.id.tv_teacher) TextView tv2;
		private Thesis t;

		public BulletinViewHolder(View v) {
			super(v);
			ButterKnife.bind(this, v);
			v.setOnClickListener(this);
		}

		public void bind(Thesis t) {
			this.t = t;
			tv1.setText(t.getShortTitle());
			tv2.setText(t.getTeacher());
		}

		public void onClick(View v) {
			if(t != null) EventBus.getDefault().post(new BusEvents.ThesisSelectEvent(t));
		}
	}
}