package com.pablo.dropbook;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.TextView;

public class WaitDialog extends Dialog {

	private TextView text;
	private String txt;

	public WaitDialog(Context context, String loaderText) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.loading_dialog);
		setCancelable(false);

		txt = loaderText;
		text = (TextView) findViewById(R.id.alert_text);
	}

	@Override
	protected void onStart() {
		super.onStart();
		text.setText(txt);
	}

}
