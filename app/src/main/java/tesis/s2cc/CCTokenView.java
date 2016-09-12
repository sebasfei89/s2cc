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

public class CCTokenView implements View.OnClickListener, View.OnFocusChangeListener {

	private static final String TAG = "CCTokenView";

	private String mEllipsis;
	private CCToken mToken;
	private EditText mView;
	private EditText mSepView;
	private RecognitionActivity mActivity;

	public CCTokenView( RecognitionActivity activity, CCToken token ) {
		mToken = token;
		mActivity = activity;
		mEllipsis = mActivity.getResources().getString(R.string.ellipsis);
		mView = initView(mToken.getOriginalWord());
		mView.setInputType( InputType.TYPE_CLASS_TEXT );
		mView.setImeOptions( EditorInfo.IME_ACTION_DONE );
		int spacing = mActivity.getResources().getDimensionPixelSize( R.dimen.word_spacing );
		mView.setPadding( spacing, 0, spacing, 0 );
		mView.setBackgroundColor( Color.TRANSPARENT );

		mSepView = initView( mEllipsis );
		mSepView.setBackgroundResource( R.drawable.border_background );
	}

	public String getWord() {
		mToken.onWordEdited( mView.getText().toString() );
		String separator = mSepView.getText().toString();
		return mToken.getAcceptedWord() + (separator.equals(mEllipsis) ? " " : separator );
	}

	public void setSeparatorText( CharSequence text ) {
		mSepView.setText(text);
	}

	public void show( ViewGroup container ) {
		Log.v(TAG, "show: word=" + mToken.getOriginalWord() + ", mView.text=" + mView.getText());
		container.addView(mView);
		container.addView(mSepView);
	}

	public void show( int index, ViewGroup container ) {
		Log.v(TAG, "show: word=" + mToken.getOriginalWord() + ", mView.text=" + mView.getText());
		container.addView(mView, index*2);
		container.addView(mSepView, index*2+1);
	}

//	public void updateWord( String word ) {
//		Log.v(TAG, "update: mOriginalWord=" + mOriginalWord + ", mView.text=" + mView.getText() + ", word=" + word);
//		if (mOriginalWord.equals(mView.getText().toString())) {
//			mOriginalWord = word;
//			mView.setText(mOriginalWord);
//		}
//	}

	public void onDeleted( ViewGroup container ) {
		Log.v(TAG, "onDeleted: word=" + mToken.getOriginalWord());
		if (mView.isFocused()) {
			unSelect();
			hideKeyboard();
		}
		container.removeView(mView);
		container.removeView(mSepView);
	}

	public void focus() {
		Log.v(TAG, "select: word=" + mToken.getOriginalWord());
		mView.requestFocus();
	}

	@Override
	public void onFocusChange(View view, boolean focused) {
		Log.v(TAG, "onFocusChange: word=" + mToken.getOriginalWord() + ", focused=" + focused);
		if (focused) {
			onViewFocused((EditText) view);
			mActivity.showActionButtons(this, view == mView, view == mSepView);
		}
	}

	@Override
	public void onClick(View view) {
		Log.v(TAG, "onClick: word=" + mToken.getOriginalWord());
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
		Log.v(TAG, "unSelect: word=" + mToken.getOriginalWord());
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
