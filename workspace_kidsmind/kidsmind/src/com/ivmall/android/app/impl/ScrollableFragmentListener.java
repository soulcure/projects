package com.ivmall.android.app.impl;

public interface ScrollableFragmentListener {

    void onFragmentAttached(ScrollableListener fragment, int position);

    void onFragmentDetached(ScrollableListener fragment, int position);
}
