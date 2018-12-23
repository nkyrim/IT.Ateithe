package com.nkyrim.itapp.domain.timetable;

import java.io.Serializable;

public final class TimetableEntry implements Serializable {
	private static final long serialVersionUID = 1561020833848828805L;

	private final String code;
	private final String course;
	private final String instructor;
	private final String type;
	private final String room;
	private final String group;
	private final int timeslot;

	public TimetableEntry(String code, String course, String instructor, String type, String group, String room, int timeslot) {
		if(code == null) throw new IllegalArgumentException("code cannot be null");
		if(course == null) throw new IllegalArgumentException("course cannot be null");
		if(instructor == null) throw new IllegalArgumentException("instructor cannot be null");
		if(type == null) throw new IllegalArgumentException("type cannot be null");
		if(group == null) throw new IllegalArgumentException("group cannot be null");
		if(room == null) throw new IllegalArgumentException("room cannot be null");
		if(timeslot < 0) throw new IllegalArgumentException("timeslot cannot be less tha zero");

		this.code = code;
		this.course = course;
		this.instructor = instructor;
		this.type = type;
		this.room = room;
		this.group = group;
		this.timeslot = timeslot;
	}

	public String getCode() {
		return code;
	}

	public String getCourse() {
		return course;
	}

	public String getInstructor() {
		return instructor;
	}

	public String getType() {
		return type;
	}

	public String getRoom() {
		return room;
	}

	public String getGroup() {
		return group;
	}

	public int getTimeslot() {
		return timeslot;
	}

	@Override
	public String toString() {
		return "TimetableEntry{" +
				"code='" + code + '\'' +
				", course='" + course + '\'' +
				", instructor='" + instructor + '\'' +
				", type='" + type + '\'' +
				", room='" + room + '\'' +
				", group='" + group + '\'' +
				", timeslot=" + timeslot +
				'}';
	}
}