package com.nkyrim.itapp.ui.staff;

import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.Staff;
import com.nkyrim.itapp.ui.util.base.BaseFragment;
import com.nkyrim.itapp.ui.util.TaskResult;
import com.nkyrim.itapp.util.Logger;
import com.nkyrim.itapp.util.Util;
import pocketknife.SaveState;

import java.util.ArrayList;

import butterknife.BindView;

public class StaffDetailFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<TaskResult<Staff>> {
	private static final int LID_STAFF = 0;
	@BindView(R.id.tv_title) TextView tvTitle;
	@BindView(R.id.tv_lastname) TextView tvLastname;
	@BindView(R.id.tv_firstname) TextView tvFirstname;
	@BindView(R.id.tv_roles) TextView tvRoles;
	@BindView(R.id.tv_tel) TextView tvTel;
	@BindView(R.id.tv_mail) TextView tvEmail;
	@BindView(R.id.tv_teaches) TextView tvTeaches;
	@BindView(R.id.tv_webpage) TextView tvWebpage;
	@BindView(R.id.prg) ProgressBar progBar;
	@BindView(R.id.view_detail) View detailView;

	@SaveState Staff member;

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_staff_detail, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.action_add_contact) {
			if(tvFirstname.getText().toString().isEmpty()) return true;

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			String msg;
			if(TextUtils.isEmpty(tvTel.getText()) && TextUtils.isEmpty(tvEmail.getText())) {
				msg = getString(R.string.no_contact_elements);
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				builder.setTitle(R.string.warning);
			} else msg = getString(R.string.add_contact_msg);

			builder.setMessage(msg).setPositiveButton(R.string.ok, (dialog, which) -> {
				addContact();
			}).setNegativeButton(R.string.cancel, null);

			builder.create().show();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_staff_detail, container, false);
		bindView(view);
		setHasOptionsMenu(true);

		// If we are in 1pane layout don't show title
		if(!getResources().getBoolean(R.bool.has_two_panes)) {
			tvTitle.setVisibility(View.GONE);
		}

		// reconnect to loader if present
		if(getLoaderManager().getLoader(LID_STAFF) != null) {
			showProgress(true);
			getLoaderManager().initLoader(LID_STAFF, null, this);
		}

		return view;
	}

	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);
		if(member != null) showContent(member);
	}

	public void showContent(Staff staffMember) {
		showProgress(true);
		// just a way to see if data already retrieved
		if(staffMember.getEmail() != null) {
			setContent(staffMember);
		} else {
			Bundle args = new Bundle();
			args.putString("id", staffMember.getDetailId());
			getLoaderManager().initLoader(LID_STAFF, args, this);
		}
	}

	private void setContent(Staff staffMember) {
		member = staffMember;
		tvLastname.setText(member.getLastName());
		tvFirstname.setText(member.getFirstName());
		tvRoles.setText(member.getRole());
		tvTel.setText(member.getTel());
		tvEmail.setText(member.getEmail());
		tvTeaches.setText(member.getClasses());
		tvWebpage.setText(member.getWebpage());
		showProgress(false);
	}

	private void showProgress(boolean show) {
		progBar.setVisibility(show ? View.VISIBLE : View.GONE);
		detailView.setVisibility(show ? View.GONE : View.VISIBLE);
	}

	private void addContact() {
		String firstName = member.getFirstName();
		String lastName = member.getLastName();
		String[] workNumbers = member.getTel().split(",");
		String email = member.getEmail();
		String webpage = member.getWebpage();

		ArrayList<ContentProviderOperation> ops = new ArrayList<>();

		ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
										.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
										.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

		// Name
		if(firstName != null) {
			ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
											.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
											.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName
													.CONTENT_ITEM_TYPE)
											.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, firstName + " " +
													lastName)
											.build());
		}

		// Work Numbers
		if(workNumbers.length > 0) {
			for (String wn : workNumbers) {
				ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
												.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
												.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone
														.CONTENT_ITEM_TYPE)
												.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, wn)
												.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
														   ContactsContract.CommonDataKinds.Phone.TYPE_WORK).build());
			}
		}

		// Email
		if(email != null) {
			ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
											.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
											.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email
													.CONTENT_ITEM_TYPE)
											.withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
											.withValue(ContactsContract.CommonDataKinds.Email.TYPE,
													   ContactsContract.CommonDataKinds.Email.TYPE_WORK).build());
		}

		// Site
		if(webpage != null) {
			ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
											.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
											.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website
													.CONTENT_ITEM_TYPE)
											.withValue(ContactsContract.CommonDataKinds.Website.DATA, webpage)
											.withValue(ContactsContract.CommonDataKinds.Website.TYPE,
													   ContactsContract.CommonDataKinds.Website.TYPE_HOMEPAGE).build());
		}

		try {
			// Asking the Contact provider to create a new contact
			getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
			Util.showToast(getActivity(), R.string.add_contact_success);
		} catch (RemoteException | OperationApplicationException ex) {
			Logger.e(TAG, "Error adding contact", ex);
			Util.showToast(getActivity(), R.string.add_contact_fail);
		}
	}

	@Override
	public Loader<TaskResult<Staff>> onCreateLoader(int id, Bundle args) {
		String sid = args.getString("id");
		return new StaffDetailLoader(getActivity(), sid);
	}

	@Override
	public void onLoadFinished(Loader<TaskResult<Staff>> loader, TaskResult<Staff> result) {
		getLoaderManager().destroyLoader(LID_STAFF);

		if(result.isSuccessful()) setContent(result.getResult());
		else Util.showToast(getActivity(), result.getMessage());
	}

	@Override
	public void onLoaderReset(Loader<TaskResult<Staff>> loader) {
		member = null;
	}

}
