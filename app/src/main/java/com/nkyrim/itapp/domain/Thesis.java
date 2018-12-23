package com.nkyrim.itapp.domain;

import java.io.Serializable;

public final class Thesis implements Serializable {
	private static final long serialVersionUID = 1146930274850048675L;

	private String shortTitle;
	private String title;
	private String submitted; // date
	private String teacher;
	private String tags;
	private String summary;
	private String docID; // id for link
	private String detailID; // id for detail page
	private boolean available;
	private boolean completed;

	public Thesis(String shortTitle, String teacher, String detailID, String tags, boolean available) {
		if(shortTitle == null) throw new IllegalArgumentException("shortTitle cannot be null");
		if(teacher == null) throw new IllegalArgumentException("teacher cannot be null");
		if(detailID == null) throw new IllegalArgumentException("detailID cannot be null");
		if(tags == null) throw new IllegalArgumentException("tags cannot be null");

		this.shortTitle = shortTitle;
		this.teacher = teacher;
		this.detailID = detailID;
		this.available = available;
		this.tags = tags;
	}

	public Thesis(String title, String teacher, String summary, String submitted, String tags,
				  String docID, boolean available, boolean completed) {
		if(title == null) throw new IllegalArgumentException("title cannot be null");
		if(teacher == null) throw new IllegalArgumentException("teacher cannot be null");
		if(summary == null) throw new IllegalArgumentException("summary cannot be null");
		if(submitted == null) throw new IllegalArgumentException("submitted cannot be null");
		if(tags == null) throw new IllegalArgumentException("tags cannot be null");
		if(docID == null) throw new IllegalArgumentException("docID cannot be null");

		this.title = title;
		this.teacher = teacher;
		this.summary = summary;
		this.submitted = submitted;
		this.tags = tags;
		this.docID = docID;
		this.available = available;
		this.completed = completed;
	}

	public String getDetailID() {
		return detailID;
	}

	public String getDocID() {
		return docID;
	}

	public String getShortTitle() {
		return shortTitle;
	}

	public String getSubmitted() {
		return submitted;
	}

	public String getSummary() {
		return summary;
	}

	public String getTags() {
		return tags;
	}

	public String getTeacher() {
		return teacher;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isAvailable() {
		return available;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		Thesis thesis = (Thesis) o;

		if(available != thesis.available) return false;
		if(shortTitle != null ? !shortTitle.equals(thesis.shortTitle) : thesis.shortTitle != null) return false;
		if(title != null ? !title.equals(thesis.title) : thesis.title != null) return false;
		if(submitted != null ? !submitted.equals(thesis.submitted) : thesis.submitted != null) return false;
		if(teacher != null ? !teacher.equals(thesis.teacher) : thesis.teacher != null) return false;
		if(tags != null ? !tags.equals(thesis.tags) : thesis.tags != null) return false;
		if(summary != null ? !summary.equals(thesis.summary) : thesis.summary != null) return false;
		if(docID != null ? !docID.equals(thesis.docID) : thesis.docID != null) return false;
		return !(detailID != null ? !detailID.equals(thesis.detailID) : thesis.detailID != null);

	}

	@Override
	public int hashCode() {
		int result = shortTitle != null ? shortTitle.hashCode() : 0;
		result = 31 * result + (title != null ? title.hashCode() : 0);
		result = 31 * result + (submitted != null ? submitted.hashCode() : 0);
		result = 31 * result + (teacher != null ? teacher.hashCode() : 0);
		result = 31 * result + (tags != null ? tags.hashCode() : 0);
		result = 31 * result + (summary != null ? summary.hashCode() : 0);
		result = 31 * result + (docID != null ? docID.hashCode() : 0);
		result = 31 * result + (detailID != null ? detailID.hashCode() : 0);
		result = 31 * result + (available ? 1 : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Thesis{" +
				"shortTitle='" + shortTitle + '\'' +
				", title='" + title + '\'' +
				", submitted='" + submitted + '\'' +
				", teacher='" + teacher + '\'' +
				", tags='" + tags + '\'' +
				", summary='" + summary + '\'' +
				", docID='" + docID + '\'' +
				", detailID='" + detailID + '\'' +
				", available=" + available +
				'}';
	}

	public boolean isCompleted() {
		return completed;
	}
}
