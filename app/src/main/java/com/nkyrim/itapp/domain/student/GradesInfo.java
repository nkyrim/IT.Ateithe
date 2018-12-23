package com.nkyrim.itapp.domain.student;

import java.io.Serializable;
import java.util.ArrayList;

public final class GradesInfo implements Serializable {
	private final static long serialVersionUID = 7471309077772151113L;

	private String student;
	private String studentAM;
	private String passedCourses;
	private String avgGrade;
	private String totalDM;
	private ArrayList<Grade> latestGrades;

	public GradesInfo(String student, String studentAM, ArrayList<Grade> latestGrades) {
		if(student == null) throw new IllegalArgumentException("student cannot be null");
		if(studentAM == null) throw new IllegalArgumentException("studentAM cannot be null");
		if(latestGrades == null) throw new IllegalArgumentException("latestGrades cannot be null");

		this.latestGrades = latestGrades;
		this.student = student;
		this.studentAM = studentAM;
	}

	public String getAvgGrade() {
		return avgGrade;
	}

	public ArrayList<Grade> getLatestGrades() {
		return latestGrades;
	}

	public String getPassedCourses() {
		return passedCourses;
	}

	public String getStudent() {
		return student;
	}

	public String getStudentAM() {
		return studentAM;
	}

	public String getTotalDM() {
		return totalDM;
	}

	public void setAvgGrade(String avgGrade) {
		if(avgGrade == null) throw new IllegalArgumentException("avgGrade cannot be null");
		this.avgGrade = avgGrade;
	}

	public void setPassedCourses(String passedCourses) {
		if(passedCourses == null) throw new IllegalArgumentException("passedCourses cannot be null");
		this.passedCourses = passedCourses;
	}

	public void setTotalDM(String totalDM) {
		if(totalDM == null) throw new IllegalArgumentException("totalDM cannot be null");
		this.totalDM = totalDM;
	}

	@Override
	public String toString() {
		return "GradesInfo{" +
				"student='" + student + '\'' +
				", studentAM='" + studentAM + '\'' +
				", passedCourses='" + passedCourses + '\'' +
				", avgGrade='" + avgGrade + '\'' +
				", totalDM='" + totalDM + '\'' +
				", latestGrades=" + latestGrades +
				'}';
	}
}

