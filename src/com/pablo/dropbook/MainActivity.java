package com.pablo.dropbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.pablo.dropbook.utilities.DropBoxController;

public class MainActivity extends Activity implements AppData {

	// DropBox objects
	DropBoxController dbHandler;

	Button authBtn, listBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_view);

		// Initialize DropBox services
		DropBoxController.setupController(getApplicationContext());
		dbHandler = DropBoxController.getInstance();

		authBtn = (Button) findViewById(R.id.accessBtn);
		listBtn = (Button) findViewById(R.id.goListBtn);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (dbHandler != null) {
			if (dbHandler.checkLogInStatus()) {
				authBtn.setVisibility(View.GONE);
				listBtn.setVisibility(View.VISIBLE);
			}
		}
	}

	public void goDropBox(View view) {
		if (dbHandler != null) {
			dbHandler.logIn(MainActivity.this);
		}
	}

	public void goList(View view) {
		Intent listIntent = new Intent(getBaseContext(), ListActivity.class);
		startActivity(listIntent);
	}
}
