package com.app.markeet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.markeet.adapter.AdapterProduct;
import com.app.markeet.connection.API;
import com.app.markeet.connection.RestAdapter;
import com.app.markeet.connection.callbacks.CallbackProduct;
import com.app.markeet.data.AppConfig;
import com.app.markeet.data.Constant;
import com.app.markeet.model.Category;
import com.app.markeet.model.Product;
import com.app.markeet.utils.NetworkCheck;
import com.app.markeet.utils.Tools;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityCategoryDetails extends AppCompatActivity {
    private static final String EXTRA_OBJECT = "key.EXTRA_OBJECT";

    // activity transition
    public static void navigate(Activity activity, Category obj) {
        Intent i = new Intent(activity, ActivityCategoryDetails.class);
        i.putExtra(EXTRA_OBJECT, obj);
        activity.startActivity(i);
    }

    // extra obj
    private Category category;

    private Toolbar toolbar;
    private ActionBar actionBar;
    private View parent_view;
    private SwipeRefreshLayout swipe_refresh;
    private Call<CallbackProduct> callbackCall = null;

    private RecyclerView recyclerView;
    private AdapterProduct mAdapter;

    private int post_total = 0;
    private int failed_page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_details);
        parent_view = findViewById(android.R.id.content);
        category = (Category) getIntent().getSerializableExtra(EXTRA_OBJECT);
        initComponent();
        initToolbar();

        displayCategoryData(category);

        requestAction(1);
    }

    private void initComponent() {
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, Tools.getGridSpanCount(this)));
        recyclerView.setHasFixedSize(true);

        //set data and list adapter
        mAdapter = new AdapterProduct(this, recyclerView, new ArrayList<Product>());
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterProduct.OnItemClickListener() {
            @Override
            public void onItemClick(View v, Product obj, int position) {
                ActivityProductDetails.navigate(ActivityCategoryDetails.this, obj.id, false);
            }
        });

        // detect when scroll reach bottom
        mAdapter.setOnLoadMoreListener(new AdapterProduct.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int current_page) {
                if (post_total > mAdapter.getItemCount() && current_page != 0) {
                    int next_page = current_page + 1;
                    requestAction(next_page);
                } else {
                    mAdapter.setLoaded();
                }
            }
        });

        // on swipe list
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (callbackCall != null && callbackCall.isExecuted()) callbackCall.cancel();
                mAdapter.resetListData();
                requestAction(1);
            }
        });
    }

    private void displayCategoryData(Category c) {
        ((AppBarLayout) findViewById(R.id.app_bar_layout)).setBackgroundColor(Color.parseColor(c.color));
        ((TextView) findViewById(R.id.name)).setText(c.name);
        ((TextView) findViewById(R.id.brief)).setText(c.brief);
        ImageView icon = (ImageView) findViewById(R.id.icon);
        Tools.displayImageOriginal(this, icon, Constant.getURLimgCategory(c.icon));
        Tools.setSystemBarColorDarker(this, c.color);
        if (AppConfig.TINT_CATEGORY_ICON) {
            icon.setColorFilter(Color.WHITE);
        }

        // analytics track
        ThisApplication.getInstance().saveLogEvent(c.id, c.name, "CATEGORY_DETAILS");
    }


    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_category_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if(item_id == android.R.id.home){
            super.onBackPressed();
        } else if(item_id == R.id.action_search){
            ActivitySearch.navigate(ActivityCategoryDetails.this, category);
        } else if(item_id == R.id.action_cart){
            Intent i = new Intent(this, ActivityShoppingCart.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void displayApiResult(final List<Product> items) {
        mAdapter.insertData(items);
        swipeProgress(false);
        if (items.size() == 0) showNoItemView(true);
    }

    private void requestListProduct(final int page_no) {
        API api = RestAdapter.createAPI();
        callbackCall = api.getListProduct(page_no, Constant.PRODUCT_PER_REQUEST, null, category.id);
        callbackCall.enqueue(new Callback<CallbackProduct>() {
            @Override
            public void onResponse(Call<CallbackProduct> call, Response<CallbackProduct> response) {
                CallbackProduct resp = response.body();
                if (resp != null && resp.status.equals("success")) {
                    post_total = resp.count_total;
                    displayApiResult(resp.products);
                } else {
                    onFailRequest(page_no);
                }
            }

            @Override
            public void onFailure(Call<CallbackProduct> call, Throwable t) {
                if (!call.isCanceled()) onFailRequest(page_no);
            }

        });
    }

    private void onFailRequest(int page_no) {
        failed_page = page_no;
        mAdapter.setLoaded();
        swipeProgress(false);
        if (NetworkCheck.isConnect(this)) {
            showFailedView(true, getString(R.string.failed_text));
        } else {
            showFailedView(true, getString(R.string.no_internet_text));
        }
    }

    private void requestAction(final int page_no) {
        showFailedView(false, "");
        showNoItemView(false);
        if (page_no == 1) {
            swipeProgress(true);
        } else {
            mAdapter.setLoading();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestListProduct(page_no);
            }
        }, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        swipeProgress(false);
        if (callbackCall != null && callbackCall.isExecuted()) {
            callbackCall.cancel();
        }
    }

    private void showFailedView(boolean show, String message) {
        View lyt_failed = (View) findViewById(R.id.lyt_failed);
        ((TextView) findViewById(R.id.failed_message)).setText(message);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_failed.setVisibility(View.GONE);
        }
        ((Button) findViewById(R.id.failed_retry)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAction(failed_page);
            }
        });
    }

    private void showNoItemView(boolean show) {
        View lyt_no_item = (View) findViewById(R.id.lyt_no_item);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_no_item.setVisibility(View.GONE);
        }
    }

    private void swipeProgress(final boolean show) {
        if (!show) {
            swipe_refresh.setRefreshing(show);
            return;
        }
        swipe_refresh.post(new Runnable() {
            @Override
            public void run() {
                swipe_refresh.setRefreshing(show);
            }
        });
    }
}
