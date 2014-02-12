package com.pablo.dropbook;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

public class ListActivity extends Activity implements AppData {

	ListView list;
	ArrayList<Ebook> eBooks;
	BookListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view);

		// get ebooks array from intent
		eBooks = getIntent().getParcelableArrayListExtra(DB_DATA);

		// Create adapter for bookList
		adapter = new BookListAdapter(getBaseContext(), eBooks);

		// Get views reference
		list = (ListView) findViewById(R.id.listContent);
		list.setEmptyView((TextView) findViewById(R.id.emptyContent));

		list.setAdapter(adapter);

	}

}
