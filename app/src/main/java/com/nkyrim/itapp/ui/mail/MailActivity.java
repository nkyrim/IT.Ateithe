package com.nkyrim.itapp.ui.mail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuItem;

import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.mail.Email;
import com.nkyrim.itapp.ui.other.AboutActivity;
import com.nkyrim.itapp.ui.other.MainActivity;
import com.nkyrim.itapp.ui.util.Intents;
import com.nkyrim.itapp.ui.util.PocketKnifeIntents;
import com.nkyrim.itapp.ui.util.base.BaseActivity;
import com.nkyrim.itapp.ui.filepickers.DownloadsActivity;
import com.nkyrim.itapp.ui.info.HelpActivity;
import com.nkyrim.itapp.ui.settings.SettingsActivity;
import com.nkyrim.itapp.ui.timetable.TimetablePersonalActivity;
import com.nkyrim.itapp.ui.util.BusEvents;
import com.nkyrim.itapp.util.Util;
import pocketknife.SaveState;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Mail activity
 */
public class MailActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<ArrayList<String>> {
	private static final int LID_FOLDER = 0;

	private MailDetailFragment detail;
	private MailListFragment master;
	private Drawer drawer;

	@SaveState Email lastMail;
	@SaveState ArrayList<String> folders;
	private boolean twoPane;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_mail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if(id == R.id.action_compose) {
			Intent intent = new Intent(this, MailComposeActivity.class);
			startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		EventBus.getDefault().register(this);
	}

	@Override
	protected int getLayoutResource() {
		return R.layout.mail;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addMailDrawer();

		master = (MailListFragment) getSupportFragmentManager().findFragmentById(R.id.frag_master);
		detail = (MailDetailFragment) getSupportFragmentManager().findFragmentById(R.id.frag_detail);
		twoPane = getResources().getBoolean(R.bool.has_two_panes);

		getSupportLoaderManager().initLoader(LID_FOLDER, null, this);
	}

