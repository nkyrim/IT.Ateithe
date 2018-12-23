package com.nkyrim.itapp.domain;

import android.os.Parcel;
import android.os.Parcelable;

public final class Bulletin implements Parcelable {
	private String title;
	private String text;
	private String author;
	private String board;
	private String date;
	private String attach;

	public Bulletin(String title, String text, String author, String board, String date, String attach) {
		if(title == null) throw new IllegalArgumentException("title cannot be null");
		if(text == null) throw new IllegalArgumentException("text cannot be null");
		if(author == null) throw new IllegalArgumentException("author cannot be null");
		if(board == null) throw new IllegalArgumentException("board cannot be null");
		if(date == null) throw new IllegalArgumentException("date cannot be null");

		this.author = author;
		this.title = title;
		this.text = text;
		this.attach = attach;
		this.date = date;
		this.board = board;
	}

	public String getAttach() {
		return attach;
	}

	public String getAuthor() {
		return author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBoard() {
		return board;
	}

	public String getDate() {
		return date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "Bulletin{" +
				"attach='" + attach + '\'' +
				", title='" + title + '\'' +
				", text='" + text + '\'' +
				", author='" + author + '\'' +
				", board='" + board + '\'' +
				", date='" + date + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		Bulletin bulletin = (Bulletin) o;
		if(title != null ? !title.equals(bulletin.title) : bulletin.title != null) return false;
		if(text != null ? !text.equals(bulletin.text) : bulletin.text != null) return false;
		if(author != null ? !author.equals(bulletin.author) : bulletin.author != null) return false;
		if(board != null ? !board.equals(bulletin.board) : bulletin.board != null) return false;
		return !(date != null ? !date.equals(bulletin.date) : bulletin.date != null);
	}

	@Override
	public int hashCode() {
		int result = title != null ? title.hashCode() : 0;
		result = 31 * result + (text != null ? text.hashCode() : 0);
		result = 31 * result + (author != null ? author.hashCode() : 0);
		result = 31 * result + (board != null ? board.hashCode() : 0);
		result = 31 * result + (date != null ? date.hashCode() : 0);
		result = 31 * result + (attach != null ? attach.hashCode() : 0);
		return result;
	}

	private Bulletin(Parcel in) {
		this.title = in.readString();
		this.text = in.readString();
		this.author = in.readString();
		this.board = in.readString();
		this.date = in.readString();
		this.attach = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public final static Parcelable.Creator<Bulletin> CREATOR = new Parcelable.Creator<Bulletin>() {
		public Bulletin createFromParcel(Parcel source) {
			return new Bulletin(source);
		}

		public Bulletin[] newArray(int size) {
			return new Bulletin[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.title);
		dest.writeString(this.text);
		dest.writeString(this.author);
		dest.writeString(this.board);
		dest.writeString(this.date);
		dest.writeString(this.attach);
	}

}
