package com.sample.xteamtest.events;

/**
 * Created by nitishmehrotra.
 */
public class SwipeRefreshEvent {
    private boolean mShowRefresh;

    public SwipeRefreshEvent() {
        this.mShowRefresh = false;
    }

    public SwipeRefreshEvent(boolean showRefreshEvent) {
        this.mShowRefresh = showRefreshEvent;
    }

    public boolean isShowRefresh() {
        return mShowRefresh;
    }

    public void setShowRefresh(boolean showRefresh) {
        this.mShowRefresh = showRefresh;
    }
}
