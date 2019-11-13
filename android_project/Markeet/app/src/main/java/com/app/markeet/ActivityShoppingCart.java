package com.app.markeet;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.markeet.adapter.AdapterShoppingCart;
import com.app.markeet.data.DatabaseHandler;
import com.app.markeet.data.SharedPref;
import com.app.markeet.model.Cart;
import com.app.markeet.model.Info;
import com.app.markeet.utils.Tools;

import java.util.List;

public class ActivityShoppingCart extends AppCompatActivity {

    private View parent_view;
    private RecyclerView recyclerView;
    private DatabaseHandler db;
    private AdapterShoppingCart adapter;
    private TextView price_total;
    private SharedPref sharedPref;
    private Info info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
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
        price_total = (TextView) findViewById(R.id.price_total);
    }

    private void initToolbar() {
        ActionBar actionBar;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(R.string.title_activity_cart);
        Tools.systemBarLolipop(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_shopping_cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == android.R.id.home) {
            super.onBackPressed();
        } else if (item_id == R.id.action_checkout) {
            if (adapter.getItemCount() > 0) {
                Intent intent = new Intent(ActivityShoppingCart.this, ActivityCheckout.class);
                startActivity(intent);
            } else {
                Snackbar.make(parent_view, R.string.msg_cart_empty, Snackbar.LENGTH_SHORT).show();
            }
        } else if (item_id == R.id.action_delete) {
            if (adapter.getItemCount() == 0) {
                Snackbar.make(parent_view, R.string.msg_cart_empty, Snackbar.LENGTH_SHORT).show();
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

    @Override
    protected void onResume() {
        super.onResume();
        displayData();
    }

    private void displayData() {
        List<Cart> items = db.getActiveCartList();
        adapter = new AdapterShoppingCart(this, true, items);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);

        adapter.setOnItemClickListener(new AdapterShoppingCart.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Cart obj) {
                dialogCartAction(obj);
            }
        });
        View lyt_no_item = (View) findViewById(R.id.lyt_no_item);
        if (adapter.getItemCount() == 0) {
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            lyt_no_item.setVisibility(View.GONE);
        }
        setTotalPrice();
    }

    private void setTotalPrice() {
        List<Cart> items = adapter.getItem();
        Double _price_total = 0D;
        String _price_total_tax_str;
        for (Cart c : items) {
            _price_total = _price_total + (c.amount * c.price_item);
        }
        _price_total_tax_str = Tools.getFormattedPrice(_price_total, this);
        price_total.setText(" " + _price_total_tax_str);
    }

    private void dialogCartAction(final Cart model) {

        final Dialog dialog = new Dialog(ActivityShoppingCart.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_cart_option);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        ((TextView) dialog.findViewById(R.id.title)).setText(model.product_name);
        ((TextView) dialog.findViewById(R.id.stock)).setText(getString(R.string.stock) + model.stock);
        final TextView qty = (TextView) dialog.findViewById(R.id.quantity);
        qty.setText(model.amount + "");

        ((ImageView) dialog.findViewById(R.id.img_decrease)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model.amount > 1) {
                    model.amount = model.amount - 1;
                    qty.setText(model.amount + "");
                }
            }
        });
        ((ImageView) dialog.findViewById(R.id.img_increase)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model.amount < model.stock) {
                    model.amount = model.amount + 1;
                    qty.setText(model.amount + "");
                }
            }
        });
        ((Button) dialog.findViewById(R.id.bt_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.saveCart(model);
                displayData();
                dialog.dismiss();
            }
        });
        ((Button) dialog.findViewById(R.id.bt_remove)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.deleteActiveCart(model.product_id);
                displayData();
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public void dialogDeleteConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_delete_confirm);
        builder.setMessage(getString(R.string.content_delete_confirm) + getString(R.string.title_activity_cart));
        builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface di, int i) {
                di.dismiss();
                db.deleteActiveCart();
                onResume();
                Snackbar.make(parent_view, R.string.delete_success, Snackbar.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.CANCEL, null);
        builder.show();
    }

}
