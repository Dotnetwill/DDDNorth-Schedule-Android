package com.havedroid.dddsched.data;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import com.havedroid.dddsched.Constants;
import com.havedroid.dddsched.R;
import com.havedroid.dddsched.Util.DDDPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

    public static String AttendingSessions = null;
	public static List<SessionSlot> getSchedule(Context context, Boolean forceReparse){
		if(mSchedule == null || forceReparse){
			String schedule = DDDPreferences.getSharedPreferences(context).getString(Constants.SCHEDULE_KEY, "");
            if(schedule == ""){
                schedule = loadFromRes(context);
            }
            loadSchedule(schedule);
		}
		
		return mSchedule;
	}

    private static String loadFromRes(Context context) {
        try{
            Resources res = context.getResources();
            InputStream in_s = res.openRawResource(R.raw.schedule);

            byte[] b = new byte[in_s.available()];

            in_s.read(b);
            return new String(b);
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "Unable to open schedule", e);
        }
        return "";
    }

    private static void loadSchedule(String schedule) {
		JSONArray fullSchedule;
		
		mSchedule = new ArrayList<SessionSlot>();
		
		try {
			fullSchedule = new JSONArray(schedule);
			JSONObject slot;
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
