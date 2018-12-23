package com.nkyrim.itapp.domain.mail;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public final class Email implements Comparable<Email>, Serializable {
	private final static long serialVersionUID = 2603256136862472855L;

	private String sender;
	private String subject;
	private String text;
	private String html;
	private String folder;
	private Date date;
	private int num;
	private boolean read;
	private boolean deleted;
	private ArrayList<String> attachments;
	private boolean isComplete;

	public Email(String folder, String sender, String subject, Date date, int num,
				 ArrayList<String> attachments, boolean read, boolean deleted) {
		if(folder == null) throw new IllegalArgumentException("folder cannot be null");
		if(sender == null) throw new IllegalArgumentException("sender cannot be null");
		if(date == null) throw new IllegalArgumentException("date cannot be null");
		if(num < 0) throw new IllegalArgumentException("num cannot be less than zero");
		if(attachments == null) throw new IllegalArgumentException("attachments cannot be null");

		this.folder = folder;
		this.sender = sender;
		this.subject = subject;
		this.date = date;
		this.num = num;
		this.read = read;
		this.deleted = deleted;
		this.attachments = attachments;
	}

	public String getHtml() {
		return html;
	}

	public boolean hasAttachments() {
		return !attachments.isEmpty();
	}

	public ArrayList<String> getAttachments() {
		return attachments;
	}

	public int getNum() {
		return num;
	}

	public Date getDate() {
		return date;
	}

	public String getSender() {
		return sender;
	}

	public String getFolder() {
		return folder;
	}

	public String getSubject() {
		return subject;
	}

	public String getText() {
		return text;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public boolean isRead() {
		return read;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setIsComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	@Override
	public int compareTo(@NonNull Email mail) {
		return date.compareTo(mail.date);
	}

	@Override
	public String toString() {
		return "Email{" +
				"sender='" + sender + '\'' +
				", subject='" + subject + '\'' +
				", text='" + text + '\'' +
				", html='" + html + '\'' +
				", folder='" + folder + '\'' +
				", date=" + date +
				", num=" + num +
				", read=" + read +
				", attachments=" + attachments +
				", isComplete=" + isComplete +
				'}';
	}
}
