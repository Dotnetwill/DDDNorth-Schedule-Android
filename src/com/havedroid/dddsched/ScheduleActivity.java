package com.havedroid.dddsched;

import com.havedroid.dddsched.data.Schedule;
import com.havedroid.dddsched.data.SessionSlot;

import android.app.TabActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;

public class ScheduleActivity extends TabActivity  {
	
	private final String ALL_SCHEDULE_TAG = "overall";
	private final String MY_SCHEDULE_TAG = "myschedule";
	
	private SectionedListAdapter mAllAdapter;
	private SectionedListAdapter mMySessionsAdapter;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTabs();
        setupListAdapters();
         
    }

	private void setupListAdapters() {
		ListView allScheduleList = (ListView)findViewById(R.id.AllScheduleListView);
		allScheduleList.setAdapter(getAllSessionListAdapter());
	
		ListView myScheduleList = (ListView)findViewById(R.id.MyScheduleListView);
		myScheduleList.setAdapter(getMySessionsAdapter());
		
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
	}
	
	private TabHost.OnTabChangeListener mTabChangeListener = new TabHost.OnTabChangeListener() {
		
		public void onTabChanged(String tabId) {
			//Total hack but update the lists as we move between tabs in case something has changed
			Log.d(Constants.LOG_TAG, "notified of change to new tab:" +tabId);
			
			if(tabId.equals(ALL_SCHEDULE_TAG)){
				if(mAllAdapter != null){
					mAllAdapter.notifyAllDatasetChanges();
				}
			}else{
				if(mMySessionsAdapter != null){
					mMySessionsAdapter.notifyAllDatasetChanges();
				}
			}
		}
	};
	
	private ListAdapter getAllSessionListAdapter(){
		if(mAllAdapter == null){
			mAllAdapter = new SectionedListAdapter(this);
			ListAdapter filteredList = getMySessionsAdapter();
			mAllAdapter.setFilteredList(filteredList);
			for(SessionSlot slot : Schedule.getSchedule(this)){
				ScheduleListViewAdapter sessionAdapter = new ScheduleListViewAdapter(this, slot.getSessions());
				mAllAdapter.addSection(slot.getSessionSlotDisplayName(), sessionAdapter);
			}
		}
		return mAllAdapter;
	}
	
	private ListAdapter getMySessionsAdapter(){
		if(mMySessionsAdapter == null){
			mMySessionsAdapter = new SectionedListAdapter(this);
			for(SessionSlot slot : Schedule.getSchedule(this)){
				AttendingSessionsListViewAdapter sessionAdapter = new AttendingSessionsListViewAdapter(this, slot.getSessions().clone());
				mMySessionsAdapter.addSection(slot.getSessionSlotDisplayName(), sessionAdapter);
			}
		}
		return mMySessionsAdapter;
	}
}
