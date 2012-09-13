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
        if(tweets != null && tweets.size() > 0){
            Long lastId = context.getSharedPreferences(Constants.SHARED_PREFS_KEY, Context.MODE_PRIVATE).getLong(LAST_HALO_ID_PREF, 0);
            if(tweets.get(0).id > lastId){
                DDDTweet tweet = tweets.get(0);
                updateHaloCircle(tweet);
                return tweet;
            }else{
                DDDTweet tweet = findTweetById(lastId);
                if(tweet == null){
                    tweet = getTweetFromCircle();
                    //if we still haven't found one to use
                    if(tweet == null){
                        for(DDDTweet haloTweet : tweets){
                            if(haloTweet.id == lastId) continue;
                            tweet = haloTweet;
                            break;
                        }
                    }
                }
                updateHaloCircle(tweet);
                return tweet;
            }
        }
        return null;
    }

    private DDDTweet getTweetFromCircle(){
        String circleIds = context.getSharedPreferences(Constants.SHARED_PREFS_KEY, Context.MODE_PRIVATE)
                .getString(TWEET_ID_CIRCLE_PREF_KEY, "");

        for(DDDTweet storedTweet : tweets){
            if(!circleIds.contains(storedTweet.id + ",")){
                return storedTweet;
            }
        }
        return null;
    }

    private void updateHaloCircle(DDDTweet tweet) {
        if(tweet == null) return;

        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        String circleIds = prefs.getString(TWEET_ID_CIRCLE_PREF_KEY, "");

        if(circleIds.contains(tweet.id + ",")){
           circleIds = circleIds.replace(tweet.id + ",", "");
        }
        circleIds = tweet.id + "," + circleIds;

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(TWEET_ID_CIRCLE_PREF_KEY, circleIds);
        editor.putLong(LAST_HALO_ID_PREF, tweet.id);
        editor.commit();
    }

    private DDDTweet findTweetById(Long lastId) {
        for(DDDTweet tweet : tweets){
            if(tweet.id > lastId)
                return tweet;
        }
        return null;
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

