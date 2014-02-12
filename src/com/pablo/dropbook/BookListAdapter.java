package com.pablo.dropbook;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BookListAdapter extends BaseAdapter {

	private ArrayList<Ebook> bookList;
	private LayoutInflater inflater;

	private class ViewHolder {
		public TextView titleViewHolder;
	}

	public BookListAdapter(Context context, ArrayList<Ebook> items) {
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		bookList = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		final ViewHolder tempView;

		if (view == null) {
			view = inflater.inflate(R.layout.list_item, parent, false);

			tempView = new ViewHolder();

			tempView.titleViewHolder = (TextView) view
					.findViewById(R.id.itemTitle);

			view.setTag(tempView);
		} else {
			tempView = (ViewHolder) view.getTag();
		}

		Ebook book = getItem(position);
		if (book == null) {
			tempView.titleViewHolder.setText("Error en libro");
		} else {
			Log.d("EBOOK", "Book title: " + book.getBookTitle());
			tempView.titleViewHolder.setText(book.getBookTitle());
		}
		return view;
	}

	@Override
	public int getCount() {
		return bookList.size();
	}

	@Override
	public Ebook getItem(int position) {
		return bookList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}
