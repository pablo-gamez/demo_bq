package com.pablo.dropbook;

import com.dropbox.client2.session.Session.AccessType;

public interface AppData {

	// DropBox keys
	public static final String APP_KEY = "z57o02qodwuu7y0";
	public static final String APP_SECRET = "6ircrg8fq9vcdzz";
	public static final AccessType ACCESS_TYPE = AccessType.DROPBOX;
	
	// Preferences
	public static final String PREFS_NAME = "com.pablo.dropbook.prefs";
	public static final String USR_TOKEN = "userToken";
	
	// Constants
	public static final String _EPUB = ".epub";
	public static final String TAG_ERROR = "ERROR";
	public static final String DROPBOX_BASE_PATH = "/";

}
