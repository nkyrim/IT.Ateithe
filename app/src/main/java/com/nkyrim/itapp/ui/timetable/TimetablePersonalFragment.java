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
import com.nkyrim.itapp.domain.courseselection.CourseSelection;
import com.nkyrim.itapp.domain.timetable.Timetable;
import com.nkyrim.itapp.domain.timetable.TimetableEntry;
import com.nkyrim.itapp.domain.timetable.TimetableSemester;
import com.nkyrim.itapp.ui.util.base.BaseFragment;

import java.util.List;

import butterknife.BindViews;

public class TimetablePersonalFragment extends BaseFragment {
	public final static String ARG_TT = "ARG_TT";
	public final static String ARG_SEL = "ARG_SEL";
	public final static String ARG_DAY = "ARG_DAY";

	@BindViews({R.id.slot1, R.id.slot2, R.id.slot3, R.id.slot4, R.id.slot5, R.id.slot6,
			R.id.slot7, R.id.slot8, R.id.slot9, R.id.slot10, R.id.slot11, R.id.slot12})
	List<LinearLayout> timeslot;

	private CourseSelection sel;
	private Timetable timetable;
	private int day = -1;

	public static TimetablePersonalFragment newInstance(Timetable list, int day, CourseSelection sel) {
		TimetablePersonalFragment fragment = new TimetablePersonalFragment();
		Bundle args = new Bundle();
		args.putSerializable(ARG_TT, list);
		args.putSerializable(ARG_SEL, sel);
		args.putInt(ARG_DAY, day);

		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null) {
			timetable = (Timetable) getArguments().getSerializable(ARG_TT);
			sel = (CourseSelection) getArguments().getSerializable(ARG_SEL);
			day = getArguments().getInt(ARG_DAY);
		}

		if(getArguments() == null || timetable == null || day == -1 || sel == null) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_timetable_day, container, false);
		bindView(view);

		for (TimetableSemester ttSemester : timetable.getSemesters()) {
			for (TimetableEntry entry : ttSemester.get(day)) {
				if(sel.containsCourse(entry)) {
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

					if(entry.getType().equals("E"))
						card.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.light_yellow));

					timeslot.get(entry.getTimeslot()).addView(content);
				}
			}

		}

		return view;
	}
}
