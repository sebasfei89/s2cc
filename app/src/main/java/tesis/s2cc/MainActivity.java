package tesis.s2cc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
	}

	public void startRecognitionActivity(View view) {
		Intent intent = new Intent(this, RecognitionActivity.class);
		int mode = (view == findViewById(R.id.demoModeBtn)) ? RecognitionActivity.DEMO_MODE : RecognitionActivity.REMOTE_MODE;
		intent.putExtra(RecognitionActivity.MODE, mode);
		startActivity(intent);
	}
}
