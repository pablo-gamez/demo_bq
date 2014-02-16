package com.pablo.dropbook;

import java.io.IOException;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Date;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Ebook {

	private Book mBook;
	private String mDropboxTitle;

	public Ebook(String bookName, Book book) {
		mDropboxTitle = bookName;
		mBook = book;

	}

	public String getDropboxTitle() {
		return mDropboxTitle;
	}

	public String getBookTitle() {
		return mBook.getTitle();
	}

	public Date getModifiedTime() {
		try {
			return mBook.getMetadata().getDates().get(0);
		} catch (Exception e) {
			return null;
		}
	}

	public Bitmap getCoverImage() {
		if (mBook.getCoverImage() != null) {
			Bitmap coverImage;
			try {
				coverImage = BitmapFactory.decodeStream(mBook.getCoverImage()
						.getInputStream());

				return coverImage;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
