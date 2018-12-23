package com.nkyrim.itapp.ui.util;

import android.net.Uri;

import com.nkyrim.itapp.domain.Bulletin;
import com.nkyrim.itapp.domain.Route;
import com.nkyrim.itapp.domain.Staff;
import com.nkyrim.itapp.domain.Thesis;
import com.nkyrim.itapp.domain.mail.Email;

import java.util.ArrayList;

public class BusEvents {
	private BusEvents() {}

	public static class MailMoveEvent {
		public boolean success;

		public MailMoveEvent(boolean success) {
			this.success = success;
		}
	}

	public static class LoginEvent {
		public boolean success;

		public LoginEvent(boolean success) {
			this.success = success;
		}
	}

	public static class CloseActivityEvent {
		public boolean close;

		public CloseActivityEvent(boolean close) {
			this.close = close;
		}
	}

	public static class BulletinSelectEvent {
		public Bulletin bulletin;

		public BulletinSelectEvent(Bulletin bulletin) {
			this.bulletin = bulletin;
		}
	}

	public static class ThesisSelectEvent {
		public Thesis thesis;

		public ThesisSelectEvent(Thesis thesis) {
			this.thesis = thesis;
		}
	}

	public static class StaffSelectEvent {
		public Staff staff;

		public StaffSelectEvent(Staff staff) {
			this.staff = staff;
		}
	}

	public static class FoldersLoadedEvent {
		public ArrayList<String> folders;

		public FoldersLoadedEvent(ArrayList<String> folders) {
			this.folders = folders;
		}
	}

	public static class MailSelectedEvent {
		public Email mail;

		public MailSelectedEvent(Email mail) {
			this.mail = mail;
		}
	}

	public static class ShowDirectionsEvent {
		public Route route;

		public ShowDirectionsEvent(Route route) {
			this.route = route;
		}
	}

	public static class FileSelectedEvent {
		public Uri uri;

		public FileSelectedEvent(Uri uri) {
			this.uri = uri;
		}
	}
}
