package com.app.markeet;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.markeet.adapter.AdapterOrderHistory;
import com.app.markeet.adapter.AdapterShoppingCart;
import com.app.markeet.data.DatabaseHandler;
import com.app.markeet.data.SharedPref;
import com.app.markeet.model.Info;
import com.app.markeet.model.Order;
import com.app.markeet.utils.Tools;

import java.util.List;

public class ActivityOrderHistory extends AppCompatActivity {

    private View parent_view;
    private RecyclerView recyclerView;
    private DatabaseHandler db;
    private AdapterOrderHistory adapter;
    private SharedPref sharedPref;
    private Info info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        db = new DatabaseHandler(this);
        sharedPref = new SharedPref(this);
        info = sharedPref.getInfoData();

        initToolbar();
        iniComponent();
    }

    private void iniComponent() {
        parent_view = findViewById(android.R.id.content);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initToolbar() {
        ActionBar actionBar;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(R.string.title_activity_history);
        Tools.systemBarLolipop(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_order_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == android.R.id.home) {
            super.onBackPressed();
        } else if (item_id == R.id.action_delete) {
            if (adapter.getItemCount() == 0) {
                Snackbar.make(parent_view, R.string.msg_history_empty, Snackbar.LENGTH_SHORT).show();
                return true;
            }
            dialogDeleteConfirmation();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayData();
    }

    private void displayData() {
        List<Order> items = db.getOrderList();
        adapter = new AdapterOrderHistory(this, items);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        adapter.setOnItemClickListener(new AdapterOrderHistory.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Order obj) {
                dialogOrderHistoryDetails(obj);
            }
        });
        View lyt_no_item = (View) findViewById(R.id.lyt_no_item);
        if (adapter.getItemCount() == 0) {
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            lyt_no_item.setVisibility(View.GONE);
        }
    }

    public void dialogDeleteConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_delete_confirm);
        builder.setMessage(getString(R.string.content_delete_confirm) + getString(R.string.title_activity_history));
        builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface di, int i) {
                di.dismiss();
                db.deleteOrder();
                onResume();
                Snackbar.make(parent_view, R.string.delete_success, Snackbar.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.CANCEL, null);
        builder.show();
    }

    private void dialogOrderHistoryDetails(final Order order) {
        final Dialog dialog = new Dialog(ActivityOrderHistory.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_order_history_details);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AdapterShoppingCart _adapter = new AdapterShoppingCart(this, false, order.cart_list);
        recyclerView.setAdapter(_adapter);
        recyclerView.setNestedScrollingEnabled(false);
        ((ImageView) dialog.findViewById(R.id.img_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ((TextView) dialog.findViewById(R.id.code)).setText(order.code);
        ((ImageButton) dialog.findViewById(R.id.copy)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tools.copyToClipboard(getApplicationContext(), order.code);
            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

}
