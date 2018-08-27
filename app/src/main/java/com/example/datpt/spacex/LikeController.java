package com.example.datpt.spacex;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.datpt.spacex.Fragment.LikeFragment;
import com.example.datpt.spacex.Volley.LruBitmapCache;

public class LikeController extends Application {

    public static final String TAG = LikeFragment.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    LruBitmapCache lruBitmapCache;

    private static LikeController mController;


    @Override
    public void onCreate() {
        super.onCreate();
        mController = this;
    }


    public static synchronized LikeController getmController() {
        return mController;
    }

    public RequestQueue getmRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;

    }

    public ImageLoader getmImageLoader() {
        getmRequestQueue();

        if (mImageLoader == null) {
            getLruBitmapCache();

            mImageLoader = new ImageLoader(this.getmRequestQueue(), lruBitmapCache);
        }
        return this.mImageLoader;

    }

    public LruBitmapCache getLruBitmapCache() {

        if (lruBitmapCache == null)
            lruBitmapCache = new LruBitmapCache();

        return this.lruBitmapCache;

    }

    public <T> void addToRequestQueue(Request<T> request, String tag) {
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getmRequestQueue().add(request);

    }

    public <T> void addToRequestQueue(Request<T> request) {
        request.setTag(TAG);
        getmRequestQueue().add(request);

    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }

    }
}
