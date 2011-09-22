package com.havedroid.dddsched;

import java.util.Calendar;
import java.util.Date;

import com.havedroid.dddsched.data.Schedule;
import com.havedroid.dddsched.data.Session;
import com.havedroid.dddsched.data.SessionSlot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DDDNorthScheduleActivity extends Activity {
    private static final int GRACE_TIME_BEFORE_MOVING_TO_NEXT_SESSION = 10;
	private Button mShowSchedule;
	private Context mContext;
	private TextView mSessionTitle;
	private TextView mShortInfo;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContext = getApplicationContext();
        
        mSessionTitle = (TextView)findViewById(R.id.next_session_title);
		mShortInfo = (TextView)findViewById(R.id.next_session_short_info);
		mShowSchedule = (Button)findViewById(R.id.ShowSchedule);
        mShowSchedule.setOnClickListener(showSchduleClick);
        
        displayNextSession();
    }
    
	@Override
	public void onResume(){
		super.onResume();
		displayNextSession();
	}
	
    private void displayNextSession() {
		Session session = getNextSession();
    	
    	if(session != null){
			mSessionTitle.setText(session.getTitle());
			mShortInfo.setText("In room " + session.getRoom() + " by " + session.getSpeaker());
		}else{
			mSessionTitle.setText("You haven't selected anything yet :(");
			mShortInfo.setText("We have lots of great sessions, honest!");
		}
	}

	private Session getNextSession(){
    	long smallestDifference = -1;
    	Session nextSession = null; 
    	
    	long timeToNextSession = 0;
    	for(SessionSlot slot : Schedule.getSchedule(mContext)){
    		for(Session session : slot.getSessions()){
    			if(session.getAttending(mContext)){
    				timeToNextSession = getUtcDifferenceToDate(session.getStartTime());
    				if(smallestDifference == -1 || smallestDifference > timeToNextSession){
    					nextSession = session;
    					smallestDifference = timeToNextSession;
    				}
    			}
    		}
    	}
    	
    	return nextSession;
    }
    
    private long getUtcDifferenceToDate(Date startTime) {
    	Calendar calendar = Calendar.getInstance();
    	long now = calendar.getTimeInMillis();
    	
    	calendar.setTime(startTime);
    	calendar.add(Calendar.MINUTE, GRACE_TIME_BEFORE_MOVING_TO_NEXT_SESSION);
    	
    	return  calendar.getTimeInMillis() - now;
	}

	private OnClickListener showSchduleClick = new OnClickListener(){
		public void onClick(View sender) {
			Intent showScheduleIntent = new Intent(mContext, ScheduleActivity.class);
			startActivity(showScheduleIntent);
		}
    };
    
}