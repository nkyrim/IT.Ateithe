package com.nkyrim.itapp.ui.bulletins;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.Bulletin;
import com.nkyrim.itapp.ui.util.base.BaseRecyclerAdapter;
import com.nkyrim.itapp.ui.util.BusEvents;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

class BulletinAdapter extends BaseRecyclerAdapter<Bulletin, BulletinAdapter.BulletinViewHolder> {

	public BulletinAdapter(ArrayList<Bulletin> dataset) {
		super(dataset);
	}

	public synchronized void setFilter(String filter) {
		if(filter != null && !filter.isEmpty()) {
			filtered.clear();
			for (Bulletin b : original) {
				if(b.getBoard().equals(filter)) filtered.add(b);
			}
			dataset = filtered;
		} else {
			dataset = original;
		}

		notifyDataSetChanged();
	}

	// Create new views (invoked by the layout manager)
	@Override
	public BulletinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_bulletin, parent, false);
		return new BulletinViewHolder(v);
	}

	// Replace the contents of a view (invoked by the layout manager)
	@Override
	public void onBindViewHolder(BulletinViewHolder holder, int position) {
		// get element from your dataset at this position replace the contents of the view with that element
		Bulletin b = dataset.get(position);
		holder.bind(b);
	}

	// Provide a reference to the views for each data item
	// Complex data items may need more than one view per item, and you provide access to all the views for a data item in a view holder
	public class BulletinViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@BindView(R.id.tv_author) TextView tv1;
		@BindView(R.id.tv_title) TextView tv2;
		@BindView(R.id.tv_board) TextView tv3;
		@BindView(R.id.tv_date) TextView tv4;
		private Bulletin b;

		public BulletinViewHolder(View v) {
			super(v);
			ButterKnife.bind(this, v);
			v.setOnClickListener(this);
		}

		public void bind(Bulletin b) {
			this.b = b;
			tv1.setText(b.getAuthor());
			tv2.setText(b.getTitle());
			tv3.setText(b.getBoard());
			tv4.setText(b.getDate());
		}

		public void onClick(View v) {
			if(b != null) EventBus.getDefault().post(new BusEvents.BulletinSelectEvent(b));
		}
	}
}
