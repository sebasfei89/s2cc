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
import android.widget.TextView;

import com.wefika.flowlayout.FlowLayout;

public class RecognizedWord implements View.OnClickListener, View.OnLongClickListener, View.OnFocusChangeListener {

	private static final String TAG = "RecognizedWord";

	private RecognitionActivity mActivity;
	private EditText mView;
	private TextView mSepView;
	private ViewGroup mContainer;
	private ClosedCaptionGenerator mCCGenerator;

	RecognizedWord(String word, RecognitionActivity activity, ClosedCaptionGenerator ccGenerator) {
		mActivity = activity;
		mContainer = (FlowLayout) mActivity.findViewById(R.id.RecognitionResults);
		mCCGenerator = ccGenerator;

		mView = new EditText(mActivity);
		mView.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}
			public void onDestroyActionMode(ActionMode mode) {
			}
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				return false;
			}
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				return false;
			}
		});
		mView.setInputType(InputType.TYPE_CLASS_TEXT);
		mView.setFocusable(true);
		mView.setFocusableInTouchMode(true);
		mView.setImeOptions(EditorInfo.IME_ACTION_DONE);
		mView.setCursorVisible(false);
		mView.setTextIsSelectable(true);
		mView.setBackgroundColor(Color.TRANSPARENT);
		mView.setText(word);
		int spacing = mActivity.getResources().getDimensionPixelSize(R.dimen.word_spacing);
		mView.setPadding(spacing, 0, spacing, 0);
		mView.setTextColor(Color.BLACK);
		mView.setLongClickable(true);
		mView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
		mView.setLayoutParams(new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		mView.setOnFocusChangeListener(this);
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

	public View getView() {
		return mView;
	}

	public void update(String word) {
		Log.v(TAG, "update: word=" + mView.getText() + ", updated_word=" + word);
		mView.setText(word);
	}

	public void showKeyboard() {
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mView.selectAll();
				InputMethodManager keyboard = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
				keyboard.showSoftInput(mView, InputMethodManager.SHOW_IMPLICIT);
			}
		}, 60);
	}

	@Override
	public boolean onLongClick(View view) {
		mContainer.removeView(mView);
		mContainer.removeView(mSepView);
		mCCGenerator.removeWord(this);
		return true;
	}

	@Override
	public void onFocusChange(View view, boolean focused) {
		Log.v(TAG, "onFocusChange: word=" + text() + ", focused=" + focused);
		if (focused) {
			showKeyboard();
		}
	}

	@Override
	public void onClick(View view) {
		Log.v(TAG, "onClick: word=" + text());
		if (mView.isFocused()) {
			InputMethodManager keyboard = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
			keyboard.hideSoftInputFromWindow(mView.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mActivity.findViewById(R.id.ToggleRecognitionBtn).requestFocus();
				}
			}, 500);
		}
	}
}
