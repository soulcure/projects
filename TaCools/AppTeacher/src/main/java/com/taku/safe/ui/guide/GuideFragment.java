package com.taku.safe.ui.guide;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.taku.safe.R;

/**
 * Created by soulcure on 2017/7/11.
 */

public class GuideFragment extends Fragment {
    public final static String TAG = GuideFragment.class.getSimpleName();

    private static final String ARG_DRAWABLE = "drawable";
    private static final String TITLE = "title";
    private static final String CONTENT = "content";

    private int drawable;
    private int title;
    private int content;

    public static GuideFragment newInstance(@DrawableRes int imageDrawable,
                                            @StringRes int title, @StringRes int content) {
        GuideFragment slide = new GuideFragment();
        Bundle args = new Bundle();

        args.putInt(ARG_DRAWABLE, imageDrawable);
        args.putInt(TITLE, title);
        args.putInt(CONTENT, content);

        slide.setArguments(args);

        return slide;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().size() != 0) {
            drawable = getArguments().getInt(ARG_DRAWABLE);
            title = getArguments().getInt(TITLE);
            content = getArguments().getInt(CONTENT);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_guide, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initView(view);
    }


    private void initView(View view) {
        ImageView img_guide = (ImageView) view.findViewById(R.id.img_guide);
        img_guide.setImageResource(drawable);

        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText(title);

        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
        tv_content.setText(content);
    }


}
