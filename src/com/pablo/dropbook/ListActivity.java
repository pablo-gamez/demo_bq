package com.pablo.dropbook;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class ListActivity extends Activity implements AppData {

	// DropBox objects
	DropBoxController dbHandler;
	ListView list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view);

		// Initialize DropBox services
		dbHandler = DropBoxController.getInstance(getBaseContext());

		// Get views reference
		list = (ListView) findViewById(R.id.listContent);
		
	}

}
