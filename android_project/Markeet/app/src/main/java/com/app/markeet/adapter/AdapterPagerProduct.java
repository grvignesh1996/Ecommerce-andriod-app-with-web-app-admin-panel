package com.app.markeet.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.app.markeet.R;
import com.app.markeet.model.Product;
import com.app.markeet.utils.Tools;
import com.balysv.materialripple.MaterialRippleLayout;

import java.util.List;

public class AdapterPagerProduct extends PagerAdapter {

    private Activity act;
    private List<Product> products;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Product obj);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    // constructor
    public AdapterPagerProduct(Activity activity, List<Product> products) {
        this.act = activity;
        this.products = products;
    }

    @Override
    public int getCount() {
        return this.products.size();
    }

    public Product getItem(int pos) {
        return products.get(pos);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final Product p = products.get(position);
        LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.item_featured_news, container, false);
        ImageView image = (ImageView) v.findViewById(R.id.image);
        MaterialRippleLayout lyt_parent = (MaterialRippleLayout) v.findViewById(R.id.lyt_parent);

        Tools.displayImageOriginal(act, image, p.image);

        lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, p);
                }
            }
        });

        ((ViewPager) container).addView(v);

        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

}
