package com.pablo.dropbook;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailDialog extends Dialog {

	private TextView title;
	private ImageView imageCover;
	private Ebook mEbook;

	public DetailDialog(Context context, Ebook bookToShow) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.detail_dialog);

		mEbook = bookToShow;
		title = (TextView) findViewById(R.id.detail_title);
		imageCover = (ImageView) findViewById(R.id.detail_img);

	}

	@Override
	protected void onStart() {
		super.onStart();

		title.setText(mEbook.getBookTitle());

		Bitmap coverImage = mEbook.getCoverImage();
		if (coverImage != null) {
			imageCover.setImageBitmap(coverImage);
		}

	}

}
