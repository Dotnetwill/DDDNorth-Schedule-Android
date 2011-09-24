package com.havedroid.dddsched.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.havedroid.dddsched.Constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;

public class Session implements Parcelable {
	private static final int SHORT_DESC_LENGHT = 100;
	private int mId;
	private String mTitle = "";
	private String mDesc = "";
	private String mRoom = "";
	private String mSpeaker = "";
	private String mStartTime = "";
	
	public Session() {
	}
	
	private Session(Parcel in) {
   	 mId = in.readInt();
   	 mDesc = in.readString();
   	 mRoom = in.readString();
   	 mSpeaker = in.readString();
   	 mStartTime = in.readString();
   	 mTitle = in.readString();
	}
	
	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		this.mTitle = title;
	}

	public String getDesc() {
		return Html.fromHtml(mDesc).toString();
	}

	public void setDesc(String desc) {
		this.mDesc = desc;
	}

	public String getRoom() {
		return mRoom;
	}

	public void setRoom(String room) {
		this.mRoom = room;
	}

	public String getSpeaker() {
		return mSpeaker;
	}

	public void setSpeaker(String speaker) {
		this.mSpeaker = speaker;
	}
	
	public Date getStartTime() {
		try {
			DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
			return sdf.parse(mStartTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Date();
		}
	}
	
	public void setStartTime(String startTime) {
		this.mStartTime = startTime;
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
		Log.d(Constants.LOG_TAG, "Attending sessions string: " + attendingSessions);
		
		String idString =String.valueOf(mId);
		Log.d(Constants.LOG_TAG, "session id string: " + idString);
		
		for(String id : attendingSessions.split(",")){
			if(idString.equals(id)){
				
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

	 public void writeToParcel(Parcel out, int flags) {
         out.writeInt(mId);
         out.writeString(mDesc);
         out.writeString(mRoom);
         out.writeString(mSpeaker);
         out.writeString(mStartTime);
         out.writeString(mTitle);
     }

     public static final Parcelable.Creator<Session> CREATOR
             = new Parcelable.Creator<Session>() {
         public Session createFromParcel(Parcel in) {
             return new Session(in);
         }

         public Session[] newArray(int size) {
             return new Session[size];
         }
     };
     


	public int describeContents() {
		
		return 0;
	}
}
