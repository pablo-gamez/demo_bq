package com.pablo.dropbook;

import java.util.ArrayList;

import nl.siegmann.epublib.domain.Book;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI.Entry;
import com.pablo.dropbook.utilities.BookDownloader;
import com.pablo.dropbook.utilities.BookDownloader.OnBookDownloadedListener;
import com.pablo.dropbook.utilities.DropBoxController;
import com.pablo.dropbook.utilities.ThreadPoolQueuer;

public class ListActivity extends Activity implements AppData,
		OnBookDownloadedListener {

	// Dropbox services
	DropBoxController dbHandler;

	// Downloader service
	private ThreadPoolQueuer poolQueuer;

	// Interface components
	ListView list;
	ArrayList<Ebook> eBooks = new ArrayList<Ebook>();
	BookListAdapter adapter;
	ArrayList<Entry> entrys = new ArrayList<Entry>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view);

		dbHandler = DropBoxController.getInstance();
		poolQueuer = ThreadPoolQueuer.getInstance();

		// Create adapter for bookList
		adapter = new BookListAdapter(getBaseContext(), eBooks);

		// Get views reference
		list = (ListView) findViewById(R.id.listContent);
		list.setEmptyView((TextView) findViewById(R.id.emptyContent));

		list.setAdapter(adapter);

		// Start Async Download
		new AsyncDownload().execute();
	}

	@Override
	public void onBookDonloaded(String bookName, Book ebook) {
		Log.d("NEW_BOOK", bookName);
		eBooks.add(new Ebook(bookName, ebook));
		adapter.notifyDataSetChanged();
	}

	public class AsyncDownload extends AsyncTask<Void, Void, Void> implements
			AppData {

		WaitDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = new WaitDialog(ListActivity.this,
					getString(R.string.getting_books));
			dialog.show();

			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			getBooks(DROPBOX_BASE_PATH);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			for (Entry entry : entrys) {

				poolQueuer.addAction(new BookDownloader(entry.path, entry
						.fileName(), ListActivity.this));
			}
			dialog.dismiss();

			super.onPostExecute(result);
		}

		// Recursive method to retrieve all .epub files from dropbox account
		private void getBooks(String path) {
			Entry parentEntry = dbHandler.getEntry(path, 1000, true);

			if (parentEntry.contents != null) {
				for (Entry entry : parentEntry.contents) {
					if (entry.isDir) {
						getBooks(entry.path);
					} else {
						if (entry.fileName().endsWith(_EPUB)) {
							entrys.add(entry);
						}
					}
				}
			}

		}

	}

}
