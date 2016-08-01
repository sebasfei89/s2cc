package tesis.s2cc;

public class CCTokenView /*implements View.OnClickListener, View.OnLongClickListener, View.OnFocusChangeListener*/ {

	private CCToken mToken;

	public CCTokenView( CCToken token, RecognitionActivity activity, OldClosedCaptionGenerator ccGenerator ) {
		mToken = token;

//		mActivity = activity;
//		mContainer = (FlowLayout) mActivity.findViewById(R.id.RecognitionResults);
//		mCCGenerator = ccGenerator;
//
//		mView = new EditText(mActivity);
//		mView.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
//			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//				return false;
//			}
//			public void onDestroyActionMode(ActionMode mode) {
//			}
//			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//				return false;
//			}
//			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//				return false;
//			}
//		});
//		mView.setInputType(InputType.TYPE_CLASS_TEXT);
//		mView.setFocusable(true);
//		mView.setFocusableInTouchMode(true);
//		mView.setImeOptions(EditorInfo.IME_ACTION_DONE);
//		mView.setCursorVisible(false);
//		mView.setTextIsSelectable(true);
//		mView.setBackgroundColor(Color.TRANSPARENT);
//		mView.setText(mOriginalWord);
//		int spacing = mActivity.getResources().getDimensionPixelSize(R.dimen.word_spacing);
//		mView.setPadding(spacing, 0, spacing, 0);
//		mView.setTextColor(Color.BLACK);
//		mView.setLongClickable(true);
//		mView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
//		mView.setLayoutParams(new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//		mView.setOnFocusChangeListener(this);
//		mView.setOnClickListener(this);
//		mView.setOnLongClickListener(this);
//		mContainer.addView(mView);
//
//		mSepView = new TextView(mActivity);
//		mSepView.setText(R.string.ellipsis);
//		mSepView.setTextColor(Color.BLACK);
//		mSepView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
//		mSepView.setBackgroundResource(R.drawable.border_background);
//		mSepView.setLayoutParams(new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//		mContainer.addView(mSepView);
	}

//	public String text() {
//		return mView.getText().toString();
//	}
//
//	public View getView() {
//		return mView;
//	}
//
//	public void updateWord( String word ) {
//		Log.v(TAG, "update: mOriginalWord=" + mOriginalWord + ", mView.text=" + mView.getText() + ", word=" + word);
//		if (mOriginalWord.equals(mView.getText().toString())) {
//			mOriginalWord = word;
//			mView.setText(mOriginalWord);
//		}
//	}
//
//	public void showKeyboard() {
//		final Handler handler = new Handler();
//		handler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				mView.selectAll();
//				InputMethodManager keyboard = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
//				keyboard.showSoftInput(mView, InputMethodManager.SHOW_IMPLICIT);
//			}
//		}, 60);
//	}
//
//	@Override
//	public boolean onLongClick(View view) {
//		mContainer.removeView(mView);
//		mContainer.removeView(mSepView);
//		mCCGenerator.removeWord(this);
//		return true;
//	}
//
//	@Override
//	public void onFocusChange(View view, boolean focused) {
//		Log.v(TAG, "onFocusChange: word=" + text() + ", focused=" + focused);
//		if (focused) {
//			showKeyboard();
//		}
//	}
//
//	@Override
//	public void onClick(View view) {
//		Log.v(TAG, "onClick: word=" + text());
//		if (mView.isFocused()) {
//			InputMethodManager keyboard = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
//			keyboard.hideSoftInputFromWindow(mView.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
//			final Handler handler = new Handler();
//			handler.postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					mActivity.findViewById(R.id.ToggleRecognitionBtn).requestFocus();
//				}
//			}, 500);
//		}
//	}
}
