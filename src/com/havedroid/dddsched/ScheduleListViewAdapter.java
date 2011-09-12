package com.havedroid.dddsched;

import com.havedroid.dddsched.data.Session;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class ScheduleListViewAdapter extends BaseAdapter {
	
	protected Context mContext;
	protected Session[] mSessions;
	
	public ScheduleListViewAdapter(ScheduleActivity context, Session[] sessions) {
		mContext = context;
		mSessions = sessions;
	}
	

	public int getCount() {
		return mSessions.length;
	}

	public Object getItem(int index) {
		return mSessions[index];
	}

	public long getItemId(int index) {
		int id = mSessions[index].getId();
		return (long)id;
	}

	public View getView(int index, View view, ViewGroup viewGroup) {
		ViewHolder viewHolder;
		if(view == null)
		{
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.sessionview, null);
			
			viewHolder = new ViewHolder();
			viewHolder.Title = (TextView)view.findViewById(R.id.Detail_SessionTitle);
			viewHolder.ClippDesc = (TextView)view.findViewById(R.id.Detail_ClippedSessionDesc);
			viewHolder.Room = (TextView)view.findViewById(R.id.Detail_SessionRoom);
			viewHolder.Speaker = (TextView)view.findViewById(R.id.Detail_SessionSpeaker);
			
			viewHolder.Attend = (CheckBox)view.findViewById(R.id.Detail_Attend);
			viewHolder.Attend.setOnClickListener(attendClick);
			viewHolder.Attend.setTag(mSessions[index]);
			
			view.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)view.getTag();
		}
		
		Session curSession = mSessions[index];
		
		viewHolder.Attend.setChecked(curSession.getAttending(mContext));
		viewHolder.Attend.setTag(mSessions[index]);
		
		viewHolder.Title.setText(curSession.getTitle());
		viewHolder.ClippDesc.setText(curSession.getDesc());
		viewHolder.Room.setText(curSession.getRoom());
		viewHolder.Speaker.setText(curSession.getSpeaker());
		
		return view;
	}
	
	private static class ViewHolder{
		public TextView Title;
		public TextView ClippDesc;
		public TextView Room;
		public TextView Speaker;
		public CheckBox Attend;
	}

	private View.OnClickListener attendClick = new View.OnClickListener() {
		public void onClick(View v){
			Session session = (Session)v.getTag();
			session.setAttending(mContext, !session.getAttending(mContext));
		}
	};
}
