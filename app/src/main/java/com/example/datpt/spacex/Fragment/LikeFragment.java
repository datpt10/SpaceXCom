package com.example.datpt.spacex.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.datpt.spacex.LikeController;
import com.example.datpt.spacex.R;
import com.example.datpt.spacex.adapter.LikeAdapter;
import com.example.datpt.spacex.item.Like;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class LikeFragment extends Fragment {

    private static final String TAG = LikeFragment.class.getSimpleName();
    private RecyclerView recyclerView;
    private ArrayList<Like> arrayList;
    private LikeAdapter adapter;
    String URL_NEW = "https://firebasestorage.googleapis.com/v0/b/halo-b3655.appspot.com/o/new.json?alt=media&token=8944df59-8803-4bed-8882-b7d92e19f83b";

    private ShimmerFrameLayout mShimmerViewContainer;

    public LikeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_like, container, false);

        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);

        recyclerView =  view.findViewById(R.id.lv_like);
        arrayList = new ArrayList<>();

        adapter = new LikeAdapter(getActivity(), arrayList);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        // We first check for cached request

        Cache cache = LikeController.getmController().getmRequestQueue().getCache();
        Entry entry = cache.get(URL_NEW);

        if (entry != null) {
            // fetch the data from cache
            try {
                String data = new String(entry.data, "UTF-8");
                try {
                    parseJsonFeed(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            // making fresh volley request and getting json
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_NEW, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    if (response != null) {
                        parseJsonFeed(response);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });

            // Adding request to volley request queue
            LikeController.getmController().addToRequestQueue(jsonObjectRequest);
        }
        return view;

    }

    /**
     * Parsing json reponse and passing the data to feed view list adapter
     */

    private void parseJsonFeed(JSONObject response) {
        try {
            JSONArray array = response.getJSONArray("New");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = (JSONObject) array.get(i);

                Like like = new Like();
                like.setId(object.getInt("id"));
                like.setName(object.getString("name"));

                // Image might be null sometimes
                //String image = object.isNull("image") ? null : object.getString("image");
                like.setImage(object.getString("image"));
                like.setStatus(object.getString("status"));
                like.setProfilePic(object.getString("profilePic"));
                like.setTimeStamp(object.getString("timeStamp"));

                // url might be null sometimes
                String feedUrl = object.isNull("url") ? null : object.getString("url");
                like.setUrl(feedUrl);

                arrayList.add(like);
            }

            // notify data changes to list adapater
            adapter.notifyDataSetChanged();

            // stop animating Shimmer and hide the layout
            mShimmerViewContainer.stopShimmerAnimation();
            mShimmerViewContainer.setVisibility(View.GONE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }


}
