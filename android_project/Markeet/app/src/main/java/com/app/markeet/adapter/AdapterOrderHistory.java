package com.app.markeet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.markeet.R;
import com.app.markeet.data.SharedPref;
import com.app.markeet.model.Order;
import com.app.markeet.utils.Tools;
import com.balysv.materialripple.MaterialRippleLayout;

import java.util.ArrayList;
import java.util.List;


public class AdapterOrderHistory extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context ctx;
    private List<Order> items = new ArrayList<>();

    private OnItemClickListener onItemClickListener;
    private SharedPref sharedPref;

    public interface OnItemClickListener {
        void onItemClick(View view, Order obj);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView code;
        public TextView date;
        public TextView price;
        public TextView status;
        public MaterialRippleLayout lyt_parent;

        public ViewHolder(View v) {
            super(v);
            code = (TextView) v.findViewById(R.id.code);
            date = (TextView) v.findViewById(R.id.date);
            price = (TextView) v.findViewById(R.id.price);
            status = (TextView) v.findViewById(R.id.status);
            lyt_parent = (MaterialRippleLayout) v.findViewById(R.id.lyt_parent);
        }
    }

    public AdapterOrderHistory(Context ctx, List<Order> items) {
        this.ctx = ctx;
        this.items = items;
        sharedPref = new SharedPref(ctx);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
        vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder vItem = (ViewHolder) holder;
            final Order c = items.get(position);
            vItem.code.setText(c.code);
            vItem.price.setText(c.total_fees);
            vItem.status.setText(c.status);
            vItem.date.setText(Tools.getFormattedDateSimple(c.created_at));
            vItem.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, c);
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<Order> getItem() {
        return items;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItems(List<Order> items) {
        this.items = items;
        notifyDataSetChanged();
    }


}