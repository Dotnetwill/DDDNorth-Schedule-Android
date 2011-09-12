package com.havedroid.dddsched;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DDDNorthScheduleActivity extends Activity {
    private Button mShowSchedule;
	private Context mContext;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContext = getApplicationContext();
        
        mShowSchedule = (Button)findViewById(R.id.ShowSchedule);
        mShowSchedule.setOnClickListener(showSchduleClick);
    }
    
    private OnClickListener showSchduleClick = new OnClickListener(){
		public void onClick(View sender) {
			Intent showScheduleIntent = new Intent(mContext, ScheduleActivity.class);
			startActivity(showScheduleIntent);
		}
    };
    
}