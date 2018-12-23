package com.nkyrim.itapp.ui.mail;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.mail.Email;
import com.nkyrim.itapp.services.DownloadMailAttachmentService;
import com.nkyrim.itapp.settings.Key;
import com.nkyrim.itapp.settings.Settings;
import com.nkyrim.itapp.ui.util.PocketKnifeIntents;
import com.nkyrim.itapp.ui.util.base.BaseFragment;
import com.nkyrim.itapp.ui.util.BusEvents;
import com.nkyrim.itapp.util.Util;
import pocketknife.SaveState;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import org.greenrobot.eventbus.EventBus;

public class MailDetailFragment extends BaseFragment {
	private static final int LID_MAILDETAIL = 0;
	private static final int LID_MOVE = 1;
	private static final int LID_FOLDERS = 2;

	@BindView(R.id.tv_subject) TextView tvSubject;
	@BindView(R.id.tv_sender) TextView tvSender;
	@BindView(R.id.tv_date) TextView tvDate;
	@BindView(R.id.tv_msg) TextView tvText;
	@BindView(R.id.web) WebView web;
	@BindView(R.id.prg) ProgressBar prog;
	@BindView(R.id.fab) FloatingActionsMenu fam;

	@SaveState ArrayList<String> folders;
	@SaveState Email mail;
	@SaveState boolean showHtml;
	@SaveState boolean isDelete;
	private ArrayList<FloatingActionButton> fabs = new ArrayList<>();

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_mail_detail, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// no mail is displayed, do nothing
		if(mail == null) return true;

		int id = item.getItemId();

