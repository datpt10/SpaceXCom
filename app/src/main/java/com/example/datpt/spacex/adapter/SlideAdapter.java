package com.example.datpt.spacex.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.datpt.spacex.R;

public class SlideAdapter extends PagerAdapter {
    Context context;

    public SlideAdapter(Context context) {
        this.context = context;
    }

    //Arrays

    public int[] slide_images = {
            R.drawable.one,
            R.drawable.two,
            R.drawable.three
    };

    public String[] slide_heading = {
            "EAT", "PLAYMUSIC", "SLEEP"

    };

    public String[] slide_docs = {
            "Hãy luôn giữ cho mình một thân thể khỏe mạnh",
            "Hãy luôn giữ cho mình một trái tim chứa chan tình yêu thương bằng âm nhạc",
            "Hãy luôn giữ cho mình một giấc ngủ đủ giấc "
    };

    @Override
    public int getCount() {
        return slide_heading.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.slide_layout, container, false);


        ImageView imageView = (ImageView) view.findViewById(R.id.slide);
        TextView textView = (TextView) view.findViewById(R.id.tv_slide);
        TextView textView1 = (TextView) view.findViewById(R.id.tv_doc);

        imageView.setImageResource(slide_images[position]);
        textView.setText(slide_heading[position]);
        textView1.setText(slide_docs[position]);

        container.addView(view);
        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);


    }
}
