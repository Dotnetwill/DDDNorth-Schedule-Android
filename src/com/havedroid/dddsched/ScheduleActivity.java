package com.havedroid.dddsched;

import android.app.TabActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;
import com.havedroid.dddsched.Util.DDDNorthTwitter;
import com.havedroid.dddsched.Util.UpdateSchedule;
import com.havedroid.dddsched.data.Schedule;
import com.havedroid.dddsched.data.SessionSlot;

public class ScheduleActivity extends TabActivity  {
	
	private final String ALL_SCHEDULE_TAG = "overall";
	private final String MY_SCHEDULE_TAG = "myschedule";
    private final String DDD_TWEET_TAG = "dddtweets";

	private SectionedListAdapter mAllAdapter;
	private SectionedListAdapter mMySessionsAdapter;
	private DDDTweetListAdapter mTweetListAdapter;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTabs();
        setupListAdapters(false);
        updateSessionList.execute();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

	private void setupListAdapters(Boolean reload) {
		ListView allScheduleList = (ListView)findViewById(R.id.AllScheduleListView);
		allScheduleList.setAdapter(getAllSessionListAdapter(reload));
	
		ListView myScheduleList = (ListView)findViewById(R.id.MyScheduleListView);
		myScheduleList.setAdapter(getMySessionsAdapter(reload));

        ListView tweetListView = (ListView)findViewById(R.id.DDDTweetsView);
        tweetListView.setAdapter(getTweetAdapater());
	}

    private ListAdapter getTweetAdapater() {
        if(mTweetListAdapter == null){
            Context context = getApplicationContext();
            mTweetListAdapter = new DDDTweetListAdapter(context,
                                                        new DDDNorthTwitter(context),
                                                        getLayoutInflater());
        }
        return mTweetListAdapter;
    }

    private void setupTabs() {
		TabHost tabs = getTabHost();
        TabHost.TabSpec tab;
        
        tabs.setOnTabChangedListener(mTabChangeListener);
        
        getLayoutInflater().inflate(
                R.layout.scheduleview,
                tabs.getTabContentView(),
                true);
        
        tab = tabs.newTabSpec(ALL_SCHEDULE_TAG);
       
        tab.setContent(R.id.AllScheduleLayout);
        tab.setIndicator("All Sessions");
        tabs.addTab(tab);
        
        tab = tabs.newTabSpec(MY_SCHEDULE_TAG);
        tab.setContent(R.id.MyScheduleLayout);
        tab.setIndicator("My Sessions");
        tabs.addTab(tab);

        tab = tabs.newTabSpec(DDD_TWEET_TAG);
        tab.setContent(R.id.DDDTweetsLayout);
        tab.setIndicator("#DDDNorth");
        tabs.addTab(tab);
    }
	
	private TabHost.OnTabChangeListener mTabChangeListener = new TabHost.OnTabChangeListener() {
		
		public void onTabChanged(String tabId) {
			//Total hack but update the lists as we move between tabs in case something has changed
			Log.d(Constants.LOG_TAG, "notified of change to new tab:" +tabId);
			
			if(tabId.equals(ALL_SCHEDULE_TAG)){
				if(mAllAdapter != null){
					mAllAdapter.notifyAllDatasetChanges();
				}
			}else if(tabId.equals(MY_SCHEDULE_TAG)){
				if(mMySessionsAdapter != null){
					mMySessionsAdapter.notifyAllDatasetChanges();
				}
			}else
            {
             if(mTweetListAdapter != null){
                mTweetListAdapter.refresh();
             }
            }
		}
	};
	
	private ListAdapter getAllSessionListAdapter(Boolean reload){
		if(mAllAdapter == null || reload){
			mAllAdapter = new SectionedListAdapter(this);
			for(SessionSlot slot : getSessionSlots()){
				ScheduleListViewAdapter sessionAdapter = new ScheduleListViewAdapter(this, slot.getSessions());
				mAllAdapter.addSection(slot.getSessionSlotDisplayName(), sessionAdapter);
			}
		}
		return mAllAdapter;
	}
	
	private ListAdapter getMySessionsAdapter(Boolean reload){
		if(mMySessionsAdapter == null || reload){
			mMySessionsAdapter = new SectionedListAdapter(this);
			for(SessionSlot slot : getSessionSlots()){
				AttendingSessionsListViewAdapter sessionAdapter = new AttendingSessionsListViewAdapter(this, slot.getSessions());
				mMySessionsAdapter.addSection(slot.getSessionSlotDisplayName(), sessionAdapter);
			}
		}
		return mMySessionsAdapter;
	}

    private Iterable<SessionSlot> getSessionSlots()
    {
        return Schedule.getSchedule(getSharedPreferences(Constants.SHARED_PREFS_KEY, Context.MODE_PRIVATE), true);
    }

    private AsyncTask<Object, Object, Boolean> updateSessionList = new AsyncTask<Object, Object, Boolean>(){

        @Override
        protected Boolean doInBackground(Object... objects) {
            return UpdateSchedule.CheckForUpdate(getSharedPreferences("Session", Context.MODE_PRIVATE));
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(success){
                setupListAdapters(true);
                Log.v(Constants.LOG_TAG, "Reloaded list adapters");
                Toast.makeText(getApplicationContext(), "Schedule Updated!", 20);
            }
        }
    };
}