		if(id == R.id.action_reply) return reply();
		else if(id == R.id.action_forward) return forward();
		else if(id == R.id.action_move) return moveTo();
		else if(id == R.id.action_delete) return deleteMail();
		else if(id == R.id.action_show_html) return showHTML();
		else return super.onOptionsItemSelected(item);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mail_detail, container, false);
		bindView(view);
		setHasOptionsMenu(true);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if(savedInstanceState != null) {
			// reconnect to move/delete loader if it exists
			if(getLoaderManager().getLoader(LID_MOVE) != null) {
				getLoaderManager().initLoader(LID_MOVE, null, new MoveMailLoaderCallbacks());
			}
		}

		// load folders
		getLoaderManager().initLoader(LID_FOLDERS, null, new FolderLoaderCallbacks());

		if(mail != null) showContent(mail);
	}

	public void showContent(Email email) {
		this.mail = email;

		showProgress(true);
		if(mail.isComplete()) {
			// clear view from possible previous mail
			clear();

			String subject = (mail.getSubject() == null) ? Util.getString(R.string.no_subject) : mail.getSubject();
			tvSubject.setText(subject);
			tvSender.setText(mail.getSender());

			DateFormat df = new SimpleDateFormat("EEEE, dd MMM yyyy HH:mm:ss", Locale.getDefault());
			df.setTimeZone(TimeZone.getTimeZone("EET"));
			tvDate.setText(df.format(mail.getDate()));

			if(!TextUtils.isEmpty(mail.getText())) {
				tvText.setText(mail.getText());
				web.setVisibility(View.GONE); // in case of previous mail in 2pane
			} else if(mail.getHtml() != null) {
				web.loadDataWithBaseURL("http://dummy", mail.getHtml() + "<br/><br/><br/><br/><br/>", "text/html", "utf-8", "");
				tvText.setVisibility(View.GONE);
				web.setVisibility(View.VISIBLE);
				showHtml = true;
			} else {
				tvText.setText(getString(R.string.invalid_mail));
			}

			if(mail.hasAttachments()) {
				for (final String s : mail.getAttachments()) {
					FloatingActionButton fab = new FloatingActionButton(getActivity());
					fab.setTitle(s);
					fab.setImageResource(R.drawable.ic_download);
					fab.setSize(FloatingActionButton.SIZE_MINI);
					fab.setColorNormalResId(R.color.light_cyan);
					fab.setColorPressedResId(R.color.dark_cyan);
					fab.setOnClickListener(v -> downloadAttachment(s));
					fabs.add(fab);
					fam.addButton(fab);
				}

				fam.setVisibility(View.VISIBLE);
			}

			showProgress(false);
		} else {
			Bundle args = new Bundle();
			args.putSerializable("mail", mail);
			getLoaderManager().initLoader(LID_MAILDETAIL, args, new MailDetailLoaderCallbacks());
		}
	}

	private void showProgress(boolean show) {
		prog.setVisibility(show ? View.VISIBLE : View.GONE);
		tvText.setVisibility(show ? View.GONE : View.VISIBLE);
	}

	private void clear() {
		tvDate.setText(null);
		tvSender.setText(null);
		tvSubject.setText(null);
		web.loadUrl("about:blank");
		web.clearCache(true);
		web.setVisibility(View.GONE);
		tvText.setText(null);
		tvText.setVisibility(View.VISIBLE);
		showHtml = false;

		// clear fam
		for (FloatingActionButton f : fabs) {
			fam.removeButton(f);
			fam.collapse();
		}
		fabs.clear();
		fam.setVisibility(View.GONE);
	}

	//=========================== Mail actions =================================
	private boolean reply() {
		if(getLoaderManager().hasRunningLoaders()) return true;
		Intent i = new PocketKnifeIntents(getActivity()).getMailComposeActivity(mail, null);
		startActivity(i);
		return true;
	}

	private boolean forward() {
		Intent i = new PocketKnifeIntents(getActivity()).getMailComposeActivity(null, mail);
		startActivity(i);
		return true;
	}

	private boolean moveTo() {
		// if already moving/deleting
		if(getLoaderManager().getLoader(LID_MOVE) != null) {
			Util.showToast(getActivity(), R.string.delete_move_in_progress);
			return true;
		}

		// show move/delete confirmation and notify user about the differences with webmail if it isn't disabled
		if(Settings.getBoolean(Key.MAIL_DELETE_CONFIRM_DISABLED, false)) {
			new AlertDialog.Builder(getActivity())
					.setTitle(R.string.select_folder)
					.setItems(folders.toArray(new String[folders.size()]), (dialog, which) -> {
						Bundle args = new Bundle();
						args.putSerializable("mail", mail);
						args.putString("folder", folders.get(which));
						args.putBoolean("delete", false);
						getLoaderManager().initLoader(LID_MOVE, args, new MoveMailLoaderCallbacks());
					}).create().show();
		} else {
			View v = getActivity().getLayoutInflater().inflate(R.layout.warning_mail_delete, null);
			final CheckBox cbox = (CheckBox) v.findViewById(R.id.cbox1);
			AlertDialog d = new AlertDialog.Builder(getActivity())
					.setTitle(R.string.warning)
					.setView(v)
					.setPositiveButton(R.string.continue_, (dialog, which) -> {
						if(cbox.isChecked()) Settings.setBoolean(Key.MAIL_DELETE_CONFIRM_DISABLED, true);

						// show available folders to move the mail to
						new AlertDialog.Builder(getActivity())
								.setTitle(R.string.select_folder)
								.setItems(folders.toArray(new String[folders.size()]), (dialog1, which1) -> {
									Bundle args = new Bundle();
									args.putSerializable("mail", mail);
									args.putString("folder", folders.get(which1));
									args.putBoolean("delete", false);
									getLoaderManager().initLoader(LID_MOVE, args, new MoveMailLoaderCallbacks());
								}).create().show();
					})
					.setNegativeButton(R.string.cancel, (dialog, which) -> {
						dialog.dismiss();
					}).create();

			d.show();
		}

		return true;
	}

	private boolean deleteMail() {
		// if already moving/deleting
		if(getLoaderManager().getLoader(LID_MOVE) != null) {
			Util.showToast(getActivity(), R.string.delete_move_in_progress);
			return true;
		}

		// show move/delete confirmation and notify user about the differences with webmail if it isn't disabled
		if(Settings.getBoolean(Key.MAIL_DELETE_CONFIRM_DISABLED, false)) {
			// show permanent delete confirmation if we are deleting a mail from the trash
			if(mail.getFolder().equals("Trash")) {
				new AlertDialog.Builder(getActivity())
						.setTitle(R.string.permanent_delete).setMessage(R.string.permanent_delete_msg)
						.setPositiveButton(R.string.delete, (dialog, which) -> {
							Bundle args = new Bundle();
							args.putSerializable("mail", mail);
							args.putString("folder", "Trash");
							args.putBoolean("delete", true);
							getLoaderManager().initLoader(LID_MOVE, args, new MoveMailLoaderCallbacks());
						}).setNegativeButton(R.string.cancel, (dialog, which) -> {
							dialog.dismiss();
						}).create().show();
			} else {
				Bundle args = new Bundle();
				args.putSerializable("mail", mail);
				args.putString("folder", "Trash");
				args.putBoolean("delete", true);
				getLoaderManager().initLoader(LID_MOVE, args, new MoveMailLoaderCallbacks());
			}
		} else {
			View v = getActivity().getLayoutInflater().inflate(R.layout.warning_mail_delete, null);
			final CheckBox cbox = (CheckBox) v.findViewById(R.id.cbox1);
			new AlertDialog.Builder(getActivity())
					.setTitle(R.string.warning)
					.setView(v)
					.setPositiveButton(R.string.continue_, (dialog, which) -> {
						if(cbox.isChecked()) Settings.setBoolean(Key.MAIL_DELETE_CONFIRM_DISABLED, true);

						Bundle args = new Bundle();
						args.putSerializable("mail", mail);
						args.putString("folder", "Trash");
						args.putBoolean("delete", true);
						getLoaderManager().initLoader(LID_MOVE, args, new MoveMailLoaderCallbacks());
					})
					.setNegativeButton(R.string.cancel, (dialog, which) -> {
						dialog.dismiss();
					}).create().show();
		}

		return true;
	}

	private boolean showHTML() {
		if(getLoaderManager().hasRunningLoaders()) return true;
		showHtml = !showHtml;
		if(showHtml) {
			if(mail.getHtml() != null) {
				web.loadDataWithBaseURL("http://dummy", mail.getHtml() + "<br/><br/><br/><br/><br/>", "text/html", "utf-8", "");
				tvText.setVisibility(View.GONE);
				web.setVisibility(View.VISIBLE);
			}
		} else {
			web.loadUrl("about:blank");
			tvText.setVisibility(View.VISIBLE);
			web.setVisibility(View.GONE);
		}

		return true;
	}

	private void downloadAttachment(String attachment) {
		Intent i = new PocketKnifeIntents(getActivity()).getDownloadMailAttachmentService(mail.getNum(),attachment, mail.getFolder());
		getActivity().startService(i);
	}

	//========================== Loader Callbacks ==============================
	private class MoveMailLoaderCallbacks implements LoaderManager.LoaderCallbacks<Boolean> {

		@Override
		public Loader<Boolean> onCreateLoader(int id, Bundle args) {
			String folder = args.getString("folder");
			Email mail = (Email) args.getSerializable("mail");
			isDelete = args.getBoolean("delete");
			return new MailMoveLoader(getActivity(), mail, folder);
		}

		@Override
		public void onLoadFinished(Loader<Boolean> loader, Boolean success) {
			getLoaderManager().destroyLoader(LID_MOVE);
			if(success) {
				EventBus.getDefault().postSticky(new BusEvents.MailMoveEvent(true));
				if(isDelete) Util.showToast(getActivity(), R.string.mail_delete_success);
				else Util.showToast(getActivity(), R.string.mail_move_success);
				// clear view and possibly close activity(we are in 1pane mode and the event is consumed)
				mail = null;
				clear();
				EventBus.getDefault().post(new BusEvents.CloseActivityEvent(true));
			} else Util.showToast(getActivity(), R.string.error_general);
		}

		@Override
		public void onLoaderReset(Loader<Boolean> loader) {
			// do nothing
		}
	}

	private class MailDetailLoaderCallbacks implements LoaderManager.LoaderCallbacks<Email> {

		@Override
		public Loader<Email> onCreateLoader(int id, Bundle args) {
			return new MailDetailLoader(getActivity(), mail);
		}

		@Override
		public void onLoadFinished(Loader<Email> loader, Email mail) {
			getLoaderManager().destroyLoader(LID_MAILDETAIL);
			if(mail != null) showContent(mail);
			else Util.showToast(getActivity(), R.string.error_general);
		}

		@Override
		public void onLoaderReset(Loader<Email> loader) {
			mail = null;
		}
	}

	public class FolderLoaderCallbacks implements LoaderManager.LoaderCallbacks<ArrayList<String>> {

		@Override
		public Loader<ArrayList<String>> onCreateLoader(int i, Bundle args) {
			return new MailFolderLoader(getActivity());
		}

		@Override
		public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> data) {
			folders = data;
		}

		@Override
		public void onLoaderReset(Loader<ArrayList<String>> loader) {
			// Do nothing
		}

	}

}
