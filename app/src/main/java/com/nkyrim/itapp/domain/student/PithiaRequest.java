package com.nkyrim.itapp.domain.student;

import java.io.Serializable;

public final class PithiaRequest implements Serializable {
	private static final long serialVersionUID = 5640203989320673409L;

	private final String sn;
	private final String date;
	private final String type; // name
	private final String link;

	public PithiaRequest(String sn, String date, String type, String link) {
		if(sn == null) throw new IllegalArgumentException("sn cannot be null");
		if(date == null) throw new IllegalArgumentException("date cannot be null");
		if(type == null) throw new IllegalArgumentException("type cannot be null");
		if(link == null) throw new IllegalArgumentException("link cannot be null");

		this.sn = sn;
		this.date = date;
		this.type = type;
		this.link = link;
	}

	public String getDate() {
		return date;
	}

	public String getLink() {
		return link;
	}

	public String getSn() {
		return sn;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return "PithiaRequest{" +
				"sn='" + sn + '\'' +
				", date='" + date + '\'' +
				", type='" + type + '\'' +
				", link='" + link + '\'' +
				'}';
	}
}
