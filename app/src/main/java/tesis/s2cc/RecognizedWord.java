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

	private RecognitionActivity mActivity;
	private TextView mView;
	private TextView mSepView;
	private String mPartialInput;
	private ViewGroup mContainer;
	private ClosedCaptionGenerator mCCGenerator;

	RecognizedWord(String word, RecognitionActivity activity, ClosedCaptionGenerator ccGenerator) {
		mActivity = activity;
		mPartialInput = "";
		mContainer = (FlowLayout) mActivity.findViewById(R.id.RecognitionResults);
		mCCGenerator = ccGenerator;

		mView = new TextView(mActivity);
		mView.setText(word);
		int spacing = mActivity.getResources().getDimensionPixelSize(R.dimen.word_spacing);
		mView.setPadding(spacing, 0, spacing, 0);
		mView.setTextColor(Color.BLACK);
		mView.setLongClickable(true);
		mView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
		mView.setLayoutParams(new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		mView.setOnClickListener(this);
		mView.setOnLongClickListener(this);
		mContainer.addView(mView);

		mSepView = new TextView(mActivity);
		mSepView.setText(R.string.ellipsis);
		mSepView.setTextColor(Color.BLACK);
		mSepView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
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
			mActivity.hideKeyboard();
			onInputComplete();
			return true;
		}
		else if (keyCode == KeyEvent.KEYCODE_BACK) {
			// TODO: este evento no llega cuando esta abierto el teclado virtual
			mPartialInput = "";
			unSelect();
			return true;
		}
		else {
			char printableChar = (char) event.getUnicodeChar();
			if (printableChar>0x0F) {
				mPartialInput += printableChar;
				return true;
			}
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
		mActivity.showKeyboardFor(this);
		mView.setBackgroundColor(Color.rgb(173, 216, 230));
	}

	@Override
	public boolean onLongClick(View view) {
		mContainer.removeView(mView);
		mContainer.removeView(mSepView);
		mCCGenerator.removeWord(this);
		return true;
	}
}
