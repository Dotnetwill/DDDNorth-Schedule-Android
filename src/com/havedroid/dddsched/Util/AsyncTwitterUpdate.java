package com.havedroid.dddsched.Util;

import android.os.AsyncTask;

import java.util.List;

public class AsyncTwitterUpdate extends AsyncTask<DDDNorthTwitter, String, List<DDDTweet>> {

    private final TwitterUpdateComplete completeHandler;

    public AsyncTwitterUpdate(TwitterUpdateComplete completeHandler){

        this.completeHandler = completeHandler;
    }

    @Override
    protected List<DDDTweet> doInBackground(DDDNorthTwitter... twitters) {
        DDDNorthTwitter twitter = twitters[0];
        return twitter.requeryAllTweets();
    }

    @Override
    protected void onPostExecute(List<DDDTweet> tweets){
            completeHandler.onCompleted(tweets);
    }


}
