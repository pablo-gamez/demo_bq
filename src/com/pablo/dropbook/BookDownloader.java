package com.pablo.dropbook;

import java.util.ArrayList;

import nl.siegmann.epublib.domain.Book;
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

	public BookDownloader(Activity context) {
		mActivity = context;
		dbHandler = DropBoxController.getInstance(context
				.getApplicationContext());

	}

	@Override
	protected Boolean doInBackground(Void... params) {

		eBooks = getBooks(DROPBOX_BASE_PATH);

		if (eBooks != null && eBooks.size() > 0) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			Intent intent = new Intent(this.mActivity, ListActivity.class);
			intent.putParcelableArrayListExtra(DB_DATA, eBooks);
			mActivity.startActivity(intent);

		} else {
			// Couldn't download it, so show an error
			Toast.makeText(
					mActivity,
					"No hay eBooks con formato \".epub\" en tu cuenta de dropbox",
					Toast.LENGTH_LONG).show();
			Log.e(TAG_ERROR, "No eBooks detected");
		}
	}

	// Recursive method to retrieve all .epub files from dropbox account
	private ArrayList<Ebook> getBooks(String path) {
		ArrayList<Ebook> books = new ArrayList<Ebook>();

		Entry parentEntry = dbHandler.getEntry(path, 1000, true);

		if (parentEntry.contents != null) {
			for (Entry entry : parentEntry.contents) {
				if (entry.isDir) {
					books.addAll(getBooks(entry.path));
				} else {
					if (entry.fileName().endsWith(_EPUB)) {
						Book entryBook = dbHandler.downloadBook(entry.path,
								entry.fileName());
						books.add(new Ebook(entry, entryBook));
					}
				}
			}
		}

		return books;
	}

}