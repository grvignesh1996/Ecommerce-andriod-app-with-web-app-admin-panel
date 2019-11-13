package com.app.markeet.fcm;

import com.app.markeet.data.SharedPref;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FcmInstanceIDService extends FirebaseInstanceIdService {

    private SharedPref sharedPref;

    @Override
    public void onTokenRefresh() {
        sharedPref = new SharedPref(this);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        sharedPref.setFcmRegId(token);
        sharedPref.setOpenAppCounter(SharedPref.MAX_OPEN_COUNTER);
    }
}
