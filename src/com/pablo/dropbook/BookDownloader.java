package com.pablo.dropbook;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI.Entry;

public class BookDownloader extends AsyncTask<Void, Long, Boolean> implements
		AppData {

	private Activity mActivity;

	DropBoxController dbHandler;

	ArrayList<Ebook> eBooks = new ArrayList<Ebook>();

	private String mErrorMsg;

	public BookDownloader(Activity context) {
		mActivity = context;
		dbHandler = DropBoxController.getInstance(context
				.getApplicationContext());

	}

	@Override
	protected Boolean doInBackground(Void... params) {

		Entry dirEnt = dbHandler.getEntry(DROP_PATH, 1000, true);

		if (!dirEnt.isDir || dirEnt.contents == null) {
			// It's not a directory, or there's nothing in it
			return false;
		} else {
			for (Entry entry : dirEnt.contents) {
				if (entry.fileName().endsWith(_EPUB)) {
					eBooks.add(new Ebook(entry));
				}
				System.out.println("Entries from dropbox: "
						+ mActivity.getCacheDir().getAbsolutePath()
						+ entry.path);
			}
			return true;
		}

	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			Intent intent = new Intent(this.mActivity, ListActivity.class);
			intent.putExtra(DB_DATA, eBooks);
			mActivity.startActivity(intent);
			mActivity.finish();

		} else {
			// Couldn't download it, so show an error
			Toast.makeText(mActivity, mErrorMsg, Toast.LENGTH_LONG).show();
			Log.e(TAG_ERROR, "File or empty directory" );
		}
	}

}