	@Override
	protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
		// set latest mail shown if we go to 2pane layout after orientation change
		if(twoPane && lastMail != null) {
			detail.showContent(lastMail);
		}
	}

	@Subscribe
	public void onEventMainThread(BusEvents.MailSelectedEvent event) {
		lastMail = event.mail;
		if(twoPane) {
			detail.showContent(event.mail);
		} else {
			Intent i = new PocketKnifeIntents(this).getMailDetailActivity(lastMail);
			startActivity(i);
		}
	}

	private void showFolders(ArrayList<String> folders) {
		// add default folders with proper names
		String[] properFolder = getResources().getStringArray(R.array.proper_mail_folders);
		String[] originalFolders = getResources().getStringArray(R.array.original_mail_folders);
		drawer.addItemAtPosition(new SecondaryDrawerItem()
										 .withName(properFolder[0])
										 .withIcon(R.drawable.ic_inbox)
										 .withIconTintingEnabled(true)
										 .withOnDrawerItemClickListener((view, pos, di) -> {
											 master.getContent(originalFolders[0]);
											 return false;
										 }), 4);
		drawer.addItemAtPosition(new SecondaryDrawerItem()
										 .withName(properFolder[1])
										 .withIcon(R.drawable.ic_send)
										 .withIconTintingEnabled(true)
										 .withOnDrawerItemClickListener((view, pos, di) -> {
											 master.getContent(originalFolders[1]);
											 return false;
										 }), 5);
		drawer.addItemAtPosition(new SecondaryDrawerItem()
										 .withName(properFolder[2])
										 .withIcon(R.drawable.ic_drafts)
										 .withIconTintingEnabled(true)
										 .withOnDrawerItemClickListener((view, pos, di) -> {
											 master.getContent(originalFolders[2]);
											 return false;
										 }), 6);
		drawer.addItemAtPosition(new SecondaryDrawerItem()
										 .withName(properFolder[3])
										 .withIcon(R.drawable.ic_trash)
										 .withIconTintingEnabled(true)
										 .withOnDrawerItemClickListener((view, pos, di) -> {
											 master.getContent(originalFolders[3]);
											 return false;
										 }), 7);

		// add the rest of the folders, the user created
		ArrayList<String> def = new ArrayList<>(Arrays.asList(originalFolders));
		for (String s : folders) {
			if(def.contains(s)) continue; // skip default folder
			drawer.addItemAtPosition(new SecondaryDrawerItem()
											 .withName(s)
											 .withIcon(R.drawable.ic_folder)
											 .withIconTintingEnabled(true)
											 .withOnDrawerItemClickListener((view, pos, di) -> {
												 master.getContent(s);
												 return false;
											 }), 8);
		}

		drawer.setSelectionAtPosition(4, false);
	}

	private void addMailDrawer() {
		drawer = new DrawerBuilder()
				.withActivity(this)
				.withToolbar(toolbar)
				.withActionBarDrawerToggle(true)
				.withActionBarDrawerToggleAnimated(true)
				.withAccountHeader(new AccountHeaderBuilder()
										   .withActivity(this)
										   .withHeaderBackground(R.drawable.ic_drawer_header).build())
				.addDrawerItems(
						new PrimaryDrawerItem().withName(R.string.home)
											   .withIcon(R.drawable.ic_home)
											   .withIconTintingEnabled(true)
											   .withOnDrawerItemClickListener((view, pos, drawerItem) -> {
												   Intent i = new Intent(MailActivity.this, MainActivity.class);
												   startActivity(i);
												   return false;
											   }),
						new PrimaryDrawerItem().withName(R.string.mail)
											   .withIcon(R.drawable.ic_mail)
											   .withIconTintingEnabled(true)
											   .withOnDrawerItemClickListener((view, pos, drawerItem) -> false),
						new SectionDrawerItem().withName(R.string.folders),
						new DividerDrawerItem(),
						new PrimaryDrawerItem().withName(R.string.downloads)
											   .withIcon(R.drawable.ic_download)
											   .withIconTintingEnabled(true)
											   .withOnDrawerItemClickListener((view, pos, drawerItem) -> {
												   Intent i = new Intent(MailActivity.this, DownloadsActivity.class);
												   startActivity(i);
												   return false;
											   })
						,
						new PrimaryDrawerItem().withName(R.string.my_timetable)
											   .withIcon(R.drawable.ic_timetable)
											   .withIconTintingEnabled(true)
											   .withOnDrawerItemClickListener((view, pos, drawerItem) -> {
												   Intent i = new Intent(MailActivity.this, TimetablePersonalActivity.class);
												   startActivity(i);
												   return false;
											   }),
						new DividerDrawerItem(),
						new SecondaryDrawerItem().withName(R.string.settings)
												 .withIcon(R.drawable.ic_settings)
												 .withIconTintingEnabled(true)
												 .withOnDrawerItemClickListener((view, pos, drawerItem) -> {
													 Intent i = new Intent(MailActivity.this, SettingsActivity.class);
													 startActivity(i);
													 return false;
												 }),
						new SecondaryDrawerItem().withName(R.string.help)
												 .withIcon(R.drawable.ic_help)
												 .withIconTintingEnabled(true)
												 .withOnDrawerItemClickListener((view, pos, drawerItem) -> {
													 Intent i = new PocketKnifeIntents(this).getHelpActivity("mail");
													 startActivity(i);
													 return false;
												 }),
						new SecondaryDrawerItem().withName(R.string.about)
												 .withIcon(R.drawable.ic_info)
												 .withIconTintingEnabled(true)
												 .withOnDrawerItemClickListener((view, pos, drawerItem) -> {
													 Intent i = new Intent(MailActivity.this, AboutActivity.class);
													 startActivity(i);
													 return false;
												 })
				).build();

		if(folders != null && !folders.isEmpty()) showFolders(folders);

		getSupportActionBar().setHomeButtonEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public Loader<ArrayList<String>> onCreateLoader(int i, Bundle args) {
		return new MailFolderLoader(this);
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> data) {
		getLoaderManager().destroyLoader(LID_FOLDER);
		if(data == null || data.isEmpty()) Util.showToast(this, R.string.error_folder_loading);
		else {
			if(folders == null) {
				folders = data;
				showFolders(folders);
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<ArrayList<String>> loader) {
		// Do nothing
	}
}
