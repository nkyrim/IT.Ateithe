package com.nkyrim.itapp.ui.util;

import android.content.Intent;

import com.nkyrim.itapp.domain.Bulletin;
import com.nkyrim.itapp.domain.Staff;
import com.nkyrim.itapp.domain.Thesis;
import com.nkyrim.itapp.domain.mail.Email;
import com.nkyrim.itapp.domain.mail.ForwardMail;
import com.nkyrim.itapp.domain.mail.SendMail;
import com.nkyrim.itapp.services.DownloadHydraService;
import com.nkyrim.itapp.services.DownloadMailAttachmentService;
import com.nkyrim.itapp.services.DownloadPithiaService;
import com.nkyrim.itapp.services.MailSendService;
import com.nkyrim.itapp.ui.bulletins.BulletinDetailActivity;
import com.nkyrim.itapp.ui.info.HelpActivity;
import com.nkyrim.itapp.ui.mail.MailComposeActivity;
import com.nkyrim.itapp.ui.mail.MailDetailActivity;
import com.nkyrim.itapp.ui.other.LoginActivity;
import com.nkyrim.itapp.ui.staff.StaffDetailActivity;
import com.nkyrim.itapp.ui.thesis.ThesisDetailActivity;

import pocketknife.IntentBuilder;

public interface Intents {

	@IntentBuilder(cls=HelpActivity.class)
	Intent getHelpActivity(String section);

	@IntentBuilder(cls=LoginActivity.class)
	Intent getLoginActivity(int accountOption);

	@IntentBuilder(cls=StaffDetailActivity.class)
	Intent getStaffDetailActivity(Staff staff);

	@IntentBuilder(cls=BulletinDetailActivity.class)
	Intent getBulletinDetailActivity(Bulletin bulletin);

	@IntentBuilder(cls=ThesisDetailActivity.class)
	Intent getThesisDetailActivity(Thesis thesis);

	@IntentBuilder(cls=MailDetailActivity.class)
	Intent getMailDetailActivity(Email mail);

	@IntentBuilder(cls=MailComposeActivity.class)
	Intent getMailComposeActivity(Email reply, Email forward);

	@IntentBuilder(cls=DownloadHydraService.class)
	Intent getDownloadHydraService(String urlString);

	@IntentBuilder(cls=DownloadPithiaService.class)
	Intent getDownloadPithiaService(String urlString);

	@IntentBuilder(cls=DownloadMailAttachmentService.class)
	Intent getDownloadMailAttachmentService(int msgId, String filename, String folder);

	@IntentBuilder(cls=MailSendService.class)
	Intent getMailSendService(SendMail smail, ForwardMail fmail);


}