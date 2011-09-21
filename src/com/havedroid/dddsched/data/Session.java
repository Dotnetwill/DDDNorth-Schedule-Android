package com.havedroid.dddsched.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Session {
	private int mId;
	private String title = "";
	private String desc = "";
	private String room = "";
	private String speaker = "";
	private String startTime = "";
	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getSpeaker() {
		return speaker;
	}

	public void setSpeaker(String speaker) {
		this.speaker = speaker;
	}
	
	public String getStartTime() {
		return startTime;
	}
	
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
	public String getShortDescription(){
		String noHTMLString = getDesc().replaceAll("\\<.*?\\>", "");
		return "";
	}
	
	public Boolean getAttending(Context context) {
		SharedPreferences preferences = context.getSharedPreferences("Session", Context.MODE_PRIVATE);
		String attendingSessions = preferences.getString("sessionsAttending", "");
		
		for(String id : attendingSessions.split(",")){
			if(String.valueOf(mId).equals(id)){
				return true;
			}
		}
		
		return false;
	}

	public void setAttending(Context context, Boolean attending) {
		if(getAttending(context) == attending){
			return;
		}
		
		SharedPreferences preferences = context.getSharedPreferences("Session", Context.MODE_PRIVATE);
		String attendingSessions = preferences.getString("sessionsAttending", "");
		
		attendingSessions += "," + String.valueOf(mId);
		
		Editor editor = preferences.edit();
		editor.putString("sessionsAttending", attendingSessions);
		editor.commit();
	}

	
}
