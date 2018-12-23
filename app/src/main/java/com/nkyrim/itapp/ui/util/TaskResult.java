package com.nkyrim.itapp.ui.util;

/**
 * This class can be used with AsyncTask and AsyncTaskLoader as a return type. The TaskResult object contains the actual Result, if any, an
 * optional message, usually only in case of an error and a boolean flag to signify the outcome of the task.
 *
 * @author Nick Kyrimlidis
 */
public class TaskResult<Result> {
	private Result result;
	private String message;
	private boolean success;

	public TaskResult(boolean success) {
		this(success, null, null);
	}

	public TaskResult(boolean success, String message) {
		this(success, null, message);
	}

	public TaskResult(Result result) {
		this(true,result, null);
	}

	private TaskResult(boolean success, Result result, String message) {
		this.result = result;
		this.message = message;
		this.success = success;
	}

	public Result getResult() {
		return result;
	}

	public String getMessage() {
		return message;
	}

	public boolean isSuccessful() {
		return success;
	}
}
