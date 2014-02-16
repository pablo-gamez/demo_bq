package com.pablo.dropbook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Date;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.dropbox.client2.DropboxAPI.Entry;
import com.pablo.dropbook.utilities.BookDownloader;
import com.pablo.dropbook.utilities.BookDownloader.OnBookDownloadedListener;
import com.pablo.dropbook.utilities.DropBoxController;
import com.pablo.dropbook.utilities.ThreadPoolQueuer;

public class ListActivity extends Activity implements AppData,
		OnBookDownloadedListener, OnItemClickListener {

	// Dropbox services
	DropBoxController dbHandler;

	// Downloader service
	private ThreadPoolQueuer poolQueuer;

	// Interface components
	GridView list;
	ArrayList<Ebook> eBooks = new ArrayList<Ebook>();
	BookListAdapter adapter;
	ArrayList<Entry> entrys = new ArrayList<Entry>();

	// DrawerLayout

	private DrawerLayout mDrawerLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grid_view);

		dbHandler = DropBoxController.getInstance();
		poolQueuer = ThreadPoolQueuer.getInstance();

		// Create adapter for bookList
		adapter = new BookListAdapter(getBaseContext(), eBooks);

		// Get views reference
		list = (GridView) findViewById(R.id.listContent);
		list.setOnItemClickListener(this);
		list.setAdapter(adapter);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		// Start Async Download
		new AsyncDownload().execute();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Ebook clickedBook = eBooks.get(position);
		DetailDialog dlg = new DetailDialog(ListActivity.this, clickedBook);
		dlg.show();
	}

	@Override
	public void onBookDonloaded(String bookName, Book ebook) {
		Log.d("NEW_BOOK", bookName);
		eBooks.add(new Ebook(bookName, ebook));
		adapter.notifyDataSetChanged();
	}

	public void openTab(View view) {
		if (!mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
			mDrawerLayout.openDrawer(GravityCompat.START);
		}
	}

	public void reorder(View view) {
		switch (view.getId()) {
		case R.id.orderArchivo:
			Collections.sort(eBooks, new Comparator<Ebook>() {
				@Override
				public int compare(Ebook book1, Ebook book2) {
					String fileName1 = book1.getDropboxTitle();
					String fileName2 = book2.getDropboxTitle();

					return fileName1.compareToIgnoreCase(fileName2);
				}
			});

			break;
		case R.id.orderDate:
			Collections.sort(eBooks, new Comparator<Ebook>() {
				@Override
				public int compare(Ebook book1, Ebook book2) {
					Date date1 = book1.getModifiedTime();
					Date date2 = book2.getModifiedTime();

					if (date1 == null && date2 == null) {
						return 0;
					} else if (date1 == null) {
						return 1;
					} else if (date2 == null) {
						return -1;
					} else {
						return date1.getValue().compareToIgnoreCase(
								date2.getValue());
					}
				}
			});

			break;
		case R.id.orderBookTitle:
			Collections.sort(eBooks, new Comparator<Ebook>() {
				@Override
				public int compare(Ebook book1, Ebook book2) {
					String bookName1 = book1.getBookTitle();
					String bookName2 = book2.getBookTitle();

					return bookName1.compareToIgnoreCase(bookName2);
				}
			});

			break;

		default:
			break;
		}

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
