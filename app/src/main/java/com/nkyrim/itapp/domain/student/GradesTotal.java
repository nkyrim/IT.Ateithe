package com.nkyrim.itapp.domain.student;

import java.util.ArrayList;

public final class GradesTotal extends ArrayList<GradesSemester> {
	private final static long serialVersionUID = 7909704662510979850L;

	private final GradesInfo info;

	public GradesTotal(GradesInfo info) {
		if(info == null) throw new IllegalArgumentException("info cannot be null");
		this.info = info;
	}

	public GradesInfo getInfo() {
		return info;
	}

	@Override
	public String toString() {
		return "GradesTotal{" +
				"info=" + info +
				'}';
	}
}
