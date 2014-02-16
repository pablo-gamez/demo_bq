package com.pablo.dropbook;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BookListAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Ebook> bookList;
	private LayoutInflater inflater;

	private class ViewHolder {
		public TextView titleViewHolder;
		public ImageView imgViewHolder;
	}

	public BookListAdapter(Context context, ArrayList<Ebook> items) {
		mContext = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		bookList = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		final ViewHolder tempView;

		if (view == null) {
			view = inflater.inflate(R.layout.grid_item, parent, false);

			tempView = new ViewHolder();

			tempView.titleViewHolder = (TextView) view
					.findViewById(R.id.itemTitle);
			tempView.imgViewHolder = (ImageView) view
					.findViewById(R.id.itemImg);

			view.setTag(tempView);
		} else {
			tempView = (ViewHolder) view.getTag();
		}

		Ebook book = getItem(position);
		tempView.titleViewHolder.setText(book.getBookTitle());
		Bitmap coverImage = book.getCoverImage();
		if (coverImage == null) {
			coverImage = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.ebook_icon);
		}
		
		tempView.imgViewHolder.setImageBitmap(coverImage);

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
