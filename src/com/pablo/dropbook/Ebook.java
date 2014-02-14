package com.pablo.dropbook;

import java.io.IOException;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Ebook {

	private static final String TAG = "EBOOK";

	private byte[] mCoverImage;
	private String mDropboxTitle, mBookTitle, mBookAuthor;

	public Ebook(String bookName, Book book) {
		mDropboxTitle = bookName;

		if (book != null) {
			// Book title
			mBookTitle = book.getTitle();

			// Book Author
			Author bookAuthor = book.getMetadata().getAuthors().get(0);
			mBookAuthor = bookAuthor.getFirstname() + " "
					+ bookAuthor.getLastname();

			// Book cover image
			try {
				Resource cover = book.getCoverImage();
				if (cover != null) {
					mCoverImage = cover.getData();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Log.d(TAG, bookName);

	}

	public String getDropboxTitle() {
		return mDropboxTitle;
	}

	public String getBookTitle() {
		return mBookTitle;
	}

	public String getBookAuthor() {
		return mBookAuthor;
	}

	public Bitmap getCoverImage() {
		return BitmapFactory
				.decodeByteArray(mCoverImage, 0, mCoverImage.length);
	}

}
