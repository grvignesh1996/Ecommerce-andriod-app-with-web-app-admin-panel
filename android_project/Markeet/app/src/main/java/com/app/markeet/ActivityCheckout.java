package com.app.markeet;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.markeet.adapter.AdapterShoppingCart;
import com.app.markeet.connection.API;
import com.app.markeet.connection.RestAdapter;
import com.app.markeet.connection.callbacks.CallbackOrder;
import com.app.markeet.data.DatabaseHandler;
import com.app.markeet.data.SharedPref;
import com.app.markeet.model.BuyerProfile;
import com.app.markeet.model.Cart;
import com.app.markeet.model.Checkout;
import com.app.markeet.model.Info;
import com.app.markeet.model.Order;
import com.app.markeet.model.ProductOrder;
import com.app.markeet.model.ProductOrderDetail;
import com.app.markeet.utils.CallbackDialog;
import com.app.markeet.utils.DialogUtils;
import com.app.markeet.utils.Tools;
import com.balysv.materialripple.MaterialRippleLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityCheckout extends AppCompatActivity {

    private View parent_view;
    private Spinner shipping;
    private ImageButton bt_date_shipping;
    private TextView date_shipping;
    private RecyclerView recyclerView;
    private MaterialRippleLayout lyt_add_cart;
    private TextView total_order, tax, price_tax, total_fees;
    private TextInputLayout buyer_name_lyt, email_lyt, phone_lyt, address_lyt, comment_lyt;
    private EditText buyer_name, email, phone, address, comment;

    private DatePickerDialog datePickerDialog;
    private AdapterShoppingCart adapter;
    private DatabaseHandler db;
    private SharedPref sharedPref;
    private Info info;
    private BuyerProfile buyerProfile;
    private Long date_ship_millis = 0L;
    private Double _total_fees = 0D;
    private String _total_fees_str;

    private Call<CallbackOrder> callbackCall = null;
    // construct dialog progress
    ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        db = new DatabaseHandler(this);
        sharedPref = new SharedPref(this);
        info = sharedPref.getInfoData();
        buyerProfile = sharedPref.getBuyerProfile();

        initToolbar();
        iniComponent();
    }

    private void initToolbar() {
        ActionBar actionBar;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(R.string.title_activity_checkout);
        Tools.systemBarLolipop(this);
    }

    private void iniComponent() {
        parent_view = findViewById(android.R.id.content);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        lyt_add_cart = (MaterialRippleLayout) findViewById(R.id.lyt_add_cart);

        // cost view
        total_order = (TextView) findViewById(R.id.total_order);
        tax = (TextView) findViewById(R.id.tax);
        price_tax = (TextView) findViewById(R.id.price_tax);
        total_fees = (TextView) findViewById(R.id.total_fees);

        // form view
        buyer_name = (EditText) findViewById(R.id.buyer_name);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone);
        address = (EditText) findViewById(R.id.address);
        comment = (EditText) findViewById(R.id.comment);

        buyer_name.addTextChangedListener(new CheckoutTextWatcher(buyer_name));
        email.addTextChangedListener(new CheckoutTextWatcher(email));
        phone.addTextChangedListener(new CheckoutTextWatcher(phone));
        address.addTextChangedListener(new CheckoutTextWatcher(address));
        comment.addTextChangedListener(new CheckoutTextWatcher(comment));

        buyer_name_lyt = (TextInputLayout) findViewById(R.id.buyer_name_lyt);
        email_lyt = (TextInputLayout) findViewById(R.id.email_lyt);
        phone_lyt = (TextInputLayout) findViewById(R.id.phone_lyt);
        address_lyt = (TextInputLayout) findViewById(R.id.address_lyt);
        comment_lyt = (TextInputLayout) findViewById(R.id.comment_lyt);
        shipping = (Spinner) findViewById(R.id.shipping);
        bt_date_shipping = (ImageButton) findViewById(R.id.bt_date_shipping);
        date_shipping = (TextView) findViewById(R.id.date_shipping);
        List<String> shipping_list = new ArrayList<>();
        shipping_list.add(getString(R.string.choose_shipping));
        shipping_list.addAll(info.shipping);

        // Initialize and set Adapter
        ArrayAdapter adapter_shipping = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, shipping_list.toArray());
        adapter_shipping.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shipping.setAdapter(adapter_shipping);

        progressDialog = new ProgressDialog(ActivityCheckout.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(R.string.title_please_wait);
        progressDialog.setMessage(getString(R.string.content_submit_checkout));

        bt_date_shipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDatePicker();
            }
        });

        lyt_add_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == android.R.id.home) {
            super.onBackPressed();
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
        adapter = new AdapterShoppingCart(this, false, items);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        setTotalPrice();
        if (buyerProfile != null) {
            buyer_name.setText(buyerProfile.name);
            email.setText(buyerProfile.email);
            phone.setText(buyerProfile.phone);
            address.setText(buyerProfile.address);
        }
    }

    private void setTotalPrice() {
        List<Cart> items = adapter.getItem();
        Double _total_order = 0D, _price_tax = 0D;
        String _total_order_str, _price_tax_str;
        for (Cart c : items) {
            _total_order = _total_order + (c.amount * c.price_item);
        }
        _price_tax = _total_order * info.tax / 100;
        _total_fees = _total_order + _price_tax;
        _price_tax_str = Tools.getFormattedPrice(_price_tax, this);
        _total_order_str = Tools.getFormattedPrice(_total_order, this);
        _total_fees_str = Tools.getFormattedPrice(_total_fees, this);

        // set to display
        total_order.setText(_total_order_str);
        tax.setText(getString(R.string.tax) + info.tax + "%");
        price_tax.setText(_price_tax_str);
        total_fees.setText(_total_fees_str);
    }


    private void submitForm() {
        if (!validateName()) {
            Snackbar.make(parent_view, R.string.invalid_name, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!validateEmail()) {
            Snackbar.make(parent_view, R.string.invalid_email, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!validatePhone()) {
            Snackbar.make(parent_view, R.string.invalid_phone, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!validateAddress()) {
            Snackbar.make(parent_view, R.string.invalid_address, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!validateShipping()) {
            Snackbar.make(parent_view, R.string.invalid_shipping, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!validateDateShip()) {
            Snackbar.make(parent_view, R.string.invalid_date_ship, Snackbar.LENGTH_SHORT).show();
            return;
        }

        buyerProfile = new BuyerProfile();
        buyerProfile.name = buyer_name.getText().toString();
        buyerProfile.email = email.getText().toString();
        buyerProfile.phone = phone.getText().toString();
        buyerProfile.address = address.getText().toString();
        sharedPref.setBuyerProfile(buyerProfile);

        // hide keyboard
        hideKeyboard();

        // show dialog confirmation
        dialogConfirmCheckout();
    }

    private void submitOrderData() {
        // prepare checkout data
        Checkout checkout = new Checkout();
        ProductOrder productOrder = new ProductOrder(buyerProfile, shipping.getSelectedItem().toString(), date_ship_millis, comment.getText().toString().trim());
        productOrder.status = "WAITING";
        productOrder.total_fees = _total_fees;
        productOrder.tax = info.tax;
        // to support notification
        productOrder.serial = Tools.getDeviceID(this);

        checkout.product_order = productOrder;
        checkout.product_order_detail = new ArrayList<>();
        for (Cart c : adapter.getItem()) {
            ProductOrderDetail pod = new ProductOrderDetail(c.product_id, c.product_name, c.amount, c.price_item);
            checkout.product_order_detail.add(pod);
        }

        // submit data to server
        API api = RestAdapter.createAPI();
        callbackCall = api.submitProductOrder(checkout);
        callbackCall.enqueue(new Callback<CallbackOrder>() {
            @Override
            public void onResponse(Call<CallbackOrder> call, Response<CallbackOrder> response) {
                CallbackOrder resp = response.body();
                if (resp != null && resp.status.equals("success")) {
                    Order order = new Order(resp.data.id, resp.data.code, _total_fees_str);
                    for (Cart c : adapter.getItem()) {
                        c.order_id = order.id;
                        order.cart_list.add(c);
                    }
                    db.saveOrder(order);
                    dialogSuccess(order.code);
                } else {
                    dialogFailedRetry();
                }

            }

            @Override
            public void onFailure(Call<CallbackOrder> call, Throwable t) {
                Log.e("onFailure", t.getMessage());
                if (!call.isCanceled()) dialogFailedRetry();
            }
        });
    }

    // give delay when submit data to give good UX
    private void delaySubmitOrderData() {
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                submitOrderData();
            }
        }, 2000);
    }

    public void dialogConfirmCheckout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirmation);
        builder.setMessage(getString(R.string.confirm_checkout));
        builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                delaySubmitOrderData();
            }
        });
        builder.setNegativeButton(R.string.NO, null);
        builder.show();
    }

    public void dialogFailedRetry() {
        progressDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.failed);
        builder.setMessage(getString(R.string.failed_checkout));
        builder.setPositiveButton(R.string.TRY_AGAIN, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                delaySubmitOrderData();
            }
        });
        builder.setNegativeButton(R.string.SETTING, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(getApplicationContext(), ActivitySettings.class));
            }
        });
        builder.show();
    }

    public void dialogSuccess(String code) {
        progressDialog.dismiss();
        Dialog dialog = new DialogUtils(this).buildDialogInfo(
                getString(R.string.success_checkout),
                String.format(getString(R.string.msg_success_checkout), code),
                getString(R.string.OK),
                R.drawable.img_checkout_success,
                new CallbackDialog() {
                    @Override
                    public void onPositiveClick(Dialog dialog) {
                        finish();
                        dialog.dismiss();
                    }

                    @Override
                    public void onNegativeClick(Dialog dialog) {
                    }
                });
        dialog.show();
    }

    private void dialogDatePicker() {
        Calendar cur_calender = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, R.style.DatePickerTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int _year, int _month, int _day) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, _year);
                calendar.set(Calendar.MONTH, _month);
                calendar.set(Calendar.DAY_OF_MONTH, _day);
                date_ship_millis = calendar.getTimeInMillis();
                date_shipping.setText(Tools.getFormattedDateSimple(date_ship_millis));
                datePickerDialog.dismiss();
            }
        }, cur_calender.get(Calendar.YEAR), cur_calender.get(Calendar.MONTH), cur_calender.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.setCancelable(true);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    // validation method
    private boolean validateEmail() {
        String str = email.getText().toString().trim();
        if (str.isEmpty() || !Tools.isValidEmail(str)) {
            email_lyt.setError(getString(R.string.invalid_email));
            requestFocus(email);
            return false;
        } else {
            email_lyt.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateName() {
        String str = buyer_name.getText().toString().trim();
        if (str.isEmpty()) {
            buyer_name_lyt.setError(getString(R.string.invalid_name));
            requestFocus(buyer_name);
            return false;
        } else {
            buyer_name_lyt.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePhone() {
        String str = phone.getText().toString().trim();
        if (str.isEmpty()) {
            phone_lyt.setError(getString(R.string.invalid_phone));
            requestFocus(phone);
            return false;
        } else {
            phone_lyt.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateAddress() {
        String str = address.getText().toString().trim();
        if (str.isEmpty()) {
            address_lyt.setError(getString(R.string.invalid_address));
            requestFocus(address);
            return false;
        } else {
            address_lyt.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateShipping() {
        int pos = shipping.getSelectedItemPosition();
        if (pos == 0) {
            return false;
        }
        return true;
    }

    private boolean validateDateShip() {
        if (date_ship_millis == 0L) {
            return false;
        }
        return true;
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private class CheckoutTextWatcher implements TextWatcher {
        private View view;

        private CheckoutTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.email:
                    validateEmail();
                    break;
                case R.id.name:
                    validateName();
                    break;
                case R.id.phone:
                    validatePhone();
                    break;
                case R.id.address:
                    validateAddress();
                    break;
            }
        }
    }
}
