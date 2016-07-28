package tesis.s2cc;

import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wefika.flowlayout.FlowLayout;

public class RecognizedWord implements View.OnClickListener, View.OnLongClickListener {

	private static final String TAG = "RecognizedWord";

	private RecognitionActivity mContext;
	private TextView mView;
	private TextView mSepView;
	private String mPartialInput;
	private ViewGroup mContainer;

	RecognizedWord(String word, ViewGroup container, RecognitionActivity context) {
		mContext = context;
		mPartialInput = "";
		mContainer = container;

		mView = new TextView(mContext);
		mView.setText(word);
		mView.setTextColor(Color.BLACK);
		mView.setLongClickable(true);
		mView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
		mView.setLayoutParams(new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		mView.setOnClickListener(this);
		mView.setOnLongClickListener(this);
		mContainer.addView(mView);

		mSepView = new TextView(mContext);
		mSepView.setText(R.string.ellipsis);
		mSepView.setTextColor(Color.BLACK);
		mSepView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
		mSepView.setBackgroundResource(R.drawable.border_background);
		mSepView.setLayoutParams(new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		mContainer.addView(mSepView);
	}

	public String text() {
		return mView.getText().toString();
	}

	public void update(String word) {
		Log.v(TAG, "update: word=" + mView.getText() + ", updated_word=" + word);
		mView.setText(word);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.v(TAG, "onKeyUp: keyCode=" + keyCode + ", event=" + event.toString());

		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			mContext.hideKeyboard();
			onInputComplete();
			return true;
		}
		else if (keyCode == KeyEvent.KEYCODE_BACK) {
			// TODO: este evento no llega cuando esta abierto el teclado virtual
			mPartialInput = "";
			unSelect();
			return true;
		}
		else if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) {
			Log.v(TAG, "onKeyUp: character: " + (char) keyCode);
			mPartialInput += (char) event.getUnicodeChar();
			return true;
		}
		else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
			Log.v(TAG, "onKeyUp: number: " + (char) keyCode);
			mPartialInput += (char) event.getUnicodeChar();
			return true;
		}

		return false;
	}

	public void unSelect() {
		mView.setBackgroundColor(Color.TRANSPARENT);
	}

	private void onInputComplete() {
		if (mPartialInput.length() > 0) {
			mView.setText(mPartialInput);
			mPartialInput = "";
		}
		unSelect();
	}

	@Override
	public void onClick(View view) {
		Log.v(TAG, "onClick: word=" + mView.getText());
		mContext.showKeyboardFor(this);
		mView.setBackgroundColor(Color.rgb(173, 216, 230));
	}

	@Override
	public boolean onLongClick(View view) {
		mContainer.removeView(mView);
		mContext.removeWord(this);
		return true;
	}
}
