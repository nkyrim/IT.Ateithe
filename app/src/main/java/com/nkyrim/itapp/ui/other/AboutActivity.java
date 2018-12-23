package com.nkyrim.itapp.ui.other;

import android.app.FragmentManager;
import android.os.Bundle;

import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.ui.LibsFragment;
import com.mikepenz.aboutlibraries.ui.LibsSupportFragment;
import com.nkyrim.itapp.R;
import com.nkyrim.itapp.ui.util.base.BaseActivity;

public class AboutActivity extends BaseActivity {

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_about;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LibsSupportFragment fragment = new LibsBuilder()
				.withFields(R.string.class.getFields())
				.withAboutIconShown(true)
				.withVersionShown(true)
				.withLicenseShown(true)
				.withAboutVersionShownName(true)
				.supportFragment();

		getSupportFragmentManager().beginTransaction().add(R.id.fragment, fragment).commit();
		getSupportFragmentManager().executePendingTransactions();
	}
}
