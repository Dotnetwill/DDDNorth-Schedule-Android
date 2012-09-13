package com.havedroid.dddsched;

import android.content.Context;
import android.util.Log;
import com.havedroid.dddsched.data.Session;

public class AttendingSessionsListViewAdapter extends ScheduleListViewAdapter {

	public AttendingSessionsListViewAdapter(Context context,
			Session[] sessions) {
		super(context, sessions);
	}
	
	@Override
	public int getCount() {
		int count = 0;
		for(Session session : mSessions){
			if(session.getAttending(mContext)){
				count++;
			}
		}
		
		return count;
	}

	@Override
	public Object getItem(int index) {
		int count = -1;
		
		for(Session session : mSessions){
			if(session.getAttending(mContext)){
				count++;
			}
			if(count == index){
				return session;
			}
				
		}
		
		Log.e(Constants.LOG_TAG, "AttendingSessionsListAdapter::GetItem(): unable to resolve item with item index " + index);
		
		return null;
	}


}
