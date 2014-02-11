package com.pablo.dropbook;

import com.dropbox.client2.session.Session.AccessType;

public interface AppData {

	// DropBox keys
	public static final String APP_KEY = "z57o02qodwuu7y0";
	public static final String APP_SECRET = "6ircrg8fq9vcdzz";
	public static final AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	
	// Preferences
	public static final String PREFS_NAME = "com.pablo.dropbook.prefs";
	public static final String USR_TOKEN = "userToken";


}
