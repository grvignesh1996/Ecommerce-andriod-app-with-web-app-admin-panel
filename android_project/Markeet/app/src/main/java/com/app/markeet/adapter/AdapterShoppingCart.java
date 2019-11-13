package com.app.markeet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.markeet.R;
import com.app.markeet.data.Constant;
import com.app.markeet.data.SharedPref;
import com.app.markeet.model.Cart;
import com.app.markeet.utils.Tools;
import com.balysv.materialripple.MaterialRippleLayout;

import java.util.ArrayList;
import java.util.List;


public class AdapterShoppingCart extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context ctx;
    private SharedPref sharedPref;
    private List<Cart> items = new ArrayList<>();
    private Boolean is_cart = true;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Cart obj);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView amount;
        public TextView price;
        public ImageView image;
        public RelativeLayout lyt_image;
        public MaterialRippleLayout lyt_parent;

        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            amount = (TextView) v.findViewById(R.id.amount);
            price = (TextView) v.findViewById(R.id.price);
            image = (ImageView) v.findViewById(R.id.image);
            lyt_parent = (MaterialRippleLayout) v.findViewById(R.id.lyt_parent);
            lyt_image = (RelativeLayout) v.findViewById(R.id.lyt_image);
        }
    }

    public AdapterShoppingCart(Context ctx, boolean is_cart, List<Cart> items) {
        this.ctx = ctx;
        this.items = items;
        this.is_cart = is_cart;
        sharedPref = new SharedPref(ctx);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping_cart, parent, false);
        vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder vItem = (ViewHolder) holder;
            final Cart c = items.get(position);
            vItem.title.setText(c.product_name);
            vItem.price.setText(Tools.getFormattedPrice(c.price_item, ctx));
            vItem.amount.setText(c.amount + " " + ctx.getString(R.string.items));
            Tools.displayImageThumbnail(ctx, vItem.image, Constant.getURLimgProduct(c.image), 0.5f);
            vItem.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, c);
                    }
                }
            });

            if (is_cart) {
                vItem.lyt_image.setVisibility(View.VISIBLE);
                vItem.title.setMaxLines(2);
                vItem.lyt_parent.setEnabled(true);
            } else {
                vItem.lyt_image.setVisibility(View.GONE);
                vItem.title.setMaxLines(1);
                vItem.lyt_parent.setEnabled(false);
            }
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<Cart> getItem() {
        return items;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItems(List<Cart> items) {
        this.items = items;
        notifyDataSetChanged();
    }


}