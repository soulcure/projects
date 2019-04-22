package com.ivmall.android.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ivmall.android.app.BaseActivity;
import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.MainFragmentActivity;
import com.ivmall.android.app.R;
import com.ivmall.android.app.adapter.MineAdapter;
import com.ivmall.android.app.config.MineItem;
import com.ivmall.android.app.entity.ProfileItem;
import com.ivmall.android.app.impl.OnSucessListener;
import com.ivmall.android.app.uitls.GlideCircleTransform;
import com.ivmall.android.app.uitls.LoginUtils;
import com.ivmall.android.app.uitls.ScreenUtils;
import com.ivmall.android.app.uitls.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koen on 2016/3/16.
 */
public class MineFragment extends Fragment implements View.OnClickListener {

    private Context mContext;

    private ImageView imgHead;
    private TextView tvHead;
    private TextView tvLoginOut;

    private RecyclerView recyclerView;
    private List<MineItem> mineItemList;

    @Override
    public void onAttach(Activity activity) {
        mContext = activity;
        super.onAttach(activity);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mine_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        tvHead = (TextView) view.findViewById(R.id.tv_head);
        imgHead = (ImageView) view.findViewById(R.id.img_head);
        tvLoginOut = (TextView) view.findViewById(R.id.tv_login_out);
        imgHead.setOnClickListener(this);
        tvLoginOut.setOnClickListener(this);
        tvHead.setOnClickListener(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        int columns = getResources().getInteger(R.integer.mime_fragment_phone_columns);
        if (!ScreenUtils.isPhone(getActivity())) {
            columns = getResources().getInteger(R.integer.mime_fragment_pad_columns);
        }

        GridLayoutManager manager = new GridLayoutManager(getActivity(), columns);
        recyclerView.setLayoutManager(manager);

        MineAdapter adapter = new MineAdapter(getActivity(), mineItemList);
        recyclerView.setAdapter(adapter);

        int paddingItem = getResources().getDimensionPixelOffset(R.dimen.item_space);
        recyclerView.addItemDecoration(new PaddingItemDecoration(paddingItem));
    }


    private void initData() {
        mineItemList = new ArrayList<MineItem>(6);

        MineItem vip = new MineItem();
        vip.setDrawableId(R.drawable.btn_vip_selector);
        vip.setStringId(R.string.open_vip);
        vip.setClickId(BaseActivity.BUGVIP);
        mineItemList.add(vip);

        MineItem babyInfo = new MineItem();
        babyInfo.setDrawableId(R.drawable.btn_baby_message_selector);
        babyInfo.setStringId(R.string.babyinfo);
        babyInfo.setClickId(BaseActivity.BABYINFO);
        mineItemList.add(babyInfo);

        MineItem record = new MineItem();
        record.setDrawableId(R.drawable.btn_record_selector);
        record.setStringId(R.string.record);
        record.setClickId(BaseActivity.RECORD);
        mineItemList.add(record);

        MineItem myPost = new MineItem();
        myPost.setDrawableId(R.drawable.btn_posts_selector);
        myPost.setStringId(R.string.my_post);
        myPost.setClickId(BaseActivity.MY_BBS);
        mineItemList.add(myPost);

        MineItem response = new MineItem();
        response.setDrawableId(R.drawable.btn_talk_selector);
        response.setStringId(R.string.response);
        response.setClickId(BaseActivity.RESPONSE);
        mineItemList.add(response);

        MineItem aboutUs = new MineItem();
        aboutUs.setDrawableId(R.drawable.btn_us_selector);
        aboutUs.setStringId(R.string.p_about_us);
        aboutUs.setClickId(BaseActivity.ABOUT_US);
        mineItemList.add(aboutUs);

    }


    /**
     * 获取用户头像
     */
    private void initHeadImage() {
        ProfileItem profileItem = ((KidsMindApplication) getActivity().getApplication()).getProfile();
        if (profileItem != null) {
            initHead(profileItem);
        } else {
            ((KidsMindApplication) getActivity().getApplication()).reqProfile(new OnReqProfileResult());
        }
    }


    private void initHead(ProfileItem profileItem) {
        String imgUrl = profileItem.getImgUrl();
        if (!StringUtils.isEmpty(imgUrl)) {
            Glide.with(mContext)
                    .load(imgUrl)
                    .centerCrop()
                    .bitmapTransform(new GlideCircleTransform(mContext)) //设置图片圆角
                    .placeholder(R.drawable.icon_login_image)  //占位图片
                    .error(R.drawable.icon_login_image)        //下载失败
                    .into(imgHead);

        }
        if (((KidsMindApplication) getActivity().getApplication()).isLogin()) {
            //tvHead.setText(profileItem.getNickname());
            tvHead.setText(((KidsMindApplication) getActivity().getApplication()).getUserName());
            tvLoginOut.setVisibility(View.VISIBLE);
        } else {
            tvHead.setText(R.string.please_login);
            tvLoginOut.setVisibility(View.INVISIBLE);
        }
    }

    private class OnReqProfileResult implements OnSucessListener {

        @Override
        public void sucess() {
            ProfileItem list = ((KidsMindApplication) getActivity().getApplication()).getProfile();
            if (list != null) {
                initHead(list);
            }
        }

        @Override
        public void create() {
        }

        @Override
        public void fail() {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initHeadImage();
    }

    @Override
    public void onClick(View arg0) {
        int i = arg0.getId();

        switch (i) {
            case R.id.tv_head:
            case R.id.img_head:
                if (!((KidsMindApplication) getActivity().getApplication()).isLogin()) {
                    Intent intent = new Intent(mContext, BaseActivity.class);
                    intent.putExtra(BaseActivity.NAME, BaseActivity.LOGIN);
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, BaseActivity.class);
                    intent.putExtra(BaseActivity.NAME, BaseActivity.USER_INFO);
                    mContext.startActivity(intent);
                }
                break;

            case R.id.tv_login_out:
                showQuitDialog(mContext);
                break;
        }

    }

    private void loginOut() {
        LoginUtils loginUtils = new LoginUtils(mContext);

        loginUtils.setLoginOutSuccess(new LoginUtils.LoginOutListener() {
            @Override
            public void onSuccess() {
                initHeadImage();
                if (ScreenUtils.isPhone(mContext))
                ((MainFragmentActivity) getActivity()).initHeadImage();
            }

            @Override
            public void onFailed(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }
        });
        loginUtils.loginOut();
    }

    private void showQuitDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.login_out))
                .setMessage(context.getString(R.string.login_out_msg));

        builder.setPositiveButton(context.getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0,
                                        int arg1) {
                        loginOut();
                        arg0.dismiss();

                    }
                });

        builder.setNegativeButton(context.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0,
                                        int arg1) {
                        arg0.dismiss();
                    }
                });
        builder.show();
    }


    class PaddingItemDecoration extends RecyclerView.ItemDecoration {

        private int mSpace;

        public PaddingItemDecoration(int space) {
            mSpace = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.left = mSpace;
            outRect.top = mSpace;
            outRect.right = mSpace;
            outRect.bottom = mSpace;
        }
    }
}
