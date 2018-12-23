package com.nkyrim.itapp.ui.timetable;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.timetable.TimetableDay;
import com.nkyrim.itapp.domain.timetable.TimetableEntry;
import com.nkyrim.itapp.ui.util.base.BaseFragment;

import java.util.List;

import butterknife.BindViews;

public class TimetableFragment extends BaseFragment {
	public final static String ARG_TT_DAY = "ARG_TT_DAY";

	@BindViews({R.id.slot1, R.id.slot2, R.id.slot3, R.id.slot4, R.id.slot5, R.id.slot6,
			R.id.slot7, R.id.slot8, R.id.slot9, R.id.slot10, R.id.slot11, R.id.slot12})
	List<LinearLayout> timeslots;

	public static TimetableFragment newInstance(TimetableDay dayList) {
		TimetableFragment fragment = new TimetableFragment();
		Bundle args = new Bundle();
		args.putSerializable(ARG_TT_DAY, dayList);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_timetable_day, container, false);
		bindView(view);
		setHasOptionsMenu(true);

		TimetableDay ttDay = (TimetableDay) getArguments().getSerializable(ARG_TT_DAY);

		if(ttDay != null) {
			for (TimetableEntry entry : ttDay) {
				ViewGroup content = (ViewGroup) inflater.inflate(R.layout.row_timetable, null);
				CardView card = (CardView) content.findViewById(R.id.container1);
				TextView tvTitle = (TextView) content.findViewById(R.id.tv_title);
				TextView tvType = (TextView) content.findViewById(R.id.tv_type);
				TextView tvRoom = (TextView) content.findViewById(R.id.tv_room);
				TextView tvLab = (TextView) content.findViewById(R.id.tv_lab);
				TextView tvTeacher = (TextView) content.findViewById(R.id.tv_teacher);

				tvTitle.setText(String.format("%s (%s)", entry.getCourse(), entry.getCode()));
				tvType.setText(String.format("(%s)", entry.getType()));
				tvRoom.setText(getString(R.string.tt_room, entry.getRoom()));
				tvLab.setText(entry.getGroup());
				tvTeacher.setText(entry.getInstructor());

				if(entry.getType().equals("E")) card.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.light_yellow));

				timeslots.get(entry.getTimeslot()).addView(content);
			}
		}

		return view;
	}
}
