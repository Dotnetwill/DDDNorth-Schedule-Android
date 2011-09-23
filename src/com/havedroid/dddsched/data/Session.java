package com.havedroid.dddsched.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Html;

public class Session {
	private static final int SHORT_DESC_LENGHT = 100;
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
		return Html.fromHtml(desc).toString();
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
	
	public Date getStartTime() {
		try {
			DateFormat sdf = new SimpleDateFormat("hh:mm:ss");
			return sdf.parse(startTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Date();
		}
	}
	
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
	public String getShortDescription(){
		String noHTMLString = getDesc().replaceAll("\\<.*?\\>", "");
		
		if(noHTMLString.length() > SHORT_DESC_LENGHT){
			return noHTMLString.substring(0, SHORT_DESC_LENGHT) + "...";
		}
		
		return noHTMLString;
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
		
		if(attending){
			attendingSessions += "," + String.valueOf(mId);
		}else{
			attendingSessions = attendingSessions.replace("," + String.valueOf(mId), "");
		}
		Editor editor = preferences.edit();
		editor.putString("sessionsAttending", attendingSessions);
		editor.commit();
	}

	
}
