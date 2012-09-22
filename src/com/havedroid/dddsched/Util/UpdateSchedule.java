package com.havedroid.dddsched.Util;

import android.content.SharedPreferences;
import android.util.Log;
import com.havedroid.dddsched.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UpdateSchedule {
    private UpdateSchedule() {
    }

    private static final String VERSION_URL = "https://raw.github.com/Dotnetwill/DDDNorth-Schedule-Android/master/phonein/version";
    private static final String SCHEDULE_URL = "https://raw.github.com/Dotnetwill/DDDNorth-Schedule-Android/master/phonein/schedule";

    private static class UpdateAvailableResult {
        private final Boolean available;
        private final Integer newVersion;

        public UpdateAvailableResult(Boolean available, Integer newVersion) {
            this.available = available;
            this.newVersion = newVersion;
        }

        public Boolean getAvailable() {
            return available;
        }

        public Integer getNewVersion() {
            return newVersion;
        }
    }

    public static Boolean CheckForUpdate(SharedPreferences preferences) {
        UpdateAvailableResult res = updateAvailable(preferences);
        if (res.getAvailable()) {
            String newSchedule = getContentFromUrl(SCHEDULE_URL);

            if (newSchedule != null) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(Constants.SCHEDULE_KEY, newSchedule);
                editor.putInt(Constants.VERSION_PREF_KEY, res.getNewVersion());
                editor.commit();
                return true;
            }
        }
        return false;
    }

    private static UpdateAvailableResult updateAvailable(SharedPreferences preferences) {
        int versionOnServer = getVersionNumberAvailable();
        int curVersion = preferences.getInt(Constants.VERSION_PREF_KEY, 10);
        Log.v(Constants.LOG_TAG, "Current schedule version: " + curVersion);
        Boolean newerVersion = versionOnServer > curVersion;
        return new UpdateAvailableResult(newerVersion, versionOnServer);
    }

    private static int getVersionNumberAvailable() {
        String versionOnlineAsStr = getContentFromUrl(VERSION_URL);
        Log.v(Constants.LOG_TAG, "Version found on server:" + versionOnlineAsStr);
        return convertToInt(versionOnlineAsStr);
    }

    private static int convertToInt(String intAsStr) {
        Integer retVal = -1;

        try {
            retVal = Integer.parseInt(intAsStr);
        } catch (NumberFormatException nfe) {
            //return default
        }

        return retVal;
    }

    private static String getContentFromUrl(String url) {
        HttpURLConnection connection = null;
        try {
            URL requestUrl = new URL(url);
            connection = (HttpURLConnection) requestUrl.openConnection();
            if (connection.getResponseCode() == 200) {
                Log.v(Constants.LOG_TAG, "[" + url + "] returned 200");
                return readResponse(connection);
            } else {
                Log.e(Constants.LOG_TAG, "Server returned error code: " + connection.getResponseCode());
            }
        } catch (MalformedURLException e) {
            Log.e(Constants.LOG_TAG, "URL not well formed, WTF?!");
        } catch (IOException e) {
            //unable to open the connection, maybe we don't have internet or the host is down
            //whatever it is just return null
            Log.e(Constants.LOG_TAG, "Unable to open URL: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    private static String readResponse(HttpURLConnection connection) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder total = new StringBuilder();
        String line;

        while ((line = r.readLine()) != null) {
            total.append(line);
        }

        return total.toString();
    }
}
