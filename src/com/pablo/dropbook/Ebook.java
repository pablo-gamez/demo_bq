package com.pablo.dropbook;

import java.io.IOException;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.dropbox.client2.DropboxAPI.Entry;

public class Ebook implements Parcelable {

	private static final String TAG = "EBOOK";

	private byte[] mCoverImage;
	private String mDropboxTitle, mDropboxPath, mBookTitle, mBookAuthor;

	public Ebook(Entry entry, Book book) {
		if (entry != null) {
			mDropboxTitle = entry.fileName();
			mDropboxPath = entry.path;
		}

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

		Log.d(TAG, entry.fileName());

	}

	public Ebook(Parcel parcel) {
		mDropboxTitle = parcel.readString();
		mDropboxPath = parcel.readString();
		mBookTitle = parcel.readString();
		mBookAuthor = parcel.readString();
		mCoverImage = parcel.createByteArray();
		if (mCoverImage != null) {
			parcel.unmarshall(mCoverImage, 0, mCoverImage.length);
		}

		Log.d(TAG, "Contructor: " + mBookTitle);

	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(mDropboxTitle);
		dest.writeString(mDropboxPath);
		dest.writeString(mBookTitle);
		dest.writeString(mBookAuthor);
		dest.writeByteArray(mCoverImage);
		
		Log.d(TAG, "Write: " + mBookTitle);

	}

	public static final Parcelable.Creator<Ebook> CREATOR = new Parcelable.Creator<Ebook>() {
		public Ebook createFromParcel(Parcel parcel) {
			Log.d(TAG, "createFromParcel");

			return new Ebook(parcel);
		}

		public Ebook[] newArray(int size) {
			Log.d(TAG, "newArray");

			return new Ebook[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
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
