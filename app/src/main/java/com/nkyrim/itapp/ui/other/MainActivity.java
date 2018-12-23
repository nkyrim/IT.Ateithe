package com.nkyrim.itapp.ui.other;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.github.aakira.expandablelayout.ExpandableLayout;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.nkyrim.itapp.R;
import com.nkyrim.itapp.settings.Settings;
import com.nkyrim.itapp.ui.info.BachelorActivity;
import com.nkyrim.itapp.ui.info.ErasmusActivity;
import com.nkyrim.itapp.ui.info.MasterActivity;
import com.nkyrim.itapp.ui.util.base.BaseActivity;
import com.nkyrim.itapp.ui.filepickers.DownloadsActivity;
import com.nkyrim.itapp.ui.bulletins.BulletinActivity;
import com.nkyrim.itapp.ui.staff.StaffActivity;
import com.nkyrim.itapp.ui.thesis.ThesisActivity;
import com.nkyrim.itapp.ui.info.DepartmentActivity;
import com.nkyrim.itapp.ui.info.HelpActivity;
import com.nkyrim.itapp.ui.info.LinksActivity;
import com.nkyrim.itapp.ui.info.PhotoActivity;
import com.nkyrim.itapp.ui.info.SupportActivity;
import com.nkyrim.itapp.ui.mail.MailActivity;
import com.nkyrim.itapp.ui.grades.GradesActivity;
import com.nkyrim.itapp.ui.personalinfo.PersonalInfoActivity;
import com.nkyrim.itapp.ui.requests.RequestsActivity;
import com.nkyrim.itapp.ui.settings.SettingsActivity;
import com.nkyrim.itapp.ui.courseselection.CourseSelectionActivity;
import com.nkyrim.itapp.ui.map.MapActivity;
import com.nkyrim.itapp.ui.timetable.TimetableActivity;
import com.nkyrim.itapp.ui.timetable.TimetablePersonalActivity;

import java.util.List;

import butterknife.BindViews;

