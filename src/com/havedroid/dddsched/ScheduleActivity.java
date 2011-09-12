package com.havedroid.dddsched;

import com.havedroid.dddsched.data.Schedule;
import com.havedroid.dddsched.data.SessionSlot;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;

public class ScheduleActivity extends Activity implements TabHost.TabContentFactory {
	
	private final String ALL_SCHEDULE_TAG = "overall";
	private final String MY_SCHEDULE_TAG = "myschedule";
	
	private ListView mScheduleView;
	private SectionedListAdapter mAllAdapter;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scheduleview);
        mScheduleView = (ListView)findViewById(R.id.ScheduleListView);
        setupTabs();
    }

	private void setupTabs() {
		TabHost tabs = (TabHost)findViewById(R.id.ScheduleTabHost);
        TabHost.TabSpec tab;
        tabs.setup();

        tab = tabs.newTabSpec(ALL_SCHEDULE_TAG);

        tab.setContent(this);
        tab.setIndicator("All Sessions");
        tabs.addTab(tab);
        
        tab = tabs.newTabSpec(MY_SCHEDULE_TAG);
        tab.setContent(this);
        tab.setIndicator("My Sessions");
        tabs.addTab(tab);
	}

	public View createTabContent(String tabTag) {
		ListAdapter curAdapter = null;
		if(tabTag == ALL_SCHEDULE_TAG){
			curAdapter = getAllSessionListAdapter();
		}
		
		mScheduleView.setAdapter(curAdapter);
		
		return mScheduleView;
	}
	
	private ListAdapter getAllSessionListAdapter(){
		if(mAllAdapter == null){
			mAllAdapter = new SectionedListAdapter(this);
			
			for(SessionSlot slot : Schedule.getSchedule(this)){
				ScheduleListViewAdapter sessionAdapter = new ScheduleListViewAdapter(this, slot.getSessions());
				mAllAdapter.addSection(slot.getSessionSlotDisplayName(), sessionAdapter);
			}
		}
		return mAllAdapter;
	}
}
