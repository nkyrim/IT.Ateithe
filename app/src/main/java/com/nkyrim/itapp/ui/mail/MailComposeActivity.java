package com.nkyrim.itapp.ui.mail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.text.util.Rfc822Tokenizer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.ex.chips.BaseRecipientAdapter;
import com.android.ex.chips.RecipientEditTextView;
import com.android.ex.chips.recipientchip.DrawableRecipientChip;
import com.nkyrim.itapp.ItNet;
import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.mail.Email;
import com.nkyrim.itapp.domain.mail.ForwardMail;
import com.nkyrim.itapp.domain.mail.SendMail;
import com.nkyrim.itapp.ui.util.PocketKnifeIntents;
import com.nkyrim.itapp.ui.util.base.BaseActivity;
import com.nkyrim.itapp.ui.util.base.BaseLoader;
import com.nkyrim.itapp.ui.filepickers.FileSelectActivity;
import com.nkyrim.itapp.ui.util.BusEvents;
import com.nkyrim.itapp.ui.util.TaskResult;
import com.nkyrim.itapp.util.Cons;
import com.nkyrim.itapp.util.Logger;
import com.nkyrim.itapp.util.Util;

import pocketknife.BindExtra;
import pocketknife.SaveState;

import org.greenrobot.eventbus.Subscribe;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

public class MailComposeActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<ForwardMail> {
	private static final String ARG_NUM = "ARG_NUM";
	private static final String ARG_FOLDER = "ARG_FOLDER";
	private static final int LID_FORWARD = 0;

	// UI references
	@BindView(R.id.til_to) TextInputLayout til;
	@BindView(R.id.et_to) RecipientEditTextView etTo;
	@BindView(R.id.et_cc) RecipientEditTextView etCC;
	@BindView(R.id.et_bcc) RecipientEditTextView etBCC;
	@BindView(R.id.btn_cc) Button btnCC;
	@BindView(R.id.btn_bcc) Button btnBCC;
	@BindView(R.id.et_subject) EditText etSubject;
	@BindView(R.id.et_msg) EditText etMsg;
	@BindView(R.id.attContainer) LinearLayout attContainer;
	@BindView(R.id.prg) View prog;
	@BindView(R.id.container) View container;
	@BindView(R.id.cardForward) CardView cardForward;
	@BindView(R.id.tvForwardTitle) TextView tvForwardTitle;
	@BindView(R.id.tvForwardSender) TextView tvForwardSender;
	@BindView(R.id.tvForwardDate) TextView tvForwardDate;

	@SaveState ArrayList<String> attachments;
	@SaveState boolean showCC;
	@SaveState boolean showBCC;
	@SaveState ForwardMail fmail;

