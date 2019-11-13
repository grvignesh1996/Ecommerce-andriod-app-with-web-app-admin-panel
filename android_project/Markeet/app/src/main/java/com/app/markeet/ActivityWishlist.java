package com.app.markeet;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
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

import com.app.markeet.adapter.AdapterWishlist;
import com.app.markeet.data.Constant;
import com.app.markeet.data.DatabaseHandler;
import com.app.markeet.model.Wishlist;
import com.app.markeet.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class ActivityWishlist extends AppCompatActivity {

    private View parent_view;
    private RecyclerView recyclerView;
    private DatabaseHandler db;
    private AdapterWishlist adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        db = new DatabaseHandler(this);

        initToolbar();
        iniComponent();
    }

    private void iniComponent() {
        parent_view = findViewById(android.R.id.content);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //set data and list adapter
        adapter = new AdapterWishlist(this, recyclerView, new ArrayList<Wishlist>());
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);

        adapter.setOnItemClickListener(new AdapterWishlist.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Wishlist obj, int pos) {
                ActivityProductDetails.navigate(ActivityWishlist.this, obj.product_id, false);
            }
        });

        startLoadMoreAdapter();
    }

    private void initToolbar() {
        ActionBar actionBar;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(R.string.title_activity_wishlist);
        Tools.systemBarLolipop(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_wishlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == android.R.id.home) {
            super.onBackPressed();
        } else if (item_id == R.id.action_delete) {
            if (adapter.getItemCount() == 0) {
                Snackbar.make(parent_view, R.string.msg_wishlist_empty, Snackbar.LENGTH_SHORT).show();
                return true;
            }
            dialogDeleteConfirmation();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void startLoadMoreAdapter() {
        adapter.resetListData();
        List<Wishlist> items = db.getWishlistByPage(Constant.NOTIFICATION_PAGE, 0);
        adapter.insertData(items);
        showNoItemView();
        final int item_count = (int) db.getWishlistSize();
        // detect when scroll reach bottom
        adapter.setOnLoadMoreListener(new AdapterWishlist.OnLoadMoreListener() {
            @Override
            public void onLoadMore(final int current_page) {
                if (item_count > adapter.getItemCount() && current_page != 0) {
                    displayDataByPage(current_page);
                } else {
                    adapter.setLoaded();
                }
            }
        });
    }

    private void displayDataByPage(final int next_page) {
        adapter.setLoading();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Wishlist> items = db.getWishlistByPage(Constant.NOTIFICATION_PAGE, (next_page * Constant.NOTIFICATION_PAGE));
                adapter.insertData(items);
                showNoItemView();
            }
        }, 500);
    }

    private void showNoItemView() {
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
        builder.setMessage(getString(R.string.content_delete_confirm) + getString(R.string.title_activity_wishlist));
        builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface di, int i) {
                di.dismiss();
                db.deleteWishlist();
                startLoadMoreAdapter();
                Snackbar.make(parent_view, R.string.delete_success, Snackbar.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.CANCEL, null);
        builder.show();
    }
}
