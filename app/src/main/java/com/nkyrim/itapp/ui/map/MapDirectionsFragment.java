package com.nkyrim.itapp.ui.map;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import pocketknife.SaveState;

import java.util.ArrayList;

public class MapDirectionsFragment extends ListFragment {
	@SaveState ArrayList<String> list;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if(list != null) {
			ArrayAdapter<String> adapter = new DirectionsAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
			setListAdapter(adapter);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	public void showContent(final ArrayList<String> dirList) {
		list = dirList;
		ArrayAdapter<String> adapter = new DirectionsAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
		setListAdapter(adapter);
	}

	private class DirectionsAdapter extends ArrayAdapter<String> {

		public DirectionsAdapter(Context context, int resource, ArrayList<String> objects) {
			super(context, resource, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row;

			if(convertView == null) row = View.inflate(getActivity(), android.R.layout.simple_list_item_1, null);
			else row = convertView;

			TextView tv = (TextView) row.findViewById(android.R.id.text1);
			tv.setText(Html.fromHtml(getItem(position)));

			return row;
		}
	}
}
