package com.nkyrim.itapp.domain.mail;

import java.io.Serializable;

public final class ForwardMail implements Serializable {
	private final static long serialVersionUID = -1445824017605665174L;

	private final String title;
	private final String sender;
	private final String date;
	private final String index;
	private final String folder; // "thismailbox" in parameters

	public ForwardMail(String title, String sender, String date, String index, String folder) {
		if(title == null) throw new IllegalArgumentException("title cannot be null");
		if(sender == null) throw new IllegalArgumentException("sender cannot be null");
		if(date == null) throw new IllegalArgumentException("date cannot be null");
		if(index == null) throw new IllegalArgumentException("index cannot be null");
		if(folder == null) throw new IllegalArgumentException("folder cannot be null");

		this.title = title;
		this.sender = sender;
		this.date = date;
		this.index = index;
		this.folder = folder;
	}

	public String getTitle() {
		return title;
	}

	public String getSender() {
		return sender;
	}

	public String getDate() {
		return date;
	}

	public String getIndex() {
		return index;
	}

	public String getFolder() {
		return folder;
	}

	@Override
	public String toString() {
		return "ForwardMail{" +
				"title='" + title + '\'' +
				", sender='" + sender + '\'' +
				", date='" + date + '\'' +
				", index='" + index + '\'' +
				", folder='" + folder + '\'' +
				'}';
	}
}
