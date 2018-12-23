package com.nkyrim.itapp.ui.util.base;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

/**
 * A generic base AsyncTaskLoader that implements some very basic functionality.
 */
public abstract class BaseLoader<Result> extends AsyncTaskLoader<Result> {
	protected final String TAG = getClass().getSimpleName();
	protected Result result;

	public BaseLoader(Context context) {
		super(context);
	}

	@Override
	public void onStartLoading() {
		if(result != null) deliverResult(result);
		if(takeContentChanged() || result == null) forceLoad();
	}

	@Override
	protected void onStopLoading() {
		cancelLoad();
	}

	@Override
	protected void onReset() {
		super.onReset();
		onStopLoading();
		result = null;
	}
}