public class MainActivity extends BaseActivity {
	@BindViews({R.id.container_dep, R.id.container_sec, R.id.container_info, R.id.container_tools, R.id.container_studies})
	List<ExpandableLayout> containers;

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_main;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addDrawer(0);
		getSupportActionBar().setSubtitle(R.string.title_activity_main);
	}

	public void showDepContainer(View view) throws InterruptedException {
		toggleGroups(containers.get(0));
	}

	public void showSecContainer(View view) {
		toggleGroups(containers.get(1));
	}

	public void showInfoContainer(View view) {
		toggleGroups(containers.get(2));
	}

	public void showToolContainer(View view) {
		toggleGroups(containers.get(3));
	}

	public void showStudiesContainer(View view) {
		containers.get(4).toggle();
	}

	public void showStudiesBcs(View view) {
		Intent i = new Intent(this, BachelorActivity.class);
		startActivity(i);
	}

	public void showStudiesMcs(View view) {
		Intent i = new Intent(this, MasterActivity.class);
		startActivity(i);
	}

	public void showStudiesErasmus(View view) {
		Intent i = new Intent(this, ErasmusActivity.class);
		startActivity(i);
	}

	public void showBulletins(View view) {
		Intent i = new Intent(this, BulletinActivity.class);
		startActivity(i);
	}

	public void showTimetable(View view) {
		Intent i = new Intent(this, TimetableActivity.class);
		startActivity(i);
	}

	public void showThesis(View view) {
		Intent i = new Intent(this, ThesisActivity.class);
		startActivity(i);
	}

	public void showStaff(View view) {
		Intent i = new Intent(this, StaffActivity.class);
		startActivity(i);
	}

	public void showPersonalInfo(View view) {
		Intent i = new Intent(this, PersonalInfoActivity.class);
		startActivity(i);
	}

	public void showGrades(View view) {
		Intent i = new Intent(this, GradesActivity.class);
		startActivity(i);
	}

	public void showRequests(View view) {
		Intent i = new Intent(this, RequestsActivity.class);
		startActivity(i);
	}

	public void showBus(View view) {
		Intent i = new Intent(this, BusLineActivity.class);
		startActivity(i);
	}

	public void showMap(View view) {
		Intent i = new Intent(this, MapActivity.class);
		startActivity(i);
	}

	public void showDep(View view) {
		Intent i = new Intent(this, DepartmentActivity.class);
		startActivity(i);
	}

	public void showSupport(View view) {
		Intent i = new Intent(this, SupportActivity.class);
		startActivity(i);
	}

	public void showPhotos(View view) {
		Intent i = new Intent(this, PhotoActivity.class);
		startActivity(i);
	}

	public void showLinks(View view) {
		Intent i = new Intent(this, LinksActivity.class);
		startActivity(i);
	}

	private void toggleGroups(ExpandableLayout a) {
		if(a.isExpanded()) a.collapse();
		else a.expand();
		for (ExpandableLayout c : containers) if(c != a) c.collapse();
	}

	private void addDrawer(final int position) {
		new DrawerBuilder()
				.withActivity(this)
				.withToolbar(toolbar)
				.withActionBarDrawerToggle(true)
				.withActionBarDrawerToggleAnimated(true)
				.withSelectedItem(position)
				.withAccountHeader(new AccountHeaderBuilder()
										   .withActivity(this)
										   .withHeaderBackground(R.drawable.ic_drawer_header).build())
				.withHeaderDivider(true)
				.addDrawerItems(
						new PrimaryDrawerItem().withName(R.string.home)
											   .withIcon(R.drawable.ic_home)
											   .withIconTintingEnabled(true)
											   .withOnDrawerItemClickListener((view, pos, drawerItem) -> false),
						new PrimaryDrawerItem().withName(R.string.mail)
											   .withIcon(R.drawable.ic_mail)
											   .withIconTintingEnabled(true)
											   .withOnDrawerItemClickListener((view, pos, drawerItem) -> {
												   Intent i = new Intent(MainActivity.this, MailActivity.class);
												   startActivity(i);
												   return false;
											   }),
						new PrimaryDrawerItem().withName(R.string.downloads)
											   .withIcon(R.drawable.ic_download)
											   .withIconTintingEnabled(true)
											   .withOnDrawerItemClickListener((view, pos, drawerItem) -> {
												   Intent i = new Intent(MainActivity.this, DownloadsActivity.class);
												   startActivity(i);
												   return false;
											   }),
						new PrimaryDrawerItem().withName(R.string.my_timetable)
											   .withIcon(R.drawable.ic_timetable)
											   .withIconTintingEnabled(true)
											   .withOnDrawerItemClickListener((view, pos, drawerItem) -> {
												   if(Settings.getSelectedCourses().isEmpty()) {
													   new AlertDialog.Builder(this)
															   .setTitle(R.string.courses_select_title)
															   .setMessage(R.string.courses_select_content)
															   .setPositiveButton(R.string.courses_select_positive, (dialog, which) -> {
																   Intent intent = new Intent(MainActivity.this, CourseSelectionActivity
																		   .class);
																   startActivity(intent);
															   })
															   .setNegativeButton(R.string.courses_select_negative, (dialog, b) -> {
																   dialog.dismiss();
															   })
															   .show();
												   } else {
													   Intent i = new Intent(MainActivity.this, TimetablePersonalActivity.class);
													   startActivity(i);
												   }
												   return false;
											   }),
						new DividerDrawerItem(),
						new SecondaryDrawerItem().withName(R.string.settings)
												 .withIcon(R.drawable.ic_settings)
												 .withIconTintingEnabled(true)
												 .withOnDrawerItemClickListener((view, pos, drawerItem) -> {
													 Intent i = new Intent(MainActivity.this, SettingsActivity.class);
													 startActivity(i);
													 return false;
												 }),
						new SecondaryDrawerItem().withName(R.string.help)
												 .withIcon(R.drawable.ic_help)
												 .withIconTintingEnabled(true)
												 .withOnDrawerItemClickListener((view, pos, drawerItem) -> {
													 Intent i = new Intent(MainActivity.this, HelpActivity.class);
													 startActivity(i);
													 return false;
												 }),
						new SecondaryDrawerItem().withName(R.string.about)
												 .withIcon(R.drawable.ic_info)
												 .withIconTintingEnabled(true)
												 .withOnDrawerItemClickListener((view, pos, drawerItem) -> {
													 Intent i = new Intent(this, AboutActivity.class);
													 startActivity(i);
													 return false;
												 })
				).build();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(false);
	}

}
