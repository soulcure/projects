package com.ivmall.android.app.dialog;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.FirstRecommItem;
import com.ivmall.android.app.entity.FirstRecommRequest;
import com.ivmall.android.app.entity.FirstRecommResponse;
import com.ivmall.android.app.uitls.GlideRoundTransform;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.uitls.ScreenUtils;
import com.smit.android.ivmall.stb.R;

import java.util.List;


public class FirstRecommDialog extends AlertDialog {


    private Context mContext;
    private int mSerieId;
    private OnClickView mCallBack;

    private LinearLayout serieContainer;

    public FirstRecommDialog(Context context, int serieId) {
        super(context);
        mContext = context;
        mSerieId = serieId;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.first_recomm_dialog);
        initView();
    }


    public void setCallBack(OnClickView callBack) {
        mCallBack = callBack;
    }

    private void initView() {
        serieContainer = (LinearLayout) findViewById(R.id.serie_container);

        Button btn_cacel = (Button) findViewById(R.id.btn_cacel);
        btn_cacel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack != null) {
                    mCallBack.onCancel();
                }
                dismiss();
            }
        });


        reqFirstRecomm(mSerieId);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mCallBack != null) {
            mCallBack.onCancel();
        }
        dismiss();
    }


    /**
     * 1.72 相关推荐接口
     */
    private void reqFirstRecomm(int serieId) {
        String url = AppConfig.FIRST_RECOMM;
        FirstRecommRequest request = new FirstRecommRequest();

        String token = ((KidsMindApplication) mContext.getApplicationContext()).getToken();
        request.setToken(token);
        request.setSerieId(serieId);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                FirstRecommResponse resp = GsonUtil.parse(response,
                        FirstRecommResponse.class);
                if (resp.isSucess() && resp.getData() != null) {
                    setRecommListInfo(resp.getData());
                } else {
                    if (mCallBack != null) {
                        mCallBack.onCancel();
                    }
                    dismiss();
                    Toast.makeText(mContext, resp.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        });
    }


    private void setRecommListInfo(List<FirstRecommItem> list) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        for (int i = 0; i < list.size(); i++) {
            final FirstRecommItem item = list.get(i);
            final View view = inflater.inflate(R.layout.serie_item, serieContainer, false);
            view.setTag(item);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int margin = (int) ScreenUtils.dpToPx(mContext, 30);
            params.rightMargin = margin;
            serieContainer.addView(view, i, params);

            ImageView img_serie = (ImageView) view.findViewById(R.id.img_serie);//剧集图片
            int roundPx = mContext.getResources().getDimensionPixelSize(
                    R.dimen.image_round_size);
            Glide.with(mContext)
                    .load(item.getSerieImgUrl())
                    .centerCrop()
                    .bitmapTransform(new GlideRoundTransform(mContext, roundPx)) //设置图片圆角
                    .placeholder(R.drawable.cartoon_icon_default)  //占位图片
                    .error(R.drawable.cartoon_icon_default)        //下载失败
                    .into(img_serie);

            TextView tv_serie = (TextView) view.findViewById(R.id.tv_serie);//剧集名称
            tv_serie.setText(item.getSerieName());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallBack != null) {
                        FirstRecommItem firstRecommItem = (FirstRecommItem) v.getTag();
                        mCallBack.onClick(firstRecommItem.getSerieId());
                    }
                    dismiss();
                }
            });
            view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        ObjectAnimator animX = ObjectAnimator.ofFloat(v, "ScaleX",
                                new float[]{1.0F, 1.02F}).setDuration(200);
                        ObjectAnimator animY = ObjectAnimator.ofFloat(v, "ScaleY",
                                new float[]{1.0F, 1.02F}).setDuration(200);
                        AnimatorSet scaleAnimator = new AnimatorSet();
                        scaleAnimator.playTogether(new Animator[]{animX, animY});
                        scaleAnimator.start();
                    } else {
                        ObjectAnimator animX = ObjectAnimator.ofFloat(v, "ScaleX",
                                new float[]{1.02F, 1.0F}).setDuration(200);
                        ObjectAnimator animY = ObjectAnimator.ofFloat(v, "ScaleY",
                                new float[]{1.02F, 1.0F}).setDuration(200);
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.playTogether(new Animator[]{animX, animY}); //设置两个动画一起执行
                        animatorSet.start();
                    }
                }
            });

        }

        /*ViewGroup.LayoutParams params = serieContainer.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        serieContainer.setLayoutParams(params);*/
    }


    public interface OnClickView {
        void onClick(int serieId);

        void onCancel();
    }
}
