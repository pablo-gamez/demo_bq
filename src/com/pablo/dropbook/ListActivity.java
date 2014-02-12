package com.pablo.dropbook;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

public class ListActivity extends Activity implements AppData {

	ListView list;
	ArrayList<Ebook> eBooks;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view);

		// get extra data from intent
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			if (extras.get(DB_DATA) instanceof ArrayList) {
				eBooks = (ArrayList<Ebook>) extras.get(DB_DATA);
			}
		}

		// Get views reference
		list = (ListView) findViewById(R.id.listContent);
		list.setEmptyView((TextView) findViewById(R.id.emptyContent));

	}
	
	
	
}
