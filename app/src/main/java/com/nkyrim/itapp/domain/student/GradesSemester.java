package com.nkyrim.itapp.domain.student;

import java.util.ArrayList;

public final class GradesSemester extends ArrayList<Grade> {
	private final static long serialVersionUID = 7454584867827281075L;

	private String semester;
	private String noPassed;
	private String avgPassed;

	public GradesSemester(String semester) {
		if(semester == null) throw new IllegalArgumentException("semester cannot be null");
		this.semester = semester;
	}

	public String getSemester() {
		return semester;
	}

	public String getNoPassed() {
		return noPassed;
	}

	public String getAvgPassed() {
		return avgPassed;
	}

	public void setNoPassed(String noPassed) {
		if(noPassed == null) throw new IllegalArgumentException("noPassed cannot be null");
		this.noPassed = noPassed;
	}

	public void setAvgPassed(String avgPassed) {
		if(avgPassed == null) throw new IllegalArgumentException("avgPassed cannot be null");
		this.avgPassed = avgPassed;
	}

	@Override
	public String toString() {
		return "GradesSemester{" +
				"semester='" + semester + '\'' +
				", noPassed='" + noPassed + '\'' +
				", avgPassed='" + avgPassed + '\'' +
				'}';
	}
}
