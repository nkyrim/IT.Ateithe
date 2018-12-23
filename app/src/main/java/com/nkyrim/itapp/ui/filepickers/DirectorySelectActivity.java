package com.nkyrim.itapp.ui.filepickers;

import android.net.Uri;
import android.os.Environment;

import com.nkyrim.itapp.ui.util.BusEvents;
import com.nkyrim.itapp.util.Util;
import com.nononsenseapps.filepicker.AbstractFilePickerFragment;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.nononsenseapps.filepicker.FilePickerFragment;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

public class DirectorySelectActivity extends FilePickerActivity {

	@Override
	protected AbstractFilePickerFragment<File> getFragment(String startPath, int mode, boolean allowMultiple, boolean allowCreateDir) {
		File downDir = Util.getDownloadDir();
		if(downDir != null) startPath = downDir.getPath();
		else startPath = Environment.getExternalStorageDirectory().getPath();
		AbstractFilePickerFragment<File> fragment = new FilePickerFragment();
		fragment.setArgs(startPath, FilePickerActivity.MODE_DIR, false, true);
		return fragment;
	}

	@Override
	public void onFilePicked(Uri file) {
		EventBus.getDefault().postSticky(new BusEvents.FileSelectedEvent(file));
		finish();
	}
}
