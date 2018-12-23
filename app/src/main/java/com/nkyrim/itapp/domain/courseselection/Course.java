package com.nkyrim.itapp.domain.courseselection;

import java.io.Serializable;

public final class Course implements Serializable {
	private static final long serialVersionUID = -8372493852678881440L;

	private final String code;
	private final String title;
	private final boolean hasLab;

	public Course(String code, String title, boolean hasLab) {
		if(code == null) throw new IllegalArgumentException("code cannot be null");
		if(title == null) throw new IllegalArgumentException("title cannot be null");

		this.code = code;
		this.title = title;
		this.hasLab = hasLab;
	}

	public String getCode() {
		return code;
	}

	public String getTitle() {
		return title;
	}

	public boolean hasLab() {
		return hasLab;
	}

	@Override
	public String toString() {
		return "Course{" +
				"code='" + code + '\'' +
				", title='" + title + '\'' +
				", hasLab=" + hasLab +
				'}';
	}
}
