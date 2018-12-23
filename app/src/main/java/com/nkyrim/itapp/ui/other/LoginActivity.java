package com.nkyrim.itapp.ui.other;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.nkyrim.itapp.ItNet;
import com.nkyrim.itapp.R;
import com.nkyrim.itapp.settings.Key;
import com.nkyrim.itapp.settings.Settings;
import com.nkyrim.itapp.ui.info.HelpActivity;
import com.nkyrim.itapp.ui.util.TaskResult;
import com.nkyrim.itapp.ui.util.base.BaseActivity;
import com.nkyrim.itapp.ui.util.base.BaseLoader;
import com.nkyrim.itapp.util.Cons;
import com.nkyrim.itapp.util.Util;

import pocketknife.BindExtra;
import pocketknife.SaveState;

import butterknife.BindView;

public class LoginActivity extends BaseActivity {
	public final static int LOGIN_SUCCESS = 2;
	private final static int LID_LOGIN = 0;

	@BindView(R.id.prg) View prog;
	@BindView(R.id.form) View form;
	@BindView(R.id.til_username) TextInputLayout tilUser;
	@BindView(R.id.til_password) TextInputLayout tilPsw;
	@BindView(R.id.et_username) EditText etUser;
	@BindView(R.id.et_password) EditText etPsw;
	@BindView(R.id.error) TextView error;
	@BindView(R.id.tv_msg) TextView msg;

	@SaveState String password;
	@SaveState String username;
	@BindExtra @SaveState int accountOption;

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_login;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.action_help) {
			Intent i = new Intent(this, HelpActivity.class);
			i.putExtra(HelpActivity.ARG_ANCHOR, "passwords");
			startActivity(i);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// setup UI
		String name = "";
		if(accountOption == Cons.ACCOUNT_OPTION_HYDRA) {
			tilUser.setHint(getString(R.string.am));
			getSupportActionBar().setTitle(R.string.account_hydra);
			name = Settings.getString(Key.HYDRA_NAME, "");
		} else if(accountOption == Cons.ACCOUNT_OPTION_AETOS) {
			name = Settings.getString(Key.AETOS_NAME, "");
			getSupportActionBar().setTitle(R.string.account_aetos);
		} else if(accountOption == Cons.ACCOUNT_OPTION_PITHIA) {
			name = Settings.getString(Key.PITHIA_NAME, "");
			getSupportActionBar().setTitle(R.string.account_pithia);
		} else {
			throw new RuntimeException("accountOption value invalid");
		}
		if(name.isEmpty()) msg.setVisibility(View.GONE);
		else msg.setText(getString(R.string.logged_as, name));
		etPsw.setOnEditorActionListener((v, actionId, event) -> {
			// actionId set to 6 in the xml layout
			if(actionId == EditorInfo.IME_ACTION_DONE) {
				login(null);
				return true;
			}
			return false;
		});

		if(getSupportLoaderManager().getLoader(LID_LOGIN) != null) initLogin();
	}

	private void showProgress(boolean show) {
		error.setVisibility(View.GONE);
		prog.setVisibility(show ? View.VISIBLE : View.GONE);
		form.setVisibility(show ? View.GONE : View.VISIBLE);
	}

	private void showError(String message) {
		error.setVisibility(View.VISIBLE);
		error.setText(message);
	}

	public void login(View view) {
		String username = etUser.getText().toString();
		String password = etPsw.getText().toString();

		// Validate input
		if(username.isEmpty()) {
			tilUser.setError(getString(R.string.error_field_required));
			tilUser.requestFocus();
			return;
		}
		if(password.isEmpty()) {
			tilPsw.setError(getString(R.string.error_field_required));
			tilPsw.requestFocus();
			return;
		}

		tilUser.setError(null);
		tilPsw.setError(null);

		this.username = username;
		this.password = password;

		initLogin();
	}

	private void initLogin() {
		showProgress(true);
		getSupportLoaderManager().initLoader(LID_LOGIN, null, new LoginLoaderCallbacks());
	}

	private class LoginLoaderCallbacks implements LoaderManager.LoaderCallbacks<TaskResult<String>> {

		@Override
		public Loader<TaskResult<String>> onCreateLoader(int id, Bundle args) {
			return new LoginLoader(LoginActivity.this, username, password, accountOption);
		}

		@Override
		public void onLoadFinished(Loader<TaskResult<String>> loader, TaskResult<String> result) {
			getSupportLoaderManager().destroyLoader(LID_LOGIN);
			showProgress(false);
			if(result.isSuccessful()) {
				switch (accountOption) {
					case Cons.ACCOUNT_OPTION_AETOS:
						Settings.setString(Key.AETOS_NAME, result.getResult());
						Settings.setString(Key.AETOS_USERNAME, username);
						Settings.setString(Key.AETOS_PASSWORD, password);
						break;
					case Cons.ACCOUNT_OPTION_HYDRA:
						Settings.setString(Key.HYDRA_NAME, result.getResult());
						Settings.setString(Key.HYDRA_USERNAME, username);
						Settings.setString(Key.HYDRA_PASSWORD, password);
						break;
					case Cons.ACCOUNT_OPTION_PITHIA:
						Settings.setString(Key.PITHIA_NAME, result.getResult());
						Settings.setString(Key.PITHIA_USERNAME, username);
						Settings.setString(Key.PITHIA_PASSWORD, password);
						break;
				}

				Util.showToast(LoginActivity.this, getString(R.string.logged_as, result.getResult()));
				setResult(LOGIN_SUCCESS);
				finish();
			} else {
				showError(result.getMessage());
			}
		}

		@Override
		public void onLoaderReset(Loader loader) {
			// do nothing
		}

	}

	private static class LoginLoader extends BaseLoader<TaskResult<String>> {
		private int accountOption;
		private String password;
		private String username;

		public LoginLoader(Context context, String username, String password, int accountOption) {
			super(context);
			this.username = username;
			this.password = password;
			this.accountOption = accountOption;
		}

		@Override
		public TaskResult<String> loadInBackground() {
			switch (accountOption) {
				case Cons.ACCOUNT_OPTION_AETOS:
					return ItNet.loginAetos(username, password);
				case Cons.ACCOUNT_OPTION_HYDRA:
					return ItNet.loginHydra(username, password);
				case Cons.ACCOUNT_OPTION_PITHIA:
					return ItNet.loginPithia(username, password);
				default:
					throw new IllegalArgumentException("No account option set.");
			}
		}
	}
}
