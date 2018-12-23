package com.nkyrim.itapp.services;

import android.app.IntentService;
import android.content.Intent;

import com.nkyrim.itapp.ItNet;
import com.nkyrim.itapp.domain.mail.ForwardMail;
import com.nkyrim.itapp.domain.mail.SendMail;

import pocketknife.BindExtra;
import pocketknife.PocketKnife;

/**
 * Service for sending mails. The usage of a service is necessary to guarantee the uninterrupted sending in case of an exit from the app.
 */
public class MailSendService extends IntentService {
	public  static final String EXTRA_SEND_MAIL = "EXTRA_SEND_MAIL";
	public  static final String EXTRA_FORWARD_MAIL = "EXTRA_FORWARD_MAIL";

	public MailSendService() {
		super("MailSendService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		SendMail	smail = (SendMail) intent.getExtras().getSerializable(EXTRA_SEND_MAIL);
		ForwardMail	fmail = (ForwardMail) intent.getExtras().getSerializable(EXTRA_FORWARD_MAIL);

		if(smail == null) return;
		if(fmail == null) ItNet.sendMail(smail);
		else ItNet.forwardMail(smail, fmail);
	}

}
