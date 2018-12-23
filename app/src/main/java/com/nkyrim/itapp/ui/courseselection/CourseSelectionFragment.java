package com.nkyrim.itapp.ui.courseselection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.courseselection.Course;
import com.nkyrim.itapp.domain.courseselection.CourseList;
import com.nkyrim.itapp.domain.courseselection.CourseSelection;
import com.nkyrim.itapp.ui.util.base.BaseFragment;

import java.util.Arrays;
import java.util.List;

public class CourseSelectionFragment extends BaseFragment {
	private static final String ARG_COURSE_SEL = "ARG_COURSE_SEL";
	private static final String ARG_SEM = "ARG_SEM";

	private List<String> groups;
	private int semester;
	private CourseSelection sel;
	private CourseList courseList;

	public static CourseSelectionFragment newInstance(int semester, CourseSelection selection) {
		if(selection == null) throw new IllegalArgumentException("sel cannot be null");

		CourseSelectionFragment fragment = new CourseSelectionFragment();
		Bundle args = new Bundle();
		args.putSerializable(ARG_COURSE_SEL, selection);
		args.putInt(ARG_SEM, semester);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sel = (CourseSelection) getArguments().getSerializable(ARG_COURSE_SEL);
		semester = getArguments().getInt(ARG_SEM);
		groups = Arrays.asList(getActivity().getResources().getStringArray(R.array.classes));
		courseList = new CourseList();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_course_selection, container, false);

		ViewGroup cont = (ViewGroup) view.findViewById(R.id.container);

		for (final Course c : courseList.get(semester)) {
			ViewGroup row = (ViewGroup) inflater.inflate(R.layout.row_course_sel, null);
			final TextView t1 = (TextView) row.findViewById(R.id.tv_title);
			final TextView t2 = (TextView) row.findViewById(R.id.tv_code);
			final LinearLayout erg = (LinearLayout) row.findViewById(R.id.container1);
			final CheckedTextView cbox = (CheckedTextView) row.findViewById(R.id.cbox);
			final Spinner spin = (Spinner) row.findViewById(R.id.spinner);

			t1.setText(c.getTitle());
			t2.setText(String.format("(%s)", c.getCode()));
			boolean b = sel.containsTheory(c.getCode());
			cbox.setChecked(b);
			cbox.setOnClickListener(view1 -> {
				cbox.toggle();
				if(cbox.isChecked()) sel.addTheory(c.getCode());
				else sel.removeTheory(c.getCode());
			});

			if(c.hasLab()) {
				erg.setVisibility(View.VISIBLE);
				spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
						if(spin.getSelectedItemPosition() != 0) {
							sel.addLab(c.getCode(), spin.getSelectedItem().toString());
						} else {
							sel.removeLab(c.getCode());
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> adapterView) {

					}
				});

				int x = groups.indexOf(sel.getLab(c.getCode()));
				if(x == -1) x = 0;
				final int finalX = x;
				spin.post(() -> spin.setSelection(finalX));
			} else {
				erg.setVisibility(View.GONE);
			}

			cont.addView(row);
		}

		return view;
	}
}
