package com.nkyrim.itapp.ui.filepickers;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.util.Util;
import com.nononsenseapps.filepicker.AbstractFilePickerFragment;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.nononsenseapps.filepicker.FilePickerFragment;

import java.io.File;
import java.util.List;

public class DownloadsActivity extends FilePickerActivity {

	@Override
	protected AbstractFilePickerFragment<File> getFragment(String startPath, int mode, boolean allowMultiple, boolean allowCreateDir) {
		File downDir = Util.getDownloadDir();
		if(downDir != null) startPath = downDir.getPath();
		else startPath = Environment.getExternalStorageDirectory().getPath();
		AbstractFilePickerFragment<File> fragment = new FilePickerFragment();
		fragment.setArgs(startPath, FilePickerActivity.MODE_FILE, false, false);
		return fragment;
	}

	@Override
	public void onFilePicked(Uri file) {
		String ext = file.getLastPathSegment().substring(file.getLastPathSegment().lastIndexOf(".") + 1);
		Intent i = new Intent(Intent.ACTION_VIEW);
		String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
		i.setDataAndType(file, mime);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PackageManager pm = getPackageManager();
		List<ResolveInfo> info = pm.queryIntentActivities(i, 0);
		if(info.size() > 0) {
			startActivity(i);
			finish();
		} else {
			Util.showToast(this, R.string.error_cant_open);
		}
	}

}
