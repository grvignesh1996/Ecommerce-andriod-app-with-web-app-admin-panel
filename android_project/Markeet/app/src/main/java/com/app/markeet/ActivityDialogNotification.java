package com.app.markeet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.markeet.data.Constant;
import com.app.markeet.data.DatabaseHandler;
import com.app.markeet.model.Notification;
import com.app.markeet.utils.Tools;

public class ActivityDialogNotification extends AppCompatActivity {

    private static final String EXTRA_OBJECT = "key.EXTRA_OBJECT";
    private static final String EXTRA_FROM_NOTIF = "key.EXTRA_FROM_NOTIF";
    private static final String EXTRA_POSITION = "key.EXTRA_FROM_POSITION";

    // activity transition
    public static void navigate(Activity activity, Notification obj, Boolean from_notif, int position) {
        Intent i = navigateBase(activity, obj, from_notif);
        i.putExtra(EXTRA_POSITION, position);
        activity.startActivity(i);
    }

    public static Intent navigateBase(Context context, Notification obj, Boolean from_notif) {
        Intent i = new Intent(context, ActivityDialogNotification.class);
        i.putExtra(EXTRA_OBJECT, obj);
        i.putExtra(EXTRA_FROM_NOTIF, from_notif);
        return i;
    }

    private Boolean from_notif;
    private Notification notification;
    private Intent intent;
    private DatabaseHandler db;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_notification);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        db = new DatabaseHandler(this);

        notification = (Notification) getIntent().getSerializableExtra(EXTRA_OBJECT);
        from_notif = getIntent().getBooleanExtra(EXTRA_FROM_NOTIF, false);
        position = getIntent().getIntExtra(EXTRA_POSITION, -1);

        // set notification as read
        notification.read = true;
        db.saveNotification(notification);

        initComponent();
    }

    private void initComponent() {
        ((TextView) findViewById(R.id.title)).setText(notification.title);
        ((TextView) findViewById(R.id.content)).setText(notification.content);
        ((TextView) findViewById(R.id.date)).setText(Tools.getFormattedDate(notification.created_at));

        String image_url = null;
        if (notification.type.equals("PRODUCT")) {
            image_url = Constant.getURLimgProduct(notification.image);
        } else if (notification.type.equals("NEWS_INFO")) {
            image_url = Constant.getURLimgNews(notification.image);
        }

        ((RelativeLayout) findViewById(R.id.lyt_image)).setVisibility(View.GONE);
        if (image_url != null) {
            ((RelativeLayout) findViewById(R.id.lyt_image)).setVisibility(View.VISIBLE);
            Tools.displayImageOriginal(this, ((ImageView) findViewById(R.id.image)), image_url);
        } else if (!from_notif) {
            ((Button) findViewById(R.id.bt_open)).setVisibility(View.GONE);
        }

        if (from_notif) {
            ((Button) findViewById(R.id.bt_delete)).setVisibility(View.GONE);
            if (image_url == null && ActivityMain.active) {
                ((LinearLayout) findViewById(R.id.lyt_action)).setVisibility(View.GONE);
            }
        } else {
            ((TextView) findViewById(R.id.dialog_title)).setText(getString(R.string.title_notif_details));
            ((ImageView) findViewById(R.id.logo)).setVisibility(View.GONE);
            ((View) findViewById(R.id.view_space)).setVisibility(View.GONE);
        }

        intent = new Intent(this, ActivitySplash.class);
        if (notification.type.equals("PRODUCT")) {
            intent = ActivityProductDetails.navigateBase(this, notification.obj_id, from_notif);
        } else if (notification.type.equals("NEWS_INFO")) {
            intent = ActivityNewsInfoDetails.navigateBase(this, notification.obj_id, from_notif);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        ((ImageView) findViewById(R.id.img_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ((Button) findViewById(R.id.bt_open)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(intent);
            }
        });

        ((Button) findViewById(R.id.bt_delete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                if (!from_notif && position != -1) {
                    db.deleteNotification(notification.id);
                    ActivityNotification.getInstance().adapter.removeItem(position);
                    Snackbar.make(ActivityNotification.getInstance().parent_view, "Delete successfully", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }
}
