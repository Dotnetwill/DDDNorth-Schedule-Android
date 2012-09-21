package com.havedroid.dddsched.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.havedroid.dddsched.Constants;
import twitter4j.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DDDNorthTwitter {

    private static final Twitter twitter = new TwitterFactory().getInstance();
    private static final String TWITTER_HASTAG = "#DDDNorth";
    private static final String TWITTER_LAST_TWEET_ID = "LAST_TWEET_ID";
    private static final String LAST_HALO_ID_PREF = "HALO_TWEET_ID";
    private static final String TWEET_FILENAME = ".tweets";
    private static final String TWEET_ID_CIRCLE_PREF_KEY = "HALO_TWEET_CIRCLE";
    private ArrayList<DDDTweet> tweets;
    private final Context context;

    public DDDNorthTwitter(Context context){

        this.context = context;
    }

    public List<DDDTweet> getCachedTweets(){
        loadTweets();

        return tweets;
    }

    public DDDTweet getHaloTweet(){
        loadTweets();

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        Long lastId = sharedPreferences.getLong(LAST_HALO_ID_PREF, 0);
        String circleIds = sharedPreferences.getString(TWEET_ID_CIRCLE_PREF_KEY, "");

        DDDTweet displayTweet = null;
        final int count  = tweets.size() -1;
        for(int i = count; i >= 0; i--){
            DDDTweet tweet = tweets.get(i);
            if(tweet.id == lastId) continue;
            //Anything that hasn't been in the circle display
            if(!circleIds.contains(String.valueOf(tweet.id) + ",")){
                displayTweet = tweet;
                circleIds = circleIds + String.valueOf(tweet.id) + ",";
                break;
            }
            //The idea here is that tweet ids go up and up and we store the last id we seen
            //so we append the id of every tweet to the circle id list then wrap round when we reach the end.
            //When we wrap we end up with a low tweet ID and all the others are higher so we step through.
            //Make sense? Probably not I'm shit with English
            if(tweet.id > lastId){
                displayTweet = tweet;
                break;
            }
        }

        if(displayTweet == null && tweets.size() > 0){
            displayTweet = tweets.get(0);
        }

        if(displayTweet != null){
            lastId = displayTweet.id;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(TWEET_ID_CIRCLE_PREF_KEY, circleIds);
            editor.putLong(LAST_HALO_ID_PREF, lastId);
            editor.commit();
        }

        return displayTweet;
    }

    private void loadTweets() {
        if(tweets == null){
            ArrayList<DDDTweet> tweetList = FileHelper.LoadFile(context, TWEET_FILENAME);
            if(tweetList == null){
                tweets = new ArrayList<DDDTweet>();
            }else{
                tweets = tweetList;
            }
        }
    }

    public List<DDDTweet> requeryAllTweets(){
        Log.v(Constants.LOG_TAG, "requeryAllTweets Called");
        loadTweets();
        try {
            Query query = new Query(TWITTER_HASTAG);
            QueryResult res = twitter.search(query);
            Long lastTweetId = getLastTweetId();
            query.setSinceId(lastTweetId);

            Tweet tweet;
            List<Tweet> newTweets = res.getTweets();
            for(int i=newTweets.size() - 1; i >= 0; i--){
                tweet = newTweets.get(i);
                DDDTweet dddTweet = new DDDTweet(tweet.getFromUser(),
                        tweet.getText(),
                        tweet.getProfileImageUrl(),
                        tweet.getCreatedAt(),
                        tweet.getId());

                if(tweet.getId() > lastTweetId){
                    lastTweetId = tweet.getId();
                }
                tweets.add(0, dddTweet);
            }
            saveLastTweetId(lastTweetId);
            FileHelper.SaveFile(context, TWEET_FILENAME, (Serializable)tweets.clone());
            Log.v(Constants.LOG_TAG, "requeryAllTweets exited with tweets");
            return tweets;
        }catch (TwitterException e) {
            Log.e(Constants.LOG_TAG, "Couldn't search Twitter: " + e);
        }
        return null;
    }

    private void saveLastTweetId(Long lastTweetId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREFS_KEY, Context.MODE_PRIVATE)
                                                 .edit();

        editor.putLong(TWITTER_LAST_TWEET_ID, lastTweetId);
    }

    private long getLastTweetId() {
        SharedPreferences pref = context.getSharedPreferences(Constants.SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        return pref.getLong(TWITTER_LAST_TWEET_ID, 0);
    }
}

