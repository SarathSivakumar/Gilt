package com.streak.gilt;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREF_NAME="session";
    String SESSION_KEY="session_id";

    public SessionManager(Context context){
        sharedPreferences=context.getSharedPreferences(SHARED_PREF_NAME,context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }
    public void saveSession(User user){
        int id=user.getId();
        editor.putInt(SESSION_KEY,id);
        editor.commit();
    }
    public int getSession(){
        return sharedPreferences.getInt(SESSION_KEY,-1);
    }
}
