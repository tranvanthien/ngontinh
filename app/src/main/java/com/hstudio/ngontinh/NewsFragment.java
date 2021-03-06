package com.hstudio.ngontinh;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hstudio.ngontinh.adapter.StoryAdapter;
import com.hstudio.ngontinh.async.LoadStories;
import com.hstudio.ngontinh.object.Story;
import com.hstudio.ngontinh.utils.EndlessRecyclerViewScrollListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ratan on 7/29/2015.
 */
public class NewsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private StoryAdapter mStoryAdapter;
    private SwipeRefreshLayout mSwipeContainer;
    private String mUrl;

    public Fragment setUrl(String url) {
        mUrl = url;
        return this;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_layout,null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mSwipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        initRecyclerView(view);
        mSwipeContainer.post(new Runnable() {
            @Override
            public void run() {
                mSwipeContainer.setRefreshing(true);
                new LoadStories(NewsFragment.this).execute(0);
            }
        });
        return view;
    }

    public void onStart () {
        super.onStart();
    }

    private void initRecyclerView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        List<Story> storys = new ArrayList<>();
        mStoryAdapter = new StoryAdapter(storys, getActivity());
        mRecyclerView.setAdapter(mStoryAdapter);

        // Setup refresh listener which triggers new data loading
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                new LoadStories(NewsFragment.this).execute(0);
                mSwipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {

            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                new LoadStories(NewsFragment.this).execute(totalItemsCount);
            }
        });

    }

    public void setStories(List<Story> stories) {
        mStoryAdapter.addStoryList(stories);
        mStoryAdapter.notifyDataSetChanged();
        mSwipeContainer.setRefreshing(false);
    }
}
