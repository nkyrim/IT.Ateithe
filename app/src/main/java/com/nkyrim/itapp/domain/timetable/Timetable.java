package com.nkyrim.itapp.domain.timetable;

import java.io.Serializable;
import java.util.ArrayList;

public final class Timetable implements Serializable {
	private static final long serialVersionUID = -9195950106611263600L;

	private final ArrayList<TimetableSemester> semesters;
	private String acSemester;

	private Timetable(ArrayList<String> data) {
		if(data == null) throw new IllegalArgumentException("data cannot be null");

		// add semesters
		semesters = new ArrayList<>();
		for (int i = 0; i < 7; i++) {
			// add days per acSemester
			TimetableSemester semester = new TimetableSemester();
			for (int y = 0; y < 5; y++) {
				semester.add(new TimetableDay());
			}
			semesters.add(semester);
		}

		parseData(data);
	}

	public String getAcademicSemester() {
		return acSemester;
	}

	private void parseData(ArrayList<String> rawData) {
		String[] tokens;
		String code, course, instructor, type, group, room;
		int day, semester, timeslot;
		TimetableEntry entry;

		this.acSemester = rawData.remove(0);

		for (String s : rawData) {
			tokens = s.split(":");
			code = tokens[0];
			course = tokens[1];
			instructor = tokens[2];
			type = tokens[3];
			room = tokens[4];
			group = tokens[5];
			semester = Integer.parseInt(tokens[6]);
			day = Integer.parseInt(tokens[7]);
			timeslot = Integer.parseInt(tokens[8]);

			entry = new TimetableEntry(code, course, instructor, type, group, room, timeslot);
			semesters.get(semester).get(day).add(entry);
		}
	}

	public ArrayList<TimetableSemester> getSemesters() {
		return semesters;
	}

	public static Timetable create(ArrayList<String> data) {
		return new Timetable(data);
	}
}
