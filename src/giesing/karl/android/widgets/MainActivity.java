package giesing.karl.android.widgets;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.SeekBar;

public class MainActivity extends Activity {
	Knob widget;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		widget = (Knob) findViewById(R.id.widget);
		widget.setOnKnobChangeListener(new Knob.OnKnobChangeListener() {
			
			@Override
			public void onStopTrackingTouch(Knob knob) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onStartTrackingTouch(Knob knob) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLevelChanged(Knob knob, float progress,
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
