package com.pablo.dropbook.utilities;

import nl.siegmann.epublib.domain.Book;
import android.os.Handler;

public class BookDownloader implements Runnable {

	private DropBoxController dbHandler;
	private static Handler handler = new Handler();

	// Downloader fields
	private OnBookDownloadedListener listener = null;
	private String dbPath = null;
	private String fileName = null;

	public BookDownloader(String dropboxPath, String fileName,
			OnBookDownloadedListener listener) {
		// Initialize DropboxController
		dbHandler = DropBoxController.getInstance();

		this.dbPath = dropboxPath;
		this.fileName = fileName;
		this.listener = listener;
	}

	// INTERFACE
	/**
	 * Defines an interface for a callback that will handle responses from the
	 * thread loader when a book is done being downloaded.
	 */
	public interface OnBookDownloadedListener {
		public void onBookDonloaded(String fileName, Book downloadedEbook);
	}

	@Override
	public void run() {

		final Book ebook = dbHandler.downloadBook(dbPath, fileName);

		// Return ebook if download was completed
		if (ebook != null) {
			handler.post(new Runnable() {
				public void run() {
					listener.onBookDonloaded(fileName, ebook);
				}
			});
		}

	}

}
