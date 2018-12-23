package com.nkyrim.itapp.ui.staff;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.Staff;
import com.nkyrim.itapp.ui.util.base.BaseRecyclerAdapter;
import com.nkyrim.itapp.ui.util.BusEvents;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import org.greenrobot.eventbus.EventBus;

class StaffAdapter extends BaseRecyclerAdapter<Staff, StaffAdapter.StaffViewHolder> {

	public StaffAdapter(ArrayList<Staff> dataset) {
		super(dataset);
	}

	public synchronized void setFilter(String filter) {
		if(filter != null && !filter.isEmpty()) {
			filtered.clear();
			for (Staff s : original) {
				if(s.getFullName().toLowerCase().contains(filter.toLowerCase())) filtered.add(s);
			}
			dataset = filtered;
		} else {
			dataset = original;
		}

		notifyDataSetChanged();
	}

	// Create new views (invoked by the layout manager)
	@Override
	public StaffViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_staff, parent, false);
		return new StaffViewHolder(v);
	}

	// Replace the contents of a view (invoked by the layout manager)
	@Override
	public void onBindViewHolder(StaffViewHolder holder, int position) {
		// get element from your dataset at this position replace the contents of the view with that element
		Staff b = dataset.get(position);
		holder.bind(b);
	}

	public class StaffViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@BindView(R.id.tv_name) TextView tv1;
		private Staff s;

		public StaffViewHolder(View v) {
			super(v);
			ButterKnife.bind(this, v);
			v.setOnClickListener(this);
		}

		public void bind(Staff s) {
			this.s = s;
			tv1.setText(s.getFullName());
		}

		public void onClick(View v) {
			if(s != null) EventBus.getDefault().post(new BusEvents.StaffSelectEvent(s));
		}
	}
}
