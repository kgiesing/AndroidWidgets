package giesing.karl.android.widgets;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {
	Knob widget;
	SeekBar seekBar;
	TextView tvProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tvProgress = (TextView) findViewById(R.id.tvProgress);
		seekBar = (SeekBar) findViewById(R.id.seekBar1);
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// no-op
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// no-op
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// Set progress in knob
				if (fromUser) {
					widget.setLevel(progress);
					tvProgress.setText("progress: " + progress);
				}
			}
		});
		widget = (Knob) findViewById(R.id.widget);
		widget.setOnKnobChangeListener(new Knob.OnKnobChangeListener() {
			
			@Override
			public void onStopTrackingTouch(Knob knob) {
				// no-op
			}
			
			@Override
			public void onStartTrackingTouch(Knob knob) {
				// no-op
			}
			
			@Override
			public void onLevelChanged(Knob knob, float level, boolean fromUser) {
				if (fromUser) {
					seekBar.setProgress((int) level);
					tvProgress.setText("level: " + level);
				}
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
