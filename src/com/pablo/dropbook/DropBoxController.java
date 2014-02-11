/*
 * Copyright (c) 2010-11 Dropbox, Inc.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.pablo.dropbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;

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
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AppKeyPair;

public class DropBoxController implements AppData {

	private static final String TAG = "DropBoxController";

	// Preferences
	private SharedPreferences prefs;
	private SharedPreferences.Editor prefsEdit;

	// You don't need to change these, leave them alone.
	final static private String REV_PREFIX = "REV";
	final static private String REV_MODIFIED = "MODIFIED";

	private static DropBoxController mInstance;

	DropboxAPI<AndroidAuthSession> mApi;

	private Context mContext;

	public static DropBoxController getInstance(Context context) {
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

	// access methods

	public void listFiles() {
		try {
			Entry existingEntry = mApi.metadata("/", 0, null,
					false, null);
			Log.i("DbExampleLog", "The file's rev is now: " + existingEntry.rev);
		} catch (DropboxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Entry getEntry(String path, int fileLimit, boolean showChilds) {
		try {
			return mApi.metadata(path, fileLimit, null, showChilds, null);
			
		} catch (DropboxException e) {
			e.printStackTrace();
			return null;
		}
	}

	// ========basic functions===================================

	public void uploadFile(File file, String path) {
		try {
			FileInputStream fis = new FileInputStream(file);
			Entry newEntry = mApi.putFileOverwrite(path, fis, file.length(),
					null);
			Log.i(TAG, "The uploaded file's rev is: " + newEntry.rev);
			saveFileRev(path, newEntry.rev);
			saveFileModifiedDate(path, file.lastModified() + "");
		} catch (DropboxUnlinkedException e) {
			// User has unlinked, ask them to link again here.
			Log.e(TAG, "User has unlinked.");
		} catch (DropboxException e) {
			Log.e(TAG, "Something went wrong while uploading.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void downloadFile(File file, String path) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			mApi.getFile(path, null, fos, null);
			Entry entry = mApi.metadata(path, 0, null, false, null);
			saveFileModifiedDate(path, file.lastModified() + "");
			saveFileRev(path, entry.rev);
		} catch (DropboxException e) {
			Log.e(TAG, "Something went wrong while downloading.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private String getCurrentFileRev(String path) {
		return prefs.getString(REV_PREFIX + path, "");
	}

	private void saveFileRev(String path, String rev) {
		prefsEdit.putString(REV_PREFIX + path, rev);
		prefsEdit.commit();
	}

	@SuppressWarnings("deprecation")
	private Date getCurrentFileModifiedDate(String path) {
		String dateString = prefs.getString(REV_MODIFIED + path, "0");
		long dateMilli = Long.parseLong(dateString);
		Date date = new Date(1900, 1, 1);
		try {
			date = new Date(dateMilli);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	private void saveFileModifiedDate(String path, String date) {
		prefsEdit.putString(REV_MODIFIED + path, date);
		prefsEdit.commit();
	}

	public boolean synchronizeFile(File file, String pathOnline) {
		boolean isFileUpdated = false;
		String lastRev = getCurrentFileRev(pathOnline);
		Date lastModifiedRecord = getCurrentFileModifiedDate(pathOnline);
		Date lastModified = new Date(file.lastModified());
		if (lastModifiedRecord.before(lastModified)) {
			// file has change since last download
			// need to upload or download
			try {
				Entry existingEntry = mApi.metadata(FILE_FAVIROTE, 1, null,
						false, null);
				String rev = existingEntry.rev;

				if (rev.equals(lastRev)) {
					// remote file hasn't change since last synchronization
					// upload file
					uploadFile(file, pathOnline);
				} else {
					// remote file has changed since last synchronization
					// conflict
					// TODO: merge file?
					downloadFile(file, pathOnline);
					isFileUpdated = true;
				}

			} catch (DropboxServerException e) {
				switch (e.error) {
				case DropboxServerException._404_NOT_FOUND:
					// remote file not exist
					// upload
					uploadFile(file, pathOnline);
				default:
					break;

				}
			} catch (DropboxException e) {
				Log.e(TAG, "Something went wrong while getting metadata.");
				// not available online
			}
		} else {
			// file hasn't change since last download
			// need to check if there's a new version
			try {
				Entry existingEntry = mApi.metadata(FILE_FAVIROTE, 1, null,
						false, null);
				String rev = existingEntry.rev;

				if (!rev.equals(lastRev)) {
					// not synchronized
					// need to download
					if (file.exists()) {
						file.delete();
					}
					downloadFile(file, pathOnline);
					isFileUpdated = true;
				}

			} catch (DropboxException e) {
				Log.e(TAG, "Something went wrong while getting metadata.");
				// consider as no new version available
			}
		}
		return isFileUpdated;
	}

	// =========my functions==========

	private static final String FILE_FAVIROTE = "/favorite.xml";

}
