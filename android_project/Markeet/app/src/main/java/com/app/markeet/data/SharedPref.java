package com.app.markeet.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.app.markeet.BuildConfig;
import com.app.markeet.R;
import com.app.markeet.model.BuyerProfile;
import com.app.markeet.model.Info;
import com.google.gson.Gson;

public class SharedPref {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences prefs;
    public static final int MAX_OPEN_COUNTER = BuildConfig.DEBUG ? 1 : Constant.OPEN_COUNTER;

    private static final String FCM_PREF_KEY = "_.FCM_PREF_KEY";
    private static final String FIRST_LAUNCH = "_.FIRST_LAUNCH";
    private static final String INFO_DATA = "_.INFO_DATA_KEY";
    private static final String BUYER_PROFILE = "_.BUYER_PROFILE_KEY";

    public SharedPref(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("MAIN_PREF", Context.MODE_PRIVATE);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Preference for first launch
     */
    public void setFirstLaunch(boolean flag) {
        sharedPreferences.edit().putBoolean(FIRST_LAUNCH, flag).apply();
    }

    public boolean isFirstLaunch() {
        return sharedPreferences.getBoolean(FIRST_LAUNCH, true);
    }

    /**
     * Preference for Fcm register
     */
    public void setFcmRegId(String fcmRegId) {
        sharedPreferences.edit().putString(FCM_PREF_KEY, fcmRegId).apply();
    }

    public String getFcmRegId() {
        return sharedPreferences.getString(FCM_PREF_KEY, null);
    }

    public boolean isFcmRegIdEmpty() {
        return TextUtils.isEmpty(getFcmRegId());
    }

    /**
     * For notifications flag
     */
    public boolean getNotification() {
        return prefs.getBoolean(context.getString(R.string.pref_title_notif), true);
    }

    public String getRingtone() {
        return prefs.getString(context.getString(R.string.pref_title_ringtone), "content://settings/system/notification_sound");
    }

    public boolean getVibration() {
        return prefs.getBoolean(context.getString(R.string.pref_title_vibrate), true);
    }


    /**
     * To save dialog permission state
     */
    public void setNeverAskAgain(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean getNeverAskAgain(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    // when app open N-times it will update fcm RegID at server
    public boolean isOpenAppCounterReach() {
        int counter = sharedPreferences.getInt("OPEN_COUNTER_KEY", MAX_OPEN_COUNTER) + 1;
        setOpenAppCounter(counter);
        Log.e("COUNTER", "" + counter);
        return (counter >= MAX_OPEN_COUNTER);
    }

    public void setOpenAppCounter(int val) {
        sharedPreferences.edit().putInt("OPEN_COUNTER_KEY", val).apply();
    }

    // info API loaded
    public Info setInfoData(Info info) {
        if (info == null) return null;
        String json = new Gson().toJson(info, Info.class);
        sharedPreferences.edit().putString(INFO_DATA, json).apply();
        return getInfoData();
    }

    public void clearInfoData() {
        sharedPreferences.edit().putString(INFO_DATA, null).apply();
    }

    public Info getInfoData() {
        String data = sharedPreferences.getString(INFO_DATA, null);
        if (data == null) return null;
        return new Gson().fromJson(data, Info.class);
    }

    public boolean isInfoLoaded() {
        Info info = getInfoData();
        return (info != null);
    }

    // info buyer profile data
    public BuyerProfile setBuyerProfile(BuyerProfile buyerProfile) {
        if (buyerProfile == null) return null;
        String json = new Gson().toJson(buyerProfile, BuyerProfile.class);
        sharedPreferences.edit().putString(BUYER_PROFILE, json).apply();
        return getBuyerProfile();
    }

    public void clearBuyerProfile() {
        sharedPreferences.edit().putString(BUYER_PROFILE, null).apply();
    }

    public BuyerProfile getBuyerProfile() {
        String data = sharedPreferences.getString(BUYER_PROFILE, null);
        if (data == null) return null;
        return new Gson().fromJson(data, BuyerProfile.class);
    }
}
