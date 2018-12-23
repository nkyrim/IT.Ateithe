package com.nkyrim.itapp.domain.student;

import java.io.Serializable;

public final class Grade implements Serializable {
	private final static long serialVersionUID = 8646636066391264265L;

	private String code;
	private String course;
	private String gradeAvg;
	private String gradeTheory;
	private String gradeLab;
	private String exam;
	private String examTheory;
	private String examLab;

	public Grade(String course, String gradeAvg, String exam) {
		this(null, course, gradeAvg, exam);
	}

	public Grade(String code, String course, String gradeAvg, String exam) {
		if(course == null) throw new IllegalArgumentException("course cannot be null");
		if(gradeAvg == null) throw new IllegalArgumentException("gradeAvg cannot be null");
		if(exam == null) throw new IllegalArgumentException("exam cannot be null");

		this.code = code;
		this.course = course;
		this.gradeAvg = gradeAvg;
		this.exam = exam;
	}

	public String getCode() {
		return code;
	}

	public String getCourse() {
		return course;
	}

	public String getExam() {
		return exam;
	}

	public String getExamLab() {
		return examLab;
	}

	public String getExamTheory() {
		return examTheory;
	}

	public String getGradeAvg() {
		return gradeAvg;
	}

	public String getGradeLab() {
		return gradeLab;
	}

	public String getGradeTheory() {
		return gradeTheory;
	}

	public void setExamLab(String examLab) {
		if(examLab == null) throw new IllegalArgumentException("examLab cannot be null");
		this.examLab = examLab;
	}

	public void setExamTheory(String examTheory) {
		if(examTheory == null) throw new IllegalArgumentException("examTheory cannot be null");
		this.examTheory = examTheory;
	}

	public void setGradeLab(String gradeLab) {
		if(gradeLab == null) throw new IllegalArgumentException("gradeLab cannot be null");
		this.gradeLab = gradeLab;
	}

	public void setGradeTheory(String gradeTheory) {
		if(gradeTheory == null) throw new IllegalArgumentException("gradeTheory cannot be null");
		this.gradeTheory = gradeTheory;
	}

	@Override
	public String toString() {
		return "Grade{" +
				"code='" + code + '\'' +
				", course='" + course + '\'' +
				", gradeAvg='" + gradeAvg + '\'' +
				", gradeTheory='" + gradeTheory + '\'' +
				", gradeLab='" + gradeLab + '\'' +
				", exam='" + exam + '\'' +
				", examTheory='" + examTheory + '\'' +
				", examLab='" + examLab + '\'' +
				'}';
	}
}
