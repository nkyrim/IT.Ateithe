package com.nkyrim.itapp.ui.util.base;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public abstract class BaseRecyclerAdapter<DS, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
	protected final String TAG = getClass().getSimpleName();

	protected ArrayList<DS> dataset;
	protected ArrayList<DS> original;
	protected ArrayList<DS> filtered;

	public BaseRecyclerAdapter(ArrayList<DS> dataset) {
		this.dataset = dataset;
		original = dataset;
		filtered = new ArrayList<>();
	}

	public int getItemCount() {
		return dataset.size();
	}
}
