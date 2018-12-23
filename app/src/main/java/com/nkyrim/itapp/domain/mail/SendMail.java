package com.nkyrim.itapp.domain.mail;

import java.io.Serializable;
import java.util.ArrayList;

public final class SendMail implements Serializable {
	private static final long serialVersionUID = -2905281621956534796L;

	private String to;
	private String cc;
	private String bcc;
	private String title;
	private String msg;
	private ArrayList<String> attachments;

	public SendMail(String to, String cc, String bcc, String title, String msg, ArrayList<String> attachments) {
		if(to == null) throw new IllegalArgumentException("to cannot be null");
		if(cc == null) throw new IllegalArgumentException("cc cannot be null");
		if(bcc == null) throw new IllegalArgumentException("bcc cannot be null");
		if(title == null) throw new IllegalArgumentException("title cannot be null");
		if(msg == null) throw new IllegalArgumentException("msg cannot be null");
		if(attachments == null) throw new IllegalArgumentException("attachments cannot be null");

		this.to = to;
		this.cc = cc;
		this.bcc = bcc;
		this.title = title;
		this.msg = msg;
		this.attachments = attachments;
	}

	public String getTo() {
		return to;
	}

	public String getCc() {
		return cc;
	}

	public String getBcc() {
		return bcc;
	}

	public String getTitle() {
		return title;
	}

	public String getMsg() {
		return msg;
	}

	public ArrayList<String> getAttachments() {
		return attachments;
	}

	@Override
	public String toString() {
		return "SendMail{" +
				"to='" + to + '\'' +
				", cc='" + cc + '\'' +
				", bcc='" + bcc + '\'' +
				", title='" + title + '\'' +
				", msg='" + msg + '\'' +
				", attachments=" + attachments +
				'}';
	}
}
