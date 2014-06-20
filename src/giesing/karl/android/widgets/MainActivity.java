package giesing.karl.android.widgets;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.SeekBar;

public class MainActivity extends Activity {
	RotaryKnob widget;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		widget = (RotaryKnob) findViewById(R.id.widget);
		widget.setImageResource(R.drawable.ic_launcher);
		widget.setOnRotaryKnobChangeListener(new RotaryKnob.OnRotaryKnobChangeListener() {
			
			@Override
			public void onStopTrackingTouch(RotaryKnob knob) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(RotaryKnob knob) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(RotaryKnob knob, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				
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
