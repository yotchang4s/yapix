package org.yotchang4s.android;

public abstract class AsyncTask<Params, Progress, Result> extends
		android.os.AsyncTask<Params, Progress, Result> {
	@Override
	protected Result doInBackground(Params... params) {
		return doInBackground(params.length > 0 ? params[0] : null);
	}

	abstract protected Result doInBackground(Params param);

	@Override
	protected void onProgressUpdate(Progress... values) {
		onProgressUpdate(values.length > 0 ? values[0] : null);
	}

	protected void onProgressUpdate(Progress value) {
	}

	@SuppressWarnings({ "unchecked" })
	protected final void publishProgress(Progress value) {
		super.publishProgress(value);
	}
}