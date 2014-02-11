package com.pablo.dropbook;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.pablo.dropbook.R;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    final static private String APP_KEY = "z57o02qodwuu7y0";
    final static private String APP_SECRET = "6ircrg8fq9vcdzz";
    final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
    
    private DropboxAPI<AndroidAuthSession> mDBApi;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_view);
		
		AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session = new AndroidAuthSession(appKeys, ACCESS_TYPE);
		mDBApi = new DropboxAPI<AndroidAuthSession>(session);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (mDBApi.getSession().authenticationSuccessful()) {
	        try {
	            // Required to complete auth, sets the access token on the session
	            mDBApi.getSession().finishAuthentication();

	            String accessToken = mDBApi.getSession().getOAuth2AccessToken();
	            Log.d(TAG, "Token: " + accessToken);
	        } catch (IllegalStateException e) {
	            Log.i("DbAuthLog", "Error authenticating", e);
	        }
	    }	
	}
	
	public void goDropBox(View view) {
		mDBApi.getSession().startOAuth2Authentication(MainActivity.this);

	}

}
