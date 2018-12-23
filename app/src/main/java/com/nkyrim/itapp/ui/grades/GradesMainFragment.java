package com.nkyrim.itapp.ui.grades;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.student.Grade;
import com.nkyrim.itapp.domain.student.GradesInfo;
import com.nkyrim.itapp.ui.util.base.BaseFragment;

import butterknife.BindView;

public class GradesMainFragment extends BaseFragment {
	private static final String ARG_INFO = "ARG_INFO";

	@BindView(R.id.tv_name) TextView tvName;
	@BindView(R.id.tv_am) TextView tvAM;
	@BindView(R.id.tv_passed) TextView tvPassed;
	@BindView(R.id.tv_avg) TextView tvAvg;
	@BindView(R.id.tv_dm) TextView tvDM;
	@BindView(R.id.container1) ViewGroup latestGrades;

	private GradesInfo info;

	public GradesMainFragment() { }

	public static GradesMainFragment newInstance(GradesInfo info) {
		if(info == null) throw new IllegalArgumentException("info cannot be null");

		GradesMainFragment fragment = new GradesMainFragment();
		Bundle args = new Bundle();
		args.putSerializable(ARG_INFO, info);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		info = (GradesInfo) getArguments().getSerializable(ARG_INFO);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_grades_main, container, false);
		bindView(view);

		tvName.setText(info.getStudent());
		tvAM.setText(getString(R.string.pi_am, info.getStudentAM()));
		tvPassed.setText(info.getPassedCourses());
		tvAvg.setText(info.getAvgGrade());
		tvDM.setText(info.getTotalDM());

		for (Grade grade : info.getLatestGrades()) {
			ViewGroup content = (ViewGroup) inflater.inflate(R.layout.row_grade_recent, null);
			TextView tv1 = (TextView) content.findViewById(R.id.tv_course);
			TextView tv2 = (TextView) content.findViewById(R.id.tv_grade);
			TextView tv5 = (TextView) content.findViewById(R.id.tv_exam);

			tv1.setText(grade.getCourse());
			tv2.setText(grade.getGradeAvg());
			tv5.setText(grade.getExam());

			latestGrades.addView(content);
		}

		return view;
	}
}

