package com.havedroid.dddsched;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.havedroid.dddsched.Util.*;

import java.util.List;

public class DDDTweetListAdapter extends BaseAdapter {

    private final Context context;
    private final DDDNorthTwitter twitter;
    private final LayoutInflater inflater;
    private List<DDDTweet> tweets;

    public DDDTweetListAdapter(Context context, DDDNorthTwitter twitter, LayoutInflater inflater) {
        this.context = context;
        this.twitter = twitter;
        this.inflater = inflater;
        tweets = twitter.getCachedTweets();
    }

    @Override
    public int getCount() {
        if (tweets != null) {
            return tweets.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int i) {
        return tweets.get(i);
    }

    @Override
    public long getItemId(int i) {
        return tweets.get(i).id;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        DDDTweetViewHolder holder = new DDDTweetViewHolder();
        if (view == null) {
            view = inflater.inflate(R.layout.dddtweet, null);
            holder.Content = (TextView) view.findViewById(R.id.Detail_TweetContent);
            holder.ImageView = (ImageView) view.findViewById(R.id.Twitterer_Pic);
            holder.Tweeterer = (TextView) view.findViewById(R.id.Tweet_Twitterer);

            view.setTag(holder);
        } else {
            holder = (DDDTweetViewHolder) view.getTag();
        }

        DDDTweet tweet = tweets.get(i);
        loadImage(holder.ImageView, tweet.profileImageUrl);
        holder.Content.setText(tweet.content);
        holder.Tweeterer.setText(tweet.user);

        return view;
    }

    private void loadImage(ImageView imageView, String profileImageUrl) {
        profileImageUrl = profileImageUrl.replace("normal", "bigger");
        imageView.setImageResource(R.drawable.user_placeholder);
        imageView.setTag(profileImageUrl);
        new AsyncImageViewLoader(context, imageView).execute(profileImageUrl);
    }

    public void refresh() {
        new AsyncTwitterUpdate(updateComplete).execute(twitter);
    }

    private static class DDDTweetViewHolder {
        public ImageView ImageView;
        public TextView Tweeterer;
        public TextView Content;
    }

    private TwitterUpdateComplete updateComplete = new TwitterUpdateComplete() {
        @Override
        public void onCompleted(List<DDDTweet> newTweets) {
            tweets = newTweets;
            notifyDataSetChanged();
        }
    };

}
