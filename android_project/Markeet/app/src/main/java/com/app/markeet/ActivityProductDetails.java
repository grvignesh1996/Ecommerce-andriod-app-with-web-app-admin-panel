package com.app.markeet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.markeet.adapter.AdapterProductImage;
import com.app.markeet.connection.API;
import com.app.markeet.connection.RestAdapter;
import com.app.markeet.connection.callbacks.CallbackProductDetails;
import com.app.markeet.data.AppConfig;
import com.app.markeet.data.Constant;
import com.app.markeet.data.DatabaseHandler;
import com.app.markeet.data.SharedPref;
import com.app.markeet.model.Cart;
import com.app.markeet.model.Product;
import com.app.markeet.model.ProductImage;
import com.app.markeet.model.Wishlist;
import com.app.markeet.utils.NetworkCheck;
import com.app.markeet.utils.Tools;
import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityProductDetails extends AppCompatActivity {

    private static final String EXTRA_OBJECT_ID = "key.EXTRA_OBJECT_ID";
    private static final String EXTRA_FROM_NOTIF = "key.EXTRA_FROM_NOTIF";

    // activity transition
    public static void navigate(Activity activity, Long id, Boolean from_notif) {
        Intent i = navigateBase(activity, id, from_notif);
        activity.startActivity(i);
    }

    public static Intent navigateBase(Context context, Long id, Boolean from_notif) {
        Intent i = new Intent(context, ActivityProductDetails.class);
        i.putExtra(EXTRA_OBJECT_ID, id);
        i.putExtra(EXTRA_FROM_NOTIF, from_notif);
        return i;
    }

    private Long product_id;
    private Boolean from_notif;

    // extra obj
    private Product product;

    private MenuItem wishlist_menu;
    private boolean flag_wishlist = false;
    private boolean flag_cart = false;
    private DatabaseHandler db;

    private Call<CallbackProductDetails> callbackCall = null;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private View parent_view;
    private SwipeRefreshLayout swipe_refresh;
    private MaterialRippleLayout lyt_add_cart;
    private TextView tv_add_cart;
    private WebView webview = null;
    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        product_id = getIntent().getLongExtra(EXTRA_OBJECT_ID, -1L);
        from_notif = getIntent().getBooleanExtra(EXTRA_FROM_NOTIF, false);

        db = new DatabaseHandler(this);
        sharedPref = new SharedPref(this);

        initToolbar();
        initComponent();
        requestAction();
        prepareAds();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("");
    }

    private void initComponent() {
        parent_view = findViewById(android.R.id.content);
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        lyt_add_cart = (MaterialRippleLayout) findViewById(R.id.lyt_add_cart);
        tv_add_cart = (TextView) findViewById(R.id.tv_add_cart);
        // on swipe
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestAction();
            }
        });

        lyt_add_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (product == null || (product.name != null && product.name.equals(""))) {
                    Toast.makeText(getApplicationContext(), R.string.please_wait_text, Toast.LENGTH_SHORT).show();
                    return;
                }
                toggleCartButton();
            }
        });

    }

    private void requestAction() {
        showFailedView(false, "");
        swipeProgress(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestNewsInfoDetailsApi();
            }
        }, 1000);
    }

    private void onFailRequest() {
        swipeProgress(false);
        if (NetworkCheck.isConnect(this)) {
            showFailedView(true, getString(R.string.failed_text));
        } else {
            showFailedView(true, getString(R.string.no_internet_text));
        }
    }


    private void prepareAds() {
        if (AppConfig.ADS_PRODUCT_DETAILS && NetworkCheck.isConnect(getApplicationContext())) {
            MobileAds.initialize(getApplicationContext(), getString(R.string.banner_ad_unit_id));
            AdView mAdView = (AdView) findViewById(R.id.ad_view);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        } else {
            ((RelativeLayout) findViewById(R.id.banner_layout)).setVisibility(View.GONE);
        }
    }

    private void requestNewsInfoDetailsApi() {
        API api = RestAdapter.createAPI();
        callbackCall = api.getProductDetails(product_id);
        callbackCall.enqueue(new Callback<CallbackProductDetails>() {
            @Override
            public void onResponse(Call<CallbackProductDetails> call, Response<CallbackProductDetails> response) {
                CallbackProductDetails resp = response.body();
                if (resp != null && resp.status.equals("success")) {
                    product = resp.product;
                    displayPostData();
                    swipeProgress(false);
                } else {
                    onFailRequest();
                }
            }

            @Override
            public void onFailure(Call<CallbackProductDetails> call, Throwable t) {
                Log.e("onFailure", t.getMessage());
                if (!call.isCanceled()) onFailRequest();
            }
        });
    }

    private void displayPostData() {
        ((TextView) findViewById(R.id.title)).setText(Html.fromHtml(product.name));

        webview = (WebView) findViewById(R.id.content);
        String html_data = "<style>img{max-width:100%;height:auto;} iframe{width:100%;}</style> ";
        html_data += product.description;
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.setBackgroundColor(Color.TRANSPARENT);
        webview.setWebChromeClient(new WebChromeClient());
        webview.loadData(html_data, "text/html; charset=UTF-8", null);
        // disable scroll on touch
        webview.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });

        ((TextView) findViewById(R.id.date)).setText(Tools.getFormattedDate(product.last_update));

        TextView price = (TextView) findViewById(R.id.price);
        TextView price_strike = (TextView) findViewById(R.id.price_strike);

        // handle discount view
        if (product.price_discount > 0) {
            price.setText(Tools.getFormattedPrice(product.price_discount, this));
            price_strike.setText(Tools.getFormattedPrice(product.price, this));
            price_strike.setPaintFlags(price_strike.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            price_strike.setVisibility(View.VISIBLE);
        } else {
            price.setText(Tools.getFormattedPrice(product.price, this));
            price_strike.setVisibility(View.GONE);
        }

        if (product.status.equalsIgnoreCase("READY STOCK")) {
            ((TextView) findViewById(R.id.status)).setText(getString(R.string.ready_stock));
        } else if (product.status.equalsIgnoreCase("OUT OF STOCK")) {
            ((TextView) findViewById(R.id.status)).setText(getString(R.string.out_of_stock));
        } else if (product.status.equalsIgnoreCase("SUSPEND")) {
            ((TextView) findViewById(R.id.status)).setText(getString(R.string.suspend));
        } else {
            ((TextView) findViewById(R.id.status)).setText(product.status);
        }

        // display Image slider
        displayImageSlider();

        // display category list at bottom
        displayCategoryProduct();

        Toast.makeText(this, R.string.msg_data_loaded, Toast.LENGTH_SHORT).show();

        // analytics track
        ThisApplication.getInstance().saveLogEvent(product.id, product.name, "PRODUCT_DETAILS");
    }

    private void displayImageSlider() {
        final LinearLayout layout_dots = (LinearLayout) findViewById(R.id.layout_dots);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final AdapterProductImage adapterSlider = new AdapterProductImage(this, new ArrayList<ProductImage>());

        final List<ProductImage> productImages = new ArrayList<>();
        ProductImage p = new ProductImage();
        p.product_id = product.id;
        p.name = product.image;
        productImages.add(p);
        if (product.product_images != null) productImages.addAll(product.product_images);
        adapterSlider.setItems(productImages);
        viewPager.setAdapter(adapterSlider);

        // displaying selected image first
        viewPager.setCurrentItem(0);
        addBottomDots(layout_dots, adapterSlider.getCount(), 0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int pos) {
                addBottomDots(layout_dots, adapterSlider.getCount(), pos);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        final ArrayList<String> images_list = new ArrayList<>();
        for (ProductImage img : productImages) {
            images_list.add(Constant.getURLimgProduct(img.name));
        }

        adapterSlider.setOnItemClickListener(new AdapterProductImage.OnItemClickListener() {
            @Override
            public void onItemClick(View view, ProductImage obj, int pos) {
                Intent i = new Intent(ActivityProductDetails.this, ActivityFullScreenImage.class);
                i.putExtra(ActivityFullScreenImage.EXTRA_POS, pos);
                i.putStringArrayListExtra(ActivityFullScreenImage.EXTRA_IMGS, images_list);
                startActivity(i);
            }
        });
    }

    private void displayCategoryProduct() {
        TextView category = (TextView) findViewById(R.id.category);
        String html_data = "";
        for (int i = 0; i < product.categories.size(); i++) {
            html_data += (i + 1) + ". " + product.categories.get(i).name + "\n";
        }
        category.setText(html_data);
    }

    private void addBottomDots(LinearLayout layout_dots, int size, int current) {
        ImageView[] dots = new ImageView[size];

        layout_dots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            int width_height = 15;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(width_height, width_height));
            params.setMargins(10, 10, 10, 10);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(R.drawable.shape_circle);
            dots[i].setColorFilter(ContextCompat.getColor(this, R.color.darkOverlaySoft));
            layout_dots.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[current].setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryLight));
    }

    private void showFailedView(boolean show, String message) {
        View lyt_failed = (View) findViewById(R.id.lyt_failed);
        View lyt_main_content = (View) findViewById(R.id.lyt_main_content);

        ((TextView) findViewById(R.id.failed_message)).setText(message);
        if (show) {
            lyt_main_content.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            lyt_main_content.setVisibility(View.VISIBLE);
            lyt_failed.setVisibility(View.GONE);
        }
        ((Button) findViewById(R.id.failed_retry)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAction();
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_product_details, menu);
        wishlist_menu = menu.findItem(R.id.action_wish);
        refreshWishlistMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == android.R.id.home) {
            onBackAction();
        } else if (item_id == R.id.action_wish) {
            if (product.name == null || product.name.equals("")) {
                Toast.makeText(this, R.string.cannot_add_wishlist, Toast.LENGTH_SHORT).show();
                return true;
            }
            if (flag_wishlist) {
                db.deleteWishlist(product_id);
                Toast.makeText(this, R.string.remove_wishlist, Toast.LENGTH_SHORT).show();
            } else {
                Wishlist w = new Wishlist(product.id, product.name, product.image, System.currentTimeMillis());
                db.saveWishlist(w);
                Toast.makeText(this, R.string.add_wishlist, Toast.LENGTH_SHORT).show();
            }
            refreshWishlistMenu();
        } else if (item_id == R.id.action_cart) {
            Intent i = new Intent(this, ActivityShoppingCart.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        onBackAction();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webview != null) webview.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webview != null) webview.onPause();
        refreshCartButton();
    }

    private void onBackAction() {
        if (from_notif) {
            if (ActivityMain.active) {
                finish();
            } else {
                Intent intent = new Intent(getApplicationContext(), ActivitySplash.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        } else {
            super.onBackPressed();
        }
    }

    private void refreshWishlistMenu() {
        Wishlist w = db.getWishlist(product_id);
        flag_wishlist = (w != null);
        if (flag_wishlist) {
            wishlist_menu.setIcon(R.drawable.ic_wish);
        } else {
            wishlist_menu.setIcon(R.drawable.ic_wish_outline);
        }
    }

    private void toggleCartButton() {
        if (flag_cart) {
            db.deleteActiveCart(product_id);
            Toast.makeText(this, R.string.remove_cart, Toast.LENGTH_SHORT).show();
        } else {
            // check stock product
            if (product.stock == 0 || product.status.equalsIgnoreCase("OUT OF STOCK")) {
                Toast.makeText(this, R.string.msg_out_of_stock, Toast.LENGTH_SHORT).show();
                return;
            }
            if (product.status.equalsIgnoreCase("SUSPEND")) {
                Toast.makeText(this, R.string.msg_suspend, Toast.LENGTH_SHORT).show();
                return;
            }
            Double selected_price = product.price_discount > 0 ? product.price_discount : product.price;
            Cart cart = new Cart(product.id, product.name, product.image, 1, product.stock, selected_price, System.currentTimeMillis());
            db.saveCart(cart);
            Toast.makeText(this, R.string.add_cart, Toast.LENGTH_SHORT).show();
        }
        refreshCartButton();
    }

    private void refreshCartButton() {
        Cart c = db.getCart(product_id);
        flag_cart = (c != null);
        if (flag_cart) {
            lyt_add_cart.setBackgroundColor(getResources().getColor(R.color.colorRemoveCart));
            tv_add_cart.setText(R.string.bt_remove_cart);
        } else {
            lyt_add_cart.setBackgroundColor(getResources().getColor(R.color.colorAddCart));
            tv_add_cart.setText(R.string.bt_add_cart);
        }
    }
}
