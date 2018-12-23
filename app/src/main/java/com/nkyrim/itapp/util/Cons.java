package com.nkyrim.itapp.util;

public final class Cons {
	private Cons() {
	}

	// Account options
	public final static int ACCOUNT_OPTION_HYDRA = 1;
	public final static int ACCOUNT_OPTION_PITHIA = 2;
	public final static int ACCOUNT_OPTION_AETOS = 3;

	// Encodings
	public final static String UTF8 = "utf-8";
	public final static String WINDOWS_1253 = "windows-1253";

	// Notification IDs
	public final static int NOTI_ID_BULLETINS = 1;
	public final static int NOTI_ID_BULLETIN_DL = 2;
	public final static int NOTI_ID_MAIL_DL = 3;

	public final static class URLS {
		// Hydra
		public final static String HYDRA_BASE = "http://hydra.it.teithe.gr/s/";
		public final static String HYDRA_BASE_SECURE = "https://hydra.it.teithe.gr/s/";
		public final static String HYDRA_BULLETINS = HYDRA_BASE + "index.php?m=itdep-bbstud&_lang=el";
		// public final static String HYDRA_BULLETINS_DETAIL = HYDRA_BASE + "index.php?m=itdep-bbstud&bbviewid=";
		public final static String HYDRA_LOGIN = HYDRA_BASE_SECURE + "index.php?_lang=el";
		public final static String HYDRA_STAFF = HYDRA_BASE + "index.php?m=itdep-staffliststud&_lang=el";
		public final static String HYDRA_STAFF_DETAIL = HYDRA_BASE + "index.php?m=itdep-staffliststud&s=";
		public final static String HYDRA_THESIS = HYDRA_BASE + "index.php?m=itdep-ptyxstud&s=all&tagfilter[%d]=1";
		public final static String HYDRA_THESIS_DETAIL = HYDRA_BASE + "index.php?&m=itdep-ptyxstud&s=all&lst";
		public final static String HYDRA_THESIS_DOWNLOAD = HYDRA_BASE + "index.php?m=kernelgenfileview&sdid=";
		// Pithia
		public final static String PITHIA_BASE = "http://pithia.teithe.gr/unistudent/";
		public final static String PITHIA_GRADES = PITHIA_BASE + "stud_CResults.asp";
		public final static String PITHIA_INFO = PITHIA_BASE + "studentMain.asp";
		public final static String PITHIA_LOGIN = PITHIA_BASE + "login.asp";
		public final static String PITHIA_REQUESTS = PITHIA_BASE + "stud_reqStatus.asp";
		// WebMail
		public final static String MAIL_BASE = "https://webmail.it.teithe.gr";
		public final static String MAIL_LOGIN = "https://webmail.it.teithe.gr/imp/redirect.php";
		public final static String MAIL_INFO = "https://webmail.it.teithe.gr/services/prefs.php?group=identities";
		public final static String MAIL_SEND = "https://webmail.it.teithe.gr/imp/compose.php";
		public final static String MAIL_FOLDER = "https://webmail.it.teithe.gr/imp/message.php?mailbox=";
		public final static String MAIL_FOLDER_WITH_PAGE = "https://webmail.it.teithe.gr/imp/mailbox.php?mailbox=%s&page=%s";
		public final static String MAIL_FORWARD = "https://webmail.it.teithe.gr/imp/compose" +
				".php?actionID=forward_all&index=%s&thismailbox=%s";

		// Other
		public final static String OASTH = "http://m.oasth.gr/#index.php?md=3&sn=2&line=99";
		public final static String TIMETABLE = "https://docs.google.com/uc?export=download&id=0B1n5lOTpMYpDVjU1MEdheThtblU";

		private URLS() {
		}
	}
}
