package com.taku.safe.tabs;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taku.safe.BasePermissionFragment;
import com.taku.safe.R;
import com.taku.safe.data.VideoContract;

import java.lang.ref.WeakReference;


public class SecondFragment extends BasePermissionFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public final static String TAG = SecondFragment.class.getSimpleName();

    private UIHandler mHandler;

    private static final int CATEGORY_LOADER = 123; // Unique ID for Category Loader.

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Start loading the categories from the database.
        getLoaderManager().initLoader(CATEGORY_LOADER, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new UIHandler(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_practice, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onPause() {
        super.onPause();

    }


    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == CATEGORY_LOADER) {
            return new CursorLoader(
                    getActivity(),   // Parent activity context
                    VideoContract.VideoEntry.CONTENT_URI, // Table to query
                    new String[]{"DISTINCT " + VideoContract.VideoEntry.COLUMN_CATEGORY},
                    // Only categories
                    null, // No selection clause
                    null, // No selection arguments
                    null  // Default sort order
            );
        } else {
            // Assume it is for a video.
            String category = args.getString(VideoContract.VideoEntry.COLUMN_CATEGORY);

            // This just creates a CursorLoader that gets all videos.
            return new CursorLoader(
                    getActivity(), // Parent activity context
                    VideoContract.VideoEntry.CONTENT_URI, // Table to query
                    null, // Projection to return - null means return all fields
                    VideoContract.VideoEntry.COLUMN_CATEGORY + " = ?", // Selection clause
                    new String[]{category},  // Select based on the category id.
                    null // Default sort order
            );
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            final int loaderId = loader.getId();

            if (loaderId == CATEGORY_LOADER) {

                // Every time we have to re-get the category loader, we must re-create the sidebar.
                // mCategoryRowAdapter.clear();

                // Iterate through each category entry and add it to the ArrayAdapter.
                while (!data.isAfterLast()) {

                    int categoryIndex =
                            data.getColumnIndex(VideoContract.VideoEntry.COLUMN_CATEGORY);
                    String category = data.getString(categoryIndex);


                    // Start loading the videos from the database for a particular category.
                        /*Bundle args = new Bundle();
                        args.putString(VideoContract.VideoEntry.COLUMN_CATEGORY, category);
                        getLoaderManager().initLoader(videoLoaderId, args, this);*/


                    data.moveToNext();
                }

                // cursors have loaded.
            } else {
                // The CursorAdapter contains a Cursor pointing to all videos.
                // mVideoCursorAdapters.get(loaderId).changeCursor(data);
            }
        } else {
            // Start an Intent to fetch the videos.

            //for
            //Intent serviceIntent = new Intent(getActivity(), FetchVideoService.class);
            //getActivity().startService(serviceIntent);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        int loaderId = loader.getId();
        if (loaderId != CATEGORY_LOADER) {
            //mVideoCursorAdapters.get(loaderId).changeCursor(null);
        } else {
            //mCategoryRowAdapter.clear();
        }
    }

    /**
     * service handler
     */
    public static class UIHandler extends Handler {
        private final WeakReference<SecondFragment> mTarget;

        UIHandler(SecondFragment target) {
            mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {

        }
    }

}
