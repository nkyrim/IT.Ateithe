package com.nkyrim.itapp.ui.grades;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.student.Grade;
import com.nkyrim.itapp.domain.student.GradesSemester;
import com.nkyrim.itapp.ui.util.base.BaseFragment;

import butterknife.BindView;

public class GradesFragment extends BaseFragment {
	private static final String ARG_GRADES = "ARG_GRADES";

	@BindView(R.id.tv_passed) TextView tvPassed;
	@BindView(R.id.tv_avg) TextView tvSemAvg;

	private GradesSemester grades;

	public static GradesFragment newInstance(GradesSemester gradesSemester) {
		if(gradesSemester == null) throw new IllegalArgumentException("gradesSemester cannot be null");

		GradesFragment fragment = new GradesFragment();
		Bundle args = new Bundle();
		args.putSerializable(ARG_GRADES, gradesSemester);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		grades = (GradesSemester) getArguments().getSerializable(ARG_GRADES);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_grades, container, false);
		bindView(view);

		tvPassed.setText(grades.getNoPassed());
		tvSemAvg.setText(grades.getAvgPassed());

		for (Grade grade : grades) {
			ViewGroup content = (ViewGroup) inflater.inflate(R.layout.row_grade, null);
			ViewGroup gradeList = (ViewGroup) view.findViewById(R.id.container1);
			ViewGroup groupDetail = (ViewGroup) content.findViewById(R.id.container2);

			TextView tv1 = (TextView) content.findViewById(R.id.tv_course);
			TextView tv2 = (TextView) content.findViewById(R.id.tv_grade);
			TextView tv3 = (TextView) content.findViewById(R.id.tv_grade_theory);
			TextView tv4 = (TextView) content.findViewById(R.id.tv_grade_lab);
			TextView tv5 = (TextView) content.findViewById(R.id.tv_exam);
			TextView tv6 = (TextView) content.findViewById(R.id.tv_exam_theory);
			TextView tv7 = (TextView) content.findViewById(R.id.tv_exam_lab);

			tv1.setText(String.format("%s %s", grade.getCourse(), grade.getCode()));
			tv2.setText(grade.getGradeAvg());
			tv5.setText(grade.getExam());
			if(TextUtils.isEmpty(grade.getGradeAvg()) || grade.getGradeAvg().equals("-")) {
				tv5.setVisibility(View.GONE);
				tv6.setVisibility(View.GONE);
				tv7.setVisibility(View.GONE);
			}
			if(TextUtils.isEmpty(grade.getGradeTheory())) {
				groupDetail.setVisibility(View.GONE);
			} else {
				tv3.setText(grade.getGradeTheory());
				tv4.setText(grade.getGradeLab());
				tv6.setText(grade.getExamTheory());
				tv7.setText(grade.getExamLab());
			}

			gradeList.addView(content);
		}

		return view;
	}
}
