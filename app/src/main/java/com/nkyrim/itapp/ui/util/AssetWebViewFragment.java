package com.nkyrim.itapp.ui.util;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.nkyrim.itapp.R;

/**
 * A fragment that contains a WebView and displays a file from the Assets folder that we pass as argument
 *
 * <b>Notice: </b> Always use newInstance() method for fragment creation.
 */
@SuppressLint("SetJavaScriptEnabled")
public class AssetWebViewFragment extends Fragment {
	public final static String ARG_FILENAME = "ARG_FILENAME";

	public static AssetWebViewFragment newInstance(String filename) {
		AssetWebViewFragment fragment = new AssetWebViewFragment();
		Bundle args = new Bundle();
		args.putString(AssetWebViewFragment.ARG_FILENAME, filename);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_webview, container, false);
		String filename = getArguments().getString(ARG_FILENAME);
		WebView web = ((WebView) view.findViewById(R.id.web));
		web.getSettings().setJavaScriptEnabled(true);
		web.loadUrl("file:///android_asset/" + filename);

		return view;
	}
}
