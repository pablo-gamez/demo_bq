package com.pablo.dropbook.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.pablo.dropbook.AppData;

public class DropBoxController implements AppData {

	private static final String TAG = "DropBoxController";

	// Preferences
	private SharedPreferences prefs;
	private SharedPreferences.Editor prefsEdit;

	private String basePath;

	private static DropBoxController mInstance;

	DropboxAPI<AndroidAuthSession> mApi;

	private Context mContext;

	public static DropBoxController getInstance() {
		return mInstance;
	}

	public static DropBoxController setupController(Context context) {
		if (mInstance == null) {
			mInstance = new DropBoxController(context);
		}
		return mInstance;
	}

	private DropBoxController(Context context) {
		mContext = context.getApplicationContext();

		// SetUp app preferences
		prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		prefsEdit = prefs.edit();

		// Initialize Dropbox API session.
		AndroidAuthSession session = buildSession();

		mApi = new DropboxAPI<AndroidAuthSession>(session);

		checkAppKeySetup();

		basePath = "data/data/" + mContext.getPackageName() + "/";
	}

	@SuppressWarnings("deprecation")
	private AndroidAuthSession buildSession() {
		AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session;

		String accessToken = prefs.getString(USR_TOKEN, null);
		if (accessToken != null) {
			session = new AndroidAuthSession(appKeyPair, accessToken);
		} else {
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
		}

		return session;
	}

	private void checkAppKeySetup() {
		// Check to make sure that we have a valid app key
		if (APP_KEY.startsWith("CHANGE") || APP_SECRET.startsWith("CHANGE")) {
			Log.e(TAG,
					"You must apply for an app key and secret from developers.dropbox.com,"
							+ " and add them to the DBRoulette ap before trying it.");
			return;
		}

		// Check if the app has set up its manifest properly.
		Intent testIntent = new Intent(Intent.ACTION_VIEW);
		String scheme = "db-" + APP_KEY;
		String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";

		testIntent.setData(Uri.parse(uri));

		PackageManager pm = mContext.getPackageManager();
		if (0 == pm.queryIntentActivities(testIntent, 0).size()) {
			Log.e(TAG, "URL scheme in your app's "
					+ "manifest is not set up correctly. You should have a "
					+ "com.dropbox.client2.android.AuthActivity with the "
					+ "scheme: " + scheme);
		}
	}

	public boolean isLoggedIn() {
		return mApi.getSession().isLinked();
	}

	public void logIn(Activity activity) {
		if (isLoggedIn()) {
			logOut();
		}
		mApi.getSession().startOAuth2Authentication(activity);
	}

	public void logOut() {
		// Remove credentials from the session
		mApi.getSession().unlink();

		// Clear our stored keys
		clearKeys();
	}

	private void clearKeys() {
		prefsEdit.clear();
		prefsEdit.commit();
	}

	/**
	 * Upon authentication, users are returned to the activity from which they
	 * came. To finish authentication after the user returns to your app, you'll
	 * need to call this in your onResume function.
	 * 
	 * @return
	 */
	public boolean checkLogInStatus() {
		AndroidAuthSession session = mApi.getSession();
		if (session.authenticationSuccessful()) {
			try {
				// Mandatory call to complete the auth
				session.finishAuthentication();

				// Store it locally in our app for later use
				String token = session.getOAuth2AccessToken();
				if (token != null && !token.equals("")) {
					prefsEdit.putString(USR_TOKEN, token);
					prefsEdit.commit();
				}

				return true;
			} catch (IllegalStateException e) {
				Log.i(TAG,
						"Couldn't authenticate with Dropbox:"
								+ e.getLocalizedMessage());
				Log.i(TAG, "Error authenticating", e);
			}
		}
		return false;
	}

	// Methods

	public Entry getEntry(String path, int fileLimit, boolean showChilds) {
		try {
			Entry res = mApi.metadata(path, fileLimit, null, showChilds, null);
			return res;
		} catch (DropboxException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Book downloadBook(String dropboxPath, String fileName) {
		try {
			// File to download DropBox data
			File file = new File(basePath, fileName);
			FileOutputStream fos = new FileOutputStream(file);
			mApi.getFile(dropboxPath, null, fos, null);
			fos.close();

			// Convert file into Ebook
			EpubReader reader = new EpubReader();
			FileInputStream fis = new FileInputStream(file);
			Book book = reader.readEpub(fis);

			return book;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DropboxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

}
