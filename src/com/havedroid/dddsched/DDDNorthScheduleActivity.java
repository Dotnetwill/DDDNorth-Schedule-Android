package com.havedroid.dddsched;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.havedroid.dddsched.Util.*;
import com.havedroid.dddsched.data.Schedule;
import com.havedroid.dddsched.data.Session;
import com.havedroid.dddsched.data.SessionSlot;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DDDNorthScheduleActivity extends Activity {
    private static final int GRACE_TIME_BEFORE_MOVING_TO_NEXT_SESSION = 10;
	private Button mShowSchedule;
	private Context mContext;
	private TextView mSessionTitle;
	private TextView mShortInfo;
	private DDDNorthTwitter mTwitter;
    private TextView tweetPerson;
    private ImageView tweetImage;
    private TextView tweetContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContext = getApplicationContext();
        
        SetupAppContants();
        
        mSessionTitle = (TextView)findViewById(R.id.next_session_title);
		mShortInfo = (TextView)findViewById(R.id.next_session_short_info);
		mShowSchedule = (Button)findViewById(R.id.ShowSchedule);
        mShowSchedule.setOnClickListener(showSchduleClick);

        tweetContent = (TextView)findViewById(R.id.Detail_TweetContent);
        tweetImage = (ImageView)findViewById(R.id.Twitterer_Pic);
        tweetPerson = (TextView)findViewById(R.id.Tweet_Twitterer);

        mTwitter = new DDDNorthTwitter(getApplicationContext());

        displayNextSession();
        loadHaloTweet();
        new AsyncTwitterUpdate(twitterUpdated).execute(mTwitter);
    }

    private void loadHaloTweet() {
        DDDTweet tweet = mTwitter.getHaloTweet();
        if(tweet != null){
            tweetContent.setText(tweet.content);
            tweetPerson.setText(tweet.user);
            tweetImage.setTag(tweet.profileImageUrl.replace("normal", "bigger"));
            new AsyncImageViewLoader(getApplicationContext()).execute(tweetImage);
        }
    }

    private void SetupAppContants() {
		//this is terrible I know but it's quick and easy and I'm in a pragmatic mood
		Constants.SCHEDULE = mContext.getString(R.string.schedule);
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
    	
    	long timeToNextSession;
    	for(SessionSlot slot : Schedule.getSchedule(getSharedPreferences(Constants.SHARED_PREFS_KEY, Context.MODE_PRIVATE), true)){
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

    private TwitterUpdateComplete twitterUpdated = new TwitterUpdateComplete() {
        @Override
        public void onCompleted(List<DDDTweet> tweets) {
            loadHaloTweet();
        }
    };


    
}