package com.nkyrim.itapp.ui.personalinfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nkyrim.itapp.ItNet;
import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.student.PersonalInfo;
import com.nkyrim.itapp.ui.other.LoginActivity;
import com.nkyrim.itapp.ui.util.PocketKnifeIntents;
import com.nkyrim.itapp.ui.util.base.BaseActivity;
import com.nkyrim.itapp.ui.util.TaskResult;
import com.nkyrim.itapp.util.Cons;
import pocketknife.SaveState;

import butterknife.BindView;
import butterknife.ButterKnife;
import tr.xip.errorview.ErrorView;

public class PersonalInfoActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<TaskResult<PersonalInfo>> {
	private final static int LID_INFO = 0;
	private static final int LOGIN_CODE = 0;

	@BindView(R.id.error) ErrorView error;
	@BindView(R.id.prg) ProgressBar prog;
	@BindView(R.id.container1) View content;
	@BindView(R.id.tvLastName) TextView tvLastName;
	@BindView(R.id.tvFirstName) TextView tvFirstName;
	@BindView(R.id.tv_am) TextView tvAM;
	@BindView(R.id.tvDept) TextView tvDept;
	@BindView(R.id.tvSem) TextView tvSem;
	@BindView(R.id.tvProg) TextView tvProg;
	@BindView(R.id.tvRate) TextView tvRate;
	@BindView(R.id.tvAcadYear) TextView tvYear;
	@BindView(R.id.tvPeriod) TextView tvPeriod;
	@BindView(R.id.tvRegSem) TextView tvRegSem;
	@BindView(R.id.tvRegMode) TextView tvRegMode;
	@BindView(R.id.tvAddress) TextView tvAddress1;
	@BindView(R.id.tvZip) TextView tvZip1;
	@BindView(R.id.tvCity) TextView tvCity1;
	@BindView(R.id.tvCountry) TextView tvCountry1;
	@BindView(R.id.tvAddress2) TextView tvAddress2;
	@BindView(R.id.tvZip2) TextView tvZip2;
	@BindView(R.id.tvCity2) TextView tvCity2;
	@BindView(R.id.tvCountry2) TextView tvCountry2;
	@BindView(R.id.tvPhone1) TextView tvPhone1;
	@BindView(R.id.tvPhone2) TextView tvPhone2;
	@BindView(R.id.tv_mail) TextView tvEmail;

	@SaveState PersonalInfo info;

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_personal_info;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ButterKnife.bind(this);
		error.setOnRetryListener(this::getInfo);

		// check if account stored else start login activity
		if(!ItNet.isAccountStored(Cons.ACCOUNT_OPTION_PITHIA) && savedInstanceState == null) {
			Intent i = new PocketKnifeIntents(this).getLoginActivity(Cons.ACCOUNT_OPTION_PITHIA);
			startActivityForResult(i, LOGIN_CODE);
		} else {
			if(info == null) getInfo();
			else showContent(info);
		}
	}

	private void getInfo() {
		showProgress(true);
		getSupportLoaderManager().initLoader(LID_INFO, null, this);
	}

	private void showContent(PersonalInfo info) {
		this.info = info;
		tvLastName.setText(info.getLastName());
		tvFirstName.setText(info.getFirstName());
		tvAM.setText(info.getAm());
		tvDept.setText(info.getDepartment());
		tvSem.setText(info.getSemester());
		tvProg.setText(info.getProgram());
		tvRate.setText(info.getRate());
		tvYear.setText(info.getYear());
		tvPeriod.setText(info.getPeriod());
		tvRegSem.setText(info.getRegSemester());
		tvRegMode.setText(info.getRegMode());
		tvAddress1.setText(info.getP_address());
		tvZip1.setText(info.getP_zip());
		tvCity1.setText(info.getP_city());
		tvCountry1.setText(info.getP_country());
		tvAddress2.setText(info.getC_address());
		tvZip2.setText(info.getC_zip());
		tvCity2.setText(info.getC_city());
		tvCountry2.setText(info.getC_country());
		tvPhone1.setText(info.getPhone1());
		tvPhone2.setText(info.getPhone2());
		tvEmail.setText(info.getEmail());

		showProgress(false);
	}

	private void showProgress(boolean show) {
		error.setVisibility(View.GONE);
		prog.setVisibility(show ? View.VISIBLE : View.GONE);
		content.setVisibility(show ? View.GONE : View.VISIBLE);
	}

	private void showEmpty(String msg) {
		error.setSubtitle(msg);
		error.setVisibility(View.VISIBLE);
		prog.setVisibility(View.GONE);
		content.setVisibility(View.GONE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == LoginActivity.LOGIN_SUCCESS) getInfo();
		else finish();
	}

	@Override
	public Loader<TaskResult<PersonalInfo>> onCreateLoader(int id, Bundle args) {
		return new PersonalInfoLoader(PersonalInfoActivity.this);
	}

	@Override
	public void onLoadFinished(Loader<TaskResult<PersonalInfo>> loader, TaskResult<PersonalInfo> result) {
		getSupportLoaderManager().destroyLoader(LID_INFO);

		if(result.isSuccessful()) showContent(result.getResult());
		else showEmpty(result.getMessage());
	}

	@Override
	public void onLoaderReset(Loader<TaskResult<PersonalInfo>> loader) {
		info = null;
	}

}
