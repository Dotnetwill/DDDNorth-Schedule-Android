package com.havedroid.dddsched;

import com.havedroid.dddsched.data.Session;

public class AttendingSessionsListViewAdapter extends ScheduleListViewAdapter {

	public AttendingSessionsListViewAdapter(ScheduleActivity context,
			Session[] sessions) {
		super(context, sessions);
	}
	
	public int getCount() {
		int count = 0;
		for(Session session : mSessions){
			
		}
		
		return count;
	}

	public Object getItem(int index) {
		return mSessions[index];
	}

	public long getItemId(int index) {
		int id = mSessions[index].getId();
		return (long)id;
	}

}
