package com.nkyrim.itapp.domain.courseselection;

import com.nkyrim.itapp.domain.timetable.TimetableEntry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The class represents the courses, theories and labs the student has theories to be displayed in his personal timetable.
 */
public final class CourseSelection implements Serializable {
	private static final long serialVersionUID = 1466759545904907448L;

	private final List<String> theories = new ArrayList<>();
	private final HashMap<String, String> labs = new HashMap<>();

	public boolean containsCourse(TimetableEntry e) {
		if(e.getType().equals("Θ")) return theories.contains(e.getCode());
		else {
			String group = labs.get(e.getCode());
			return group != null && group.equals(e.getGroup());
		}
	}

	public boolean containsTheory(String code) {
		return theories.contains(code);
	}

	public void addTheory(String code) {
		theories.add(code);
	}

	public void removeTheory(String code) {
		theories.remove(code);
	}

	public String getLab(String code) {
		return labs.get(code);
	}

	public void addLab(String code, String group) {
		labs.put(code, group);
	}

	public void removeLab(String code) {
		labs.remove(code);
	}

	public boolean isEmpty() {
		return theories.isEmpty() && labs.isEmpty();
	}

	@Override
	public String toString() {
		return "CourseSelection{" +
				"theories=" + theories +
				", labs=" + labs +
				'}';
	}
}
