package com.ivmall.android.app.fragment;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.CartoonItem;
import com.ivmall.android.app.entity.CartoonRoleResponse;
import com.ivmall.android.app.entity.MainPlayListRequest;
import com.ivmall.android.app.player.FreePlayingActivity;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.GlideRoundTransform;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.uitls.OnEventId;
import com.ivmall.android.app.uitls.StringUtils;
import com.ivmall.android.app.views.MirrorItemView;
import com.opensource.widget.HorizontalGridView;
import com.opensource.widget.RecyclerView;
import com.smit.android.ivmall.stb.R;

import java.util.List;


public class PlayListFragment extends Fragment {
    private static final String TAG = PlayListFragment.class.getSimpleName();

    private HorizontalGridView mHorizontalGridView;
    private Activity mAct;
    private String mInfo;

    public static PlayListFragment newInstance(String info) {
        Bundle args = new Bundle();
        PlayListFragment fragment = new PlayListFragment();
        args.putString("info", info);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mAct = activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mInfo = getArguments().getString("info");
        return inflater.inflate(R.layout.cartoon_fragment, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        int offset = getResources().getDimensionPixelSize(R.dimen.ITEM_DIVIDE_SIZE);
        mHorizontalGridView = (HorizontalGridView) view.findViewById(R.id.hgv);
        mHorizontalGridView.setRowHeight(getResources().getDimensionPixelSize(R.dimen.ITEM_NORMAL_SIZE)
                + offset);

        mHorizontalGridView.setAlwaysDrawnWithCacheEnabled(true);
        mHorizontalGridView.setNumRows(2);
        mHorizontalGridView.setHorizontalMargin(offset);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        reqMainList(0, 1000);
    }


    /**
     * 1.17 获取首页剧集列表
     */
    private void reqMainList(final int start, final int offset) {
        String url = AppConfig.MAIN_PLAYLIST_V2;
        MainPlayListRequest request = new MainPlayListRequest();

        String token = ((KidsMindApplication) mAct.getApplication()).getToken();
        request.setToken(token);
        request.setStartIndex(start);
        request.setOffset(offset);
        request.setCategory(mInfo);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                CartoonRoleResponse resp = GsonUtil.parse(response,
                        CartoonRoleResponse.class);
                if (resp != null && resp.isSucess()) {
                    List<CartoonItem> list = resp.getData().getList();
                    mHorizontalGridView.setAdapter(new CartoonRecyclerAdapter(list));
                }

            }

        });
    }


    private class CartoonRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<CartoonItem> mList;

        public CartoonRecyclerAdapter(List<CartoonItem> list) {
            mList = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int position) {
            LayoutInflater inflater = getActivity().getLayoutInflater();

            View view = inflater.inflate(R.layout.cartoon_item, null);

            MirrorItemView mirrorItemView = new MirrorItemView(mAct);
            FrameLayout.LayoutParams params = new
                    FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            mirrorItemView.setContentView(view, params);
            mirrorItemView.setFocusable(true);

            return new ViewHolder(mirrorItemView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

            final CartoonItem item = mList.get(position);
            ((ViewHolder) viewHolder).tv_serie.setText(item.getTitle());

            TextView tv_info = ((ViewHolder) viewHolder).tv_info;

            if (!item.isEnd()) {
                tv_info.setText(R.string.text_new);
                tv_info.setBackgroundResource(R.drawable.tip_info);
            } else {
                tv_info.setText("");
                tv_info.setBackgroundColor(Color.TRANSPARENT);
            }

            /*TextView tv_tag = ((ViewHolder) viewHolder).tv_tag;
            String tab = item.getTag();
            if (!StringUtils.isEmpty(tab)) {
                if (tab.equals("hd")) {
                    tv_tag.setText(R.string.text_gaoqing);
                    tv_tag.setBackgroundResource(R.drawable.gaoqing);
                } else if (tab.equals("sd")) {
                    tv_tag.setText(R.string.text_biaoqing);
                    tv_tag.setBackgroundResource(R.drawable.biaoqing);
                } else if (tab.equals("audio")) {
                    tv_tag.setText("");//v_tag.setText("听");
                    tv_tag.setBackgroundResource(R.drawable.ting);
                }
            }*/

            String url = item.getImgUrl();
            int roundPx = getResources().getDimensionPixelSize(
                    R.dimen.image_round_size);
            Glide.with(getActivity())
                    .load(url)
                    .centerCrop()
                    .bitmapTransform(new GlideRoundTransform(getActivity(), roundPx)) //设置图片圆角
                    .placeholder(R.drawable.cartoon_icon_default)  //占位图片
                    .error(R.drawable.cartoon_icon_default)        //下载失败
                    .into(((ViewHolder) viewHolder).img_serie);

            if (position % 2 == 0) {
                ((ViewHolder) viewHolder).mirrorItemView.setHasReflection(false);
            } else {
                ((ViewHolder) viewHolder).mirrorItemView.setHasReflection(true);
            }

            ((ViewHolder) viewHolder).mirrorItemView.getContentView()
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CartoonItem item = mList.get(position);

                            Intent intent = new Intent(mAct, FreePlayingActivity.class);
                            String tab = item.getTag();
                            if (!StringUtils.isEmpty(tab)) {
                                if (tab.equals("audio")) {
                                    intent.putExtra("type", FreePlayingActivity.FROM_MUSIC_PLAY);
                                } else {
                                    intent.putExtra("type", FreePlayingActivity.FROM_NORMAL);
                                }
                            } else {
                                intent.putExtra("type", FreePlayingActivity.FROM_NORMAL);
                            }
                            intent.putExtra("serieId", item.getSerieId());
                            startActivity(intent);
                            BaiduUtils.onEvent(mAct, OnEventId.FREE_PLAY_ITEM,
                                    getString(R.string.free_play_item) + item.getTitle());
                        }
                    });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }


    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnFocusChangeListener {
        ImageView img_serie;
        TextView tv_serie;
        TextView tv_tag;
        TextView tv_info;
        MirrorItemView mirrorItemView;

        public ViewHolder(View itemView) {
            super(itemView);
            img_serie = (ImageView) itemView.findViewById(R.id.img_serie);
            tv_serie = (TextView) itemView.findViewById(R.id.tv_serie);
            tv_tag = (TextView) itemView.findViewById(R.id.tv_tag);
            tv_info = (TextView) itemView.findViewById(R.id.tv_info);
            mirrorItemView = (MirrorItemView) itemView;

            itemView.setOnFocusChangeListener(this);
        }


        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                ObjectAnimator animX = ObjectAnimator.ofFloat(v, "ScaleX",
                        new float[]{1.0F, 1.1F}).setDuration(200);
                ObjectAnimator animY = ObjectAnimator.ofFloat(v, "ScaleY",
                        new float[]{1.0F, 1.1F}).setDuration(200);
                AnimatorSet scaleAnimator = new AnimatorSet();
                scaleAnimator.playTogether(new Animator[]{animX, animY});
                scaleAnimator.start();
            } else {
                ObjectAnimator animX = ObjectAnimator.ofFloat(v, "ScaleX",
                        new float[]{1.1F, 1.0F}).setDuration(200);
                ObjectAnimator animY = ObjectAnimator.ofFloat(v, "ScaleY",
                        new float[]{1.1F, 1.0F}).setDuration(200);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{animX, animY}); //设置两个动画一起执行
                animatorSet.start();
            }
        }
    }
}
