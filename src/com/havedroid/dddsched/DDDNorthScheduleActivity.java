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
    private TextView latestTweetLabel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContext = getApplicationContext();

        mSessionTitle = (TextView)findViewById(R.id.next_session_title);
		mShortInfo = (TextView)findViewById(R.id.next_session_short_info);
		mShowSchedule = (Button)findViewById(R.id.ShowSchedule);
        mShowSchedule.setOnClickListener(showSchduleClick);

        tweetContent = (TextView)findViewById(R.id.Detail_TweetContent);
        tweetImage = (ImageView)findViewById(R.id.Twitterer_Pic);
        tweetPerson = (TextView)findViewById(R.id.Tweet_Twitterer);
        latestTweetLabel = (TextView)findViewById(R.id.latestLabel);

        mTwitter = new DDDNorthTwitter(getApplicationContext());

        displayNextSession();
        loadHaloTweet();
        new AsyncTwitterUpdate(twitterUpdated).execute(mTwitter);
    }

    private void loadHaloTweet() {
        DDDTweet tweet = mTwitter.getHaloTweet();
        if(tweet != null){
            tweetContent.setVisibility(View.VISIBLE);
            tweetPerson.setVisibility(View.VISIBLE);
            tweetImage.setVisibility(View.VISIBLE);
            latestTweetLabel.setVisibility(View.VISIBLE);

            tweetContent.setText(tweet.content);
            tweetPerson.setText(tweet.user);
            String imageUrl = tweet.profileImageUrl.replace("normal", "bigger");
            tweetImage.setTag(imageUrl);
            tweetImage.setImageResource(R.drawable.user_placeholder);
            new AsyncImageViewLoader(getApplicationContext(), tweetImage).execute(imageUrl);
        }else{
            tweetContent.setVisibility(View.INVISIBLE);
            tweetPerson.setVisibility(View.INVISIBLE);
            tweetImage.setVisibility(View.INVISIBLE);
            latestTweetLabel.setVisibility(View.INVISIBLE);
        }
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
    	for(SessionSlot slot : Schedule.getSchedule(getApplicationContext(), true)){
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
            //Only update if we have new tweets
            if(tweets != null && tweets.size() > 0)
                loadHaloTweet();
        }
    };


    
}