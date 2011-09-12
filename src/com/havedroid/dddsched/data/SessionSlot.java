package com.havedroid.dddsched.data;

public class SessionSlot {
	
	private String mSessionSlotDisplayName;
	private Session[] mSessions;
	
	public SessionSlot(String displayName, Session[] sessions){
		mSessionSlotDisplayName = displayName;
		setSessions(sessions);
	}
	
	public String getSessionSlotDisplayName(){
		return mSessionSlotDisplayName;
	}
	
	public void setSessionSlotDisplayName(String sessionSlotDisplayName){
		mSessionSlotDisplayName = sessionSlotDisplayName;
	}

	public Session[] getSessions() {
		return mSessions;
	}

	public void setSessions(Session[] mSessions) {
		this.mSessions = mSessions;
	}
	
	
}