	@BindExtra Email reply;
	@BindExtra Email forward;

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_mail_compose;
	}

	@Override
	public void onResume() {
		super.onResume();
		EventBus.getDefault().register(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_mail_compose, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if(id == R.id.action_add_attachment) {
			Intent i = new Intent(this, FileSelectActivity.class);
			startActivity(i);
			return true;
		} else if(id == R.id.action_send) {
			sendMail();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(savedInstanceState == null) {
			attachments = new ArrayList<>();

			if(reply != null) setupReply(reply);
			else if(forward != null) setupForward(forward);
		}

		if(getSupportLoaderManager().getLoader(LID_FORWARD) != null) {
			if(forward != null) setupForward(forward);
		}

		// Restore
		if(showCC) {
			btnCC.setVisibility(View.GONE);
			etCC.setVisibility(View.VISIBLE);
		}
		if(showBCC) {
			btnBCC.setVisibility(View.GONE);
			etBCC.setVisibility(View.VISIBLE);
		}
		if(fmail != null) {
			showForwardedMail(fmail);
		}
		if(attachments != null & !attachments.isEmpty()) {
			for (String s : attachments) {
				addAttachmentView(Uri.parse(s));
			}
		}

		etTo.setTokenizer(new Rfc822Tokenizer());
		etTo.setAdapter(new BaseRecipientAdapter(BaseRecipientAdapter.QUERY_TYPE_EMAIL, this));
	}

	private void setupForward(Email forward) {
		// show progress
		prog.setVisibility(View.VISIBLE);
		container.setVisibility(View.GONE);

		Bundle args = new Bundle();
		args.putInt(ARG_NUM, forward.getNum());
		args.putString(ARG_FOLDER, forward.getFolder());
		getSupportLoaderManager().initLoader(LID_FORWARD, args, this);
	}

	private void setupReply(Email mail) {
		String subject = (mail.getSubject() == null) ? "" : mail.getSubject();
		etSubject.setText(String.format("Re: %s", subject));
		etTo.setText(mail.getSender());

		if(mail.getText() != null) {
			String[] lines = mail.getText().split("\n");
			etMsg.append("\n\n");
			etMsg.append(getString(R.string.quoting) + " " + mail.getSender() + ":\n");
			for (String s : lines) {
				etMsg.append("> " + s + "\n");
			}
		} else {
			etMsg.append(getString(R.string.quoting) + " " + mail.getSender() + ":\n");
			// TODO add actual reply for html only cases
		}

		etMsg.setSelection(0);
		etMsg.requestFocus();
	}

	@SuppressWarnings("unused")
	@Subscribe
	public void onEventMainThread(BusEvents.FileSelectedEvent event) {
		if(event.uri != null) {
			if(!addAttachment(event.uri)) Util.showToast(this, R.string.file_too_big);
			else Util.showToast(this, R.string.file_added);
		}
		EventBus.getDefault().removeStickyEvent(event);
	}

	private boolean addAttachment(Uri file) {
		if(new File(file.getPath()).length() >= 2 * 1024 * 1024) return false;
		attachments.add(file.getPath());
		addAttachmentView(file);
		return true;
	}

	private void addAttachmentView(Uri file) {
		View v = View.inflate(this, R.layout.row_attachment, null);

		TextView t1 = (TextView) v.findViewById(R.id.tv_title);
		TextView t2 = (TextView) v.findViewById(R.id.tv_type);
		t1.setText(file.getLastPathSegment());
		t2.setText(Util.readableSize(new File(file.getPath()).length()));

		ImageButton btn = (ImageButton) v.findViewById(R.id.btn_cc);
		btn.setOnClickListener(view -> {
			View v1 = (View) view.getParent().getParent().getParent();
			int i = attContainer.indexOfChild(v1);
			attachments.remove(i - 1);
			attContainer.removeViewAt(i);
		});

		attContainer.addView(v);
	}

	public void showCC(View view) {
		showCC = true;
		etCC.setVisibility(View.VISIBLE);
		view.setVisibility(View.GONE);
	}

	public void showBCC(View view) {
		showBCC = true;
		etBCC.setVisibility(View.VISIBLE);
		view.setVisibility(View.GONE);
	}

	private void sendMail() {
		// Gather required info
		String to = "";
		String cc = "";
		String bcc = "";
		for (DrawableRecipientChip c : etTo.getSortedRecipients()) {
			to += c.getValue() + ";";
		}
		// if no chips, try bodyBytes text(needed in case of reply)
		if(to.isEmpty()) to = etTo.getText().toString();
		for (DrawableRecipientChip c : etCC.getSortedRecipients()) {
			cc += c.getValue() + ";";
		}
		for (DrawableRecipientChip c : etBCC.getSortedRecipients()) {
			bcc += c.getValue() + ";";
		}
		String subject = etSubject.getText().toString();
		String text = etMsg.getText().toString();

		// Verify
		if(TextUtils.isEmpty(etTo.getText())) {
			til.setError(getString(R.string.error_field_required));
			etTo.requestFocus();
			return;
		}

		if(etCC.getVisibility() == View.GONE) cc = "";
		if(etBCC.getVisibility() == View.GONE) bcc = "";

		final SendMail mail = new SendMail(to, cc, bcc, subject, text, attachments);

		Intent i = new PocketKnifeIntents(this).getMailSendService(mail, fmail);
		startService(i);

		Util.showToast(this, R.string.sending_mail);
		finish();
	}

	@Override
	public Loader<ForwardMail> onCreateLoader(int id, Bundle args) {
		int num = args.getInt(ARG_NUM);
		String folder = args.getString(ARG_FOLDER);
		return new ForwardMailLoader(this, num, folder);
	}

	@Override
	public void onLoadFinished(Loader<ForwardMail> loader, ForwardMail data) {
		getSupportLoaderManager().destroyLoader(LID_FORWARD);

		if(data != null) {
			fmail = data;
			showForwardedMail(fmail);
		} else {
			Util.showToast(this, R.string.msg_forward_mail_fail);
		}
		// hide progress
		prog.setVisibility(View.GONE);
		container.setVisibility(View.VISIBLE);
	}

	private void showForwardedMail(ForwardMail fmail) {
		cardForward.setVisibility(View.VISIBLE);
		tvForwardTitle.setText(fmail.getTitle());
		tvForwardSender.setText(fmail.getSender());
		tvForwardDate.setText(fmail.getDate());

		etSubject.setText(String.format("Fwd: %s", fmail.getTitle()));
		Util.showToast(this, R.string.msg_verify_forward);
	}

	@Override
	public void onLoaderReset(Loader<ForwardMail> loader) {
		fmail = null;
	}

	private static class ForwardMailLoader extends BaseLoader<ForwardMail> {
		private int num;
		private String folder;

		public ForwardMailLoader(Context context, int num, String folder) {
			super(context);
			this.num = num;
			this.folder = folder;
		}

		@Override
		public ForwardMail loadInBackground() {
			int pages = 1;

			// 1. find the number of pages first
			TaskResult<String> r = ItNet.retrieve(Cons.ACCOUNT_OPTION_AETOS, Cons.URLS.MAIL_FOLDER + folder);
			if(r.isSuccessful()) {
				Document doc = Jsoup.parse(r.getResult());
				Element maxPage = doc.select("bodyString > div > div:nth-child(2)").first(); // e.g "Page 1 of 5"

				if(maxPage != null && (maxPage.text().contains("Page") || maxPage.text().contains("Σελίδα"))) {
					String[] tokens = maxPage.text().split(" ");
					try {
						pages = Integer.parseInt(tokens[tokens.length - 1]);
					} catch (NumberFormatException ex) {
						Logger.e(TAG, "Error parsing mail number", ex);
						// do nothing
					}
				}

				for (int i = 1; i <= pages; i++) {
					ForwardMail fmail = findMail(folder, num, i);
					if(fmail != null) return fmail;
				}
			}

			return null;
		}

		private ForwardMail findMail(String folder, int num, int pageNum) {
			String url = String.format(Cons.URLS.MAIL_FOLDER_WITH_PAGE, folder, String.valueOf(pageNum));

			TaskResult<String> r = ItNet.retrieve(Cons.ACCOUNT_OPTION_AETOS, url);
			if(r.isSuccessful()) {
				Document doc = Jsoup.parse(r.getResult());
				Elements rows = doc.select("#messages > table > tbody > tr");

				// remove first row, column titles
				rows.remove(0);

				for (Element e : rows) {
					Elements tds = e.select("td");
					String number = tds.get(1).text();

					if(Integer.parseInt(number) == num) {
						Logger.i(TAG, number);

						String id = e.attr("id");
						id = id.replace("row", "").replace(folder, "");
						String title = tds.get(4).text();
						String date = tds.get(2).text();
						String sender = tds.get(3).text();

						return new ForwardMail(title, sender, date, id, folder);
					}
				}
			}

			return null;
		}
	}

}
