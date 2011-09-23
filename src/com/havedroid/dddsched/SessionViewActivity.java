package com.havedroid.dddsched;

import com.havedroid.dddsched.data.Session;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class SessionViewActivity extends Activity {
	public static final String SESSION_EXTRA_KEY = "session";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.sessiondetailview);
        Session session = (Session)getIntent().getExtras().get(SESSION_EXTRA_KEY);
        
        ((TextView)findViewById(R.id.FullDetail_SessionTitle)).setText(session.getTitle());
        ((TextView)findViewById(R.id.FullDetail_SessionDescription)).setText(Html.fromHtml(session.getDesc()));
        ((TextView)findViewById(R.id.FullDetail_SessionPres)).setText(session.getSpeaker());
        ((TextView)findViewById(R.id.FullDetail_SessionRoom)).setText(session.getRoom());
        ((TextView)findViewById(R.id.FullDetail_SessionTime)).setText(session.getStartTime().toString());
	}
}
