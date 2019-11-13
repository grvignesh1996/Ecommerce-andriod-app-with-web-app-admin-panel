package com.app.markeet.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.app.markeet.ActivityDialogNotification;
import com.app.markeet.R;
import com.app.markeet.data.Constant;
import com.app.markeet.data.DatabaseHandler;
import com.app.markeet.data.SharedPref;
import com.app.markeet.model.Notification;
import com.app.markeet.utils.CallbackImageNotif;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

public class FcmMessagingService extends FirebaseMessagingService {

    private static int VIBRATION_TIME = 500; // in millisecond
    private SharedPref sharedPref;
    private DatabaseHandler db;
    private int retry_count = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sharedPref = new SharedPref(this);
        db = new DatabaseHandler(this);
        retry_count = 0;
        if (sharedPref.getNotification()) {
            if (remoteMessage.getData().size() <= 0) return;
            Object obj = remoteMessage.getData();
            String data = new Gson().toJson(obj);

            Notification notification = new Gson().fromJson(data, Notification.class);
            notification.id = System.currentTimeMillis();
            notification.created_at = System.currentTimeMillis();
            notification.read = false;

            // display notification
            prepareImageNotification(notification);

            // save notification to relam db
            saveNotification(notification);
        }
    }

    private void prepareImageNotification(final Notification notif) {
        String image_url = null;
        if (notif.type.equals("PRODUCT")) {
            image_url = Constant.getURLimgProduct(notif.image);
        } else if (notif.type.equals("NEWS_INFO")) {
            image_url = Constant.getURLimgNews(notif.image);
        } else if(notif.type.equals("PROCESS_ORDER")){
            // update order status
            db.updateStatusOrder(notif.code, notif.status);
        }
        if (image_url != null) {
            glideLoadImageFromUrl(this, image_url, new CallbackImageNotif() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    showNotification(notif, bitmap);
                }

                @Override
                public void onFailed(String string) {
                    Log.e("onFailed", string);
                    if (retry_count <= Constant.LOAD_IMAGE_NOTIF_RETRY) {
                        retry_count++;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                prepareImageNotification(notif);
                            }
                        }, 1000);
                    } else {
                        showNotification(notif, null);
                    }
                }
            });
        } else {
            showNotification(notif, null);
        }
    }

    private void showNotification(Notification notif, Bitmap bitmap) {
        Intent intent = ActivityDialogNotification.navigateBase(this, notif, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(notif.title);
        builder.setContentText(notif.content);
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setDefaults(android.app.Notification.DEFAULT_LIGHTS);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setPriority(android.app.Notification.PRIORITY_HIGH);
        }
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(notif.content));
        if (bitmap != null) {
            builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).setSummaryText(notif.content));
        }

        // display push notif
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int unique_id = (int) System.currentTimeMillis();
        notificationManager.notify(unique_id, builder.build());

        vibrationAndPlaySound();
    }

    private void vibrationAndPlaySound() {
        // play vibration
        if (sharedPref.getVibration()) {
            ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(VIBRATION_TIME);
        }
        // play tone
        RingtoneManager.getRingtone(this, Uri.parse(sharedPref.getRingtone())).play();
    }


    // load image with callback
    Handler mainHandler = new Handler(Looper.getMainLooper());
    Runnable myRunnable;
    private void glideLoadImageFromUrl(final Context ctx, final String url, final CallbackImageNotif callback) {

        myRunnable = new Runnable() {
            @Override
            public void run() {
                Glide.with(ctx).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        callback.onSuccess(resource);
                        mainHandler.removeCallbacks(myRunnable);
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        callback.onFailed(e.getMessage());
                        super.onLoadFailed(e, errorDrawable);
                        mainHandler.removeCallbacks(myRunnable);
                    }
                });
            }
        };
        mainHandler.post(myRunnable);
    }

    private void saveNotification(Notification notification) {
        db.saveNotification(notification);
    }

}
