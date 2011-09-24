package com.havedroid.dddsched.data;

import android.util.Log;

import com.havedroid.dddsched.Constants;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Schedule {
	//Session slot json names
	private static final String JSON_SESSION_SLOT_NAME = "slot_display_name";
	private static final String JSON_SESSION_ARRAY_NAME = "sessions";
	
	//Session json names
	private static final String JSON_SESSION_TITLE_NAME = "title";
	private static final String JSON_SESSION_SPEAKER_NAME = "speaker";
	private static final String JSON_SESSION_ID_NAME = "id";
	private static final String JSON_SESSION_DESCRIPTION_NAME = "description";
	private static final String JSON_SESSION_START_TIME_NAME = "start_time";
	private static final String JSON_SESSION_ROOM_NAME = "room";
	
	private static List<SessionSlot> mSchedule = null;
	
	public static List<SessionSlot> getSchedule(){
		if(mSchedule == null){
			loadSchedule();
		}
		
		return mSchedule;
	}

	private static void loadSchedule() {
		String defaultSchedule = Constants.SCHEDULE;
		JSONArray fullSchedule;
		
		mSchedule = new ArrayList<SessionSlot>();
		
		try {
			fullSchedule = new JSONArray(defaultSchedule);
			JSONObject slot = null;
			for(int i = 0; i < fullSchedule.length(); i++){
				slot = fullSchedule.getJSONObject(i);
				mSchedule.add(createSessionSlot(slot));
			}
		} catch (JSONException e) {
			Log.e(Constants.LOG_TAG, "Exception caught:" + e.getMessage());
			
			e.printStackTrace();
		}
	}

	private static SessionSlot createSessionSlot(JSONObject jsonSlot) throws JSONException {
		String slotDisplayText = jsonSlot.getString(JSON_SESSION_SLOT_NAME);
		Session[] sessions = getSessions(jsonSlot.getJSONArray(JSON_SESSION_ARRAY_NAME));
		
		return new SessionSlot(slotDisplayText, sessions);
	}

	private static Session[] getSessions(JSONArray jsonArray) throws JSONException {
		Session[] sessions = new Session[jsonArray.length()];
		
		for(int i = 0; i < jsonArray.length(); i++){
			JSONObject jsonSession = jsonArray.getJSONObject(i);
			
			sessions[i] = new Session();
			sessions[i].setId(jsonSession.getInt(JSON_SESSION_ID_NAME));
			sessions[i].setTitle(jsonSession.getString(JSON_SESSION_TITLE_NAME));
			sessions[i].setSpeaker(jsonSession.getString(JSON_SESSION_SPEAKER_NAME));
			sessions[i].setRoom(jsonSession.getString(JSON_SESSION_ROOM_NAME));
			sessions[i].setStartTime(jsonSession.getString(JSON_SESSION_START_TIME_NAME));
			sessions[i].setDesc(jsonSession.getString(JSON_SESSION_DESCRIPTION_NAME));
		}
		
		return sessions;
	}
}
