package com.sample.xteamtest.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sample.xteamtest.App;
import com.sample.xteamtest.R;
import com.sample.xteamtest.events.NetworkCallFailureEvent;
import com.sample.xteamtest.events.SwipeRefreshEvent;
import com.sample.xteamtest.rest.model.Face;
import com.sample.xteamtest.ui.adapters.GridAdapter;
import com.sample.xteamtest.ui.controllers.MainController;
import com.squareup.okhttp.Cache;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private TextView mErrorView;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private Toolbar mToolbar;
    private MenuItem mSearchItem;
    private MenuItem mSearchCloseItem;
    private EditText mToolbarSearch;
    private CheckBox mInStockCheckBox;

    private MainController mMainController;
    private boolean canFetchData = true;
    private boolean mInStockCheck = false;

    private int pastVisibleItems, visibleItemCount, totalItemCount;

    private List<Face> mListOfFaces;
    private static long CACHE_SIZE = 5 * 1024 * 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        mMainController = new MainController();
        mListOfFaces = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mErrorView = (TextView) findViewById(R.id.error_view);

        //Setup Search
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarSearch = (EditText) findViewById(R.id.toolbar_search);
        mToolbarSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (mToolbarSearch.getText().length() != 0)
                    mMainController.getData(null, null,
                            mToolbarSearch.getText().toString(), mInStockCheck);
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mToolbarSearch.getWindowToken(), 0);
                return true;
            }
        });

        mInStockCheckBox = (CheckBox) findViewById(R.id.in_stock);
        mInStockCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mInStockCheck = isChecked;
                if (mToolbarSearch.getVisibility() == View.VISIBLE) {
                    mMainController.getData(null, null,
                            mToolbarSearch.getText().toString(), mInStockCheck);
                } else {
                    mMainController.getData(null, null, null, mInStockCheck);
                }
                mListOfFaces.clear();
                mAdapter.notifyDataSetChanged();
            }
        });
        mRefreshLayout.setEnabled(false);
        setSupportActionBar(mToolbar);

        if (mRecyclerView != null) {
            setupGrid();
        }
    }

    private void setupGrid() {
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new GridAdapter(mListOfFaces);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);

        //scroll listener
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView mRecyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
                    if (canFetchData) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            if (mToolbarSearch.getVisibility() == View.VISIBLE) {
                                mMainController.getData(null, totalItemCount + "",
                                        mToolbarSearch.getText().toString(), mInStockCheck);
                            } else {
                                mMainController.getData(null, totalItemCount + "",
                                        null, mInStockCheck);
                            }
                            canFetchData = false;
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.getBus().register(this);
        if (mMainController == null) {
            mMainController = new MainController();
        }
        Cache cache = new Cache(new File(getCacheDir(), "http"), CACHE_SIZE);
        mMainController.init(cache);
        mMainController.getData(null, null, null, mInStockCheck);
    }

    @Override
    protected void onPause() {
        super.onPause();
        App.getBus().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        mSearchItem = menu.findItem(R.id.action_search);
        mSearchCloseItem = menu.findItem(R.id.action_search_close);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            mToolbarSearch.setVisibility(View.VISIBLE);
            mToolbarSearch.requestFocus();
            InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mToolbarSearch, InputMethodManager.SHOW_IMPLICIT);
            item.setVisible(false);
            mSearchCloseItem.setVisible(true);
            mListOfFaces.clear();
            mRecyclerView.invalidate();
        } else if (id == R.id.action_search_close) {
            InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mToolbarSearch.getWindowToken(), 0);
            mToolbarSearch.setText("");
            mToolbarSearch.setVisibility(View.GONE);
            item.setVisible(false);
            mSearchItem.setVisible(true);
            mListOfFaces.clear();
            mAdapter.notifyDataSetChanged();
            mMainController.getData(null, null, null, mInStockCheck);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Toggles SwipeRefreshLayout progress
     */
    @Subscribe
    public void showSwipeProgress(SwipeRefreshEvent swipeRefreshEvent) {
        if (mRefreshLayout != null && swipeRefreshEvent.isShowRefresh()) {
            mRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mRefreshLayout.setRefreshing(true);
                }
            });
        } else if (mRefreshLayout != null && !swipeRefreshEvent.isShowRefresh()) {
            mRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mRefreshLayout.setRefreshing(false);
                }
            });
        } else {
        }
    }

    @Subscribe
    public void onEvent(String responseBody) {
        if(responseBody == null || (responseBody != null && responseBody.length() == 0)) {
            Toast.makeText(getApplicationContext(), "No result found", Toast.LENGTH_LONG).show();
        } else {
            List<String> responseList = Arrays.asList(responseBody.split("null"));
            Gson gson = new Gson();
            for (int i = 0; i < responseList.size(); ++i) {
                Face face = gson.fromJson(responseList.get(i), Face.class);
                mListOfFaces.add(face);
            }
            mAdapter.notifyDataSetChanged();
            canFetchData = true;
            hideErrorView();
        }
        App.getBus().post(new SwipeRefreshEvent(false));
    }

    @Subscribe
    public void onEvent(NetworkCallFailureEvent event) {
        if (mAdapter.getItemCount() == 0) {
            showErrorView();
        } else {
            hideErrorView();
            Toast.makeText(getApplicationContext(), "No more new faces available",
                    Toast.LENGTH_LONG).show();
        }
        App.getBus().post(new SwipeRefreshEvent(false));
    }

    private void showErrorView() {
        mRecyclerView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
    }

    private void hideErrorView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.GONE);
    }
}
