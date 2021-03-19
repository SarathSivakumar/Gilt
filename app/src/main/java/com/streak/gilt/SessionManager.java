package com.streak.gilt;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREF_NAME="session";
    String SESSION_KEY="session_id";
    String SESSION_ROLE="session_role";

    public SessionManager(Context context){
        sharedPreferences=context.getSharedPreferences(SHARED_PREF_NAME,context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }
    public void saveSession(User user){
        int id=user.getId();
        editor.putInt(SESSION_KEY,id);
        editor.putString(SESSION_ROLE,user.getRole());
        editor.commit();
    }
    public int getSession(){
        return sharedPreferences.getInt(SESSION_KEY,-1);
    }
    public String getRole(){
        return sharedPreferences.getString(SESSION_ROLE," ");
    }
    public void logout(){
        editor.clear();
        editor.commit();
    }
}
