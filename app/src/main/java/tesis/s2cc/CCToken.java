package tesis.s2cc;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.flexbox.FlexboxLayout;

public class CCToken implements RecognizedWord.Listener, View.OnClickListener, View.OnFocusChangeListener {

	private static final String TAG = "CCToken";

	private RecognitionActivity mActivity;
	private RecognizedWord mRecognizedWord;
	private String mEllipsis;
	private EditText mView;
	private EditText mSepView;

	public CCToken(RecognitionActivity activity, RecognizedWord rWord ) {
		mActivity = activity;
		mRecognizedWord = rWord;
		rWord.setListener(this);

		// Word view
		mView = initView(rWord.getWord());
		mView.setInputType( InputType.TYPE_CLASS_TEXT );
		mView.setImeOptions( EditorInfo.IME_ACTION_DONE );
		int spacing = mActivity.getResources().getDimensionPixelSize( R.dimen.word_spacing );
		mView.setPadding( spacing, 0, spacing, 0 );
		mView.setBackgroundColor( Color.TRANSPARENT );

		// Separator view
		mEllipsis = mActivity.getResources().getString(R.string.ellipsis);
		mSepView = initView( mEllipsis );
		mSepView.setBackgroundResource( R.drawable.border_background );
	}

	public String getText() {
		String separator = mSepView.getText().toString();
		return mView.getText().toString() + (separator.equals(mEllipsis) ? "" : separator);
	}

	public long getTimeStamp() {
		return mRecognizedWord.getTimeStamp();
	}

	public float getConfidence() {
		return mRecognizedWord.getConfidence();
	}

	public void setSeparatorText( CharSequence text ) {
		mSepView.setText(text);
		mSepView.selectAll();
	}

	public void show( ViewGroup container ) {
		Log.v(TAG, "show: word=" + mRecognizedWord.getWord() + ", mView.text=" + mView.getText());
		container.addView(mView);
		container.addView(mSepView);
	}

	public void show( int index, ViewGroup container ) {
		Log.v(TAG, "show: word=" + mRecognizedWord.getWord() + ", mView.text=" + mView.getText());
		container.addView(mView, index*2);
		container.addView(mSepView, index*2+1);
	}

	public void onDeleted( ViewGroup container ) {
		Log.v(TAG, "onDeleted: word=" + mRecognizedWord.getWord());
		if (mView.isFocused()) {
			unSelect();
			hideKeyboard();
		}
		container.removeView(mView);
		mView.setOnFocusChangeListener(null);
		mView.setOnClickListener(null);
		container.removeView(mSepView);
		mSepView.setOnFocusChangeListener(null);
		mSepView.setOnClickListener(null);
		mRecognizedWord.setListener(null);
	}

	public void focus() {
		Log.v(TAG, "select: word=" + mRecognizedWord.getWord());
		mView.requestFocus();
	}

	@Override
	public void onWordChanged(String oldWord, String newWord) {
		Log.v(TAG, "onWordChanged: oldWord=" + oldWord + ", newWord=" + newWord + ", mView.text=" + mView.getText());
		if (oldWord.equals(mView.getText().toString())) {
			mView.setText(newWord);
		}
	}

	@Override
	public void onFocusChange(View view, boolean focused) {
		Log.v(TAG, "onFocusChange: word=" + mRecognizedWord.getWord() + ", focused=" + focused);
		if (focused) {
			onViewFocused((EditText) view);
			mActivity.showActionButtons(this, view == mView, view == mSepView);
		}
	}

	@Override
	public void onClick(View view) {
		Log.v(TAG, "onClick: word=" + mRecognizedWord.getWord());
		if (view.isFocused()) {
			unSelect();
			if (view == mView) {
				hideKeyboard();
			}
		} else {
			if (view == mSepView) {
				hideKeyboard();
			}
			view.requestFocus();
		}
	}

	private EditText initView( String text ) {
		EditText view = new EditText(mActivity);
		view.setText(text);
		view.setFocusable(true);
		view.setFocusableInTouchMode(true);
		view.setCursorVisible(false);
		view.setTextIsSelectable(true);
		view.setTextColor(Color.BLACK);
		view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
		view.setLayoutParams(new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		view.setOnClickListener(this);
		view.setOnFocusChangeListener(this);

		// Prevent double click default action:
		view.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {return false;}
			public void onDestroyActionMode(ActionMode mode) {}
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {return false;}
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {return false;}
		});

		return view;
	}

	private void unSelect() {
		Log.v(TAG, "unSelect: word=" + mRecognizedWord.getWord());
		mActivity.hideActionButtons();
		mActivity.findViewById(R.id.start_stop).requestFocus();
	}

	private void onViewFocused( final EditText view ) {
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				view.selectAll();
				if (view == mView) {
					showKeyboard();
				} else {
					hideKeyboard();
				}
			}
		}, 60);
	}

	private void showKeyboard() {
		InputMethodManager keyboard = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		keyboard.showSoftInput(mView, InputMethodManager.SHOW_IMPLICIT);
	}

	private void hideKeyboard() {
		InputMethodManager keyboard = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		keyboard.hideSoftInputFromWindow(mView.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
	}
}
