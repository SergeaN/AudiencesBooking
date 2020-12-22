package com.etu.audiencesbooking;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    public static final String SESSION_USERSESSION = "userLoginSession";
    public static final String SESSION_REMEMBERME = "rememberMe";

    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_ID = "id";

    private static final String IS_REMEMBERME = "IsRememberMe";
    public static final String KEY_SESSIONEMAIL = "email";
    public static final String KEY_SESSIONPASSWORD = "password";

    private SharedPreferences mUsersSession;
    private SharedPreferences.Editor mEditor;
    private Context mContext;

    public SessionManager(Context context, String sessionName) {
        mContext = context;
        mUsersSession = mContext
                .getSharedPreferences(sessionName, Context.MODE_PRIVATE);
        mEditor = mUsersSession.edit();
    }

    public void logoutUserFromSession() {
        mEditor.clear();
        mEditor.commit();
    }

    /*
    Login
    Session Functions
    */

    public void createLoginSession(String id) {
        mEditor.putBoolean(IS_LOGIN, true);
        mEditor.putString(KEY_ID, id);
        mEditor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> userData = new HashMap<>();
        userData.put(KEY_ID, mUsersSession.getString(KEY_ID, null));
        return userData;
    }

    public boolean checkLogin() {
        return mUsersSession.getBoolean(IS_LOGIN, false);
    }

    /*
    Remember Me
    Session Functions
    */

    public void createRememberMeSession(String email, String password) {
        mEditor.putBoolean(IS_REMEMBERME, true);

        mEditor.putString(KEY_SESSIONEMAIL, email);
        mEditor.putString(KEY_SESSIONPASSWORD, password);

        mEditor.commit();
    }

    public HashMap<String, String> getRememberMeDetails() {
        HashMap<String, String> userData = new HashMap<>();

        userData.put(KEY_SESSIONEMAIL, mUsersSession.getString(KEY_SESSIONEMAIL, null));
        userData.put(KEY_SESSIONPASSWORD, mUsersSession.getString(KEY_SESSIONPASSWORD, null));

        return userData;
    }

    public boolean checkRememberMe() {
        return mUsersSession.getBoolean(IS_REMEMBERME, false);
    }
}
