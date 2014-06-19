package giesing.karl.android.widgets;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.SeekBar;

public class MainActivity extends Activity {
	SeekBar vertSeekBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		vertSeekBar = (SeekBar) findViewById(R.id.vertSeekBar);
		vertSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Log.i("MainActivity", "onStopTrackingTouch");
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				Log.i("MainActivity", "onStartTrackingTouch");
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				Log.i("MainActivity", "onProgressChanged: " + progress);
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Kill the app when back or home pressed
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
		case KeyEvent.KEYCODE_HOME:
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
