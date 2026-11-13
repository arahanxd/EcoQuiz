package com.example.ecoquiz.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SessionManager handles saving, retrieving, and clearing user login session
 * using Android SharedPreferences.
 */
public class SessionManager {
    private static final String PREF_NAME = "EcoQuizUserSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Creates a login session with user details.
     */
    public void createLoginSession(int userId, String username, String name, String email) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    /**
     * Checks if a user is currently logged in.
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Retrieves logged-in user's database ID.
     */
    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    /**
     * Retrieves logged-in user's username.
     */
    public String getUsername() {
        return pref.getString(KEY_USERNAME, "");
    }

    /**
     * Retrieves logged-in user's full name.
     */
    public String getName() {
        return pref.getString(KEY_NAME, "Eco Hero");
    }

    /**
     * Retrieves logged-in user's email.
     */
    public String getEmail() {
        return pref.getString(KEY_EMAIL, "");
    }

    /**
     * Clears all session data and logs the user out.
     */
    public void logoutUser() {
        editor.clear();
        editor.apply();
    }
}
