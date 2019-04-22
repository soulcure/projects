package com.applidium.nickelodeon.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.applidium.nickelodeon.SubSeriesActivity;
import com.applidium.nickelodeon.entity.SerieItemInfo;
import com.applidium.nickelodeon.player.SmartPlayingActivity;
import com.applidium.nickelodeon.service.MediaPlayerService;
import com.bumptech.glide.Glide;
import com.applidium.nickelodeon.MNJApplication;
import com.applidium.nickelodeon.R;
import com.applidium.nickelodeon.player.FreePlayingActivity;
import com.applidium.nickelodeon.uitls.GlideRoundTransform;
import com.applidium.nickelodeon.uitls.ImageUtils;

public class RecommendCardView extends RelativeLayout implements View.OnClickListener {

    private static String TAG = RecommendCardView.class.getSimpleName();

    public static final int SMART_PLAY = 0; // 智能推荐播放
    public static final int TOPIC_PLAY = 1; // 专题播放
    public static final int UGC_PLAY = 2; // UGC播放
    public static final int FAVORITE_PLAY = 3; // 收藏播放


    private SerieItemInfo mCartoonItem;

    private int mPlayType;

    private Context mContext;
    private int mViewType = -1;
    private int mRow = -1;

    private int base_res_id;
    //private int default_background_id;

    private ImageView img_serie;
    private TextView tv_serie;
    private TextView tv_info;

    private Drawable mDrawableWhite;
    private Drawable mDrawableShadow;
    private final static int PADDING = 2;

    public RecommendCardView(Context context) {
        super(context);
        mContext = context;
        init(context);
    }

    public RecommendCardView(Context context, int viewType, int row, int playType) {
        super(context);
        mContext = context;
        mViewType = viewType;
        mRow = row;
        mPlayType = playType;
        init(context);
    }


    public RecommendCardView(Context context, int viewType, int row,
                             SerieItemInfo cartoon, int playType) {
        super(context);
        mContext = context;
        mViewType = viewType;
        mRow = row;
        mCartoonItem = cartoon;
        mPlayType = playType;
        init(context);
    }



    public int getRow() {
        return mRow;
    }

    public int getViewType() {
        return mViewType;
    }


    private void init(Context context) {
        this.setClipChildren(true);

        mDrawableWhite = getResources().getDrawable(R.drawable.item_highlight);
        mDrawableShadow = getResources().getDrawable(R.drawable.item_shadow);

        switch (mViewType) {
            case MetroLayout.Vertical:
                base_res_id = R.layout.metro_vertical_item;
                //default_background_id = R.drawable.icon_v_default;
                break;
            case MetroLayout.Horizontal:
                base_res_id = R.layout.metro_horizontal_item;
                //default_background_id = R.drawable.icon_h_default;
                break;
            case MetroLayout.Normal:
                base_res_id = R.layout.metro_normal_item;
                //default_background_id = R.drawable.icon_normal_default;
                break;
            default:
                base_res_id = R.layout.metro_normal_item;
                //default_background_id = R.drawable.icon_normal_default;
                break;
        }


        View view = LayoutInflater.from(context).inflate(base_res_id, this);
        img_serie = (ImageView) view.findViewById(R.id.img_serie);
        tv_serie = (TextView) view.findViewById(R.id.tv_serie);
        TextPaint tvPaint = tv_serie.getPaint();
        tvPaint.setFakeBoldText(true);
        tv_info = (TextView) view.findViewById(R.id.tv_info);

        setOnClickListener(this);

        int roundPx = getResources().getDimensionPixelSize(
                R.dimen.image_round_size);

        if (mCartoonItem != null) {
            Glide.with(context)
                    .load(mCartoonItem.getSubImg())
                    .centerCrop()
                    .bitmapTransform(new GlideRoundTransform(context, roundPx)) //设置图片圆角
                    .placeholder(R.drawable.cartoon_icon_default)  //占位图片
                    .error(R.drawable.cartoon_icon_default)        //下载失败
                    .into(img_serie);
            tv_serie.setText(mCartoonItem.getSubName());

            /*if (!mCartoonItem.isEnd()) {
                tv_info.setText("更新中");
                tv_info.setBackgroundResource(R.drawable.tip_info);
            } else {
                tv_info.setText("");
                tv_info.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
            }*/


        } else {
            if (mViewType == MetroLayout.Horizontal
                    && mPlayType == SMART_PLAY) {
                Bitmap bitmap = ImageUtils.ResourceToBitmap(context, R.drawable.icon_h_default);
                img_serie.setImageBitmap(ImageUtils.getRoundedCornerBitmap(bitmap, roundPx));
                tv_serie.setText(R.string.channel);
            } else if (mViewType == MetroLayout.Horizontal
                    && mPlayType == TOPIC_PLAY) {
                Bitmap bitmap = ImageUtils.ResourceToBitmap(context, R.drawable.icon_h_default);
                img_serie.setImageBitmap(ImageUtils.getRoundedCornerBitmap(bitmap, roundPx));
                tv_serie.setText(R.string.action_channel);
            } /*else if (mViewType == MetroLayout.Horizontal
                    && mPlayType == UGC_PLAY) {
                Bitmap bitmap = ImageUtils.ResourceToBitmap(context, R.drawable.icon_h_default);
                img_serie.setImageBitmap(ImageUtils.getRoundedCornerBitmap(bitmap, roundPx));
                tv_serie.setText(R.string.global_channel);
            } else if (mViewType == MetroLayout.Normal
                    && mPlayType == FAVORITE_PLAY) {
                Bitmap bitmap = ImageUtils.ResourceToBitmap(context, R.drawable.icon_normal_default);
                img_serie.setImageBitmap(ImageUtils.getRoundedCornerBitmap(bitmap, roundPx));
                tv_serie.setText(R.string.my_channel);
            }*/
        }


    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.dispatchDraw(canvas);

        if (isFocused()) {
            int width = getWidth();
            int height = getHeight();

            Rect padding = new Rect();
            mDrawableShadow.getPadding(padding);
            mDrawableShadow.setBounds(-padding.left, -padding.top, width + padding.right, height + padding.bottom);
            mDrawableShadow.draw(canvas);

            mDrawableWhite.getPadding(padding);
            mDrawableWhite.setBounds(-padding.left - PADDING, -padding.top - PADDING, width + padding.right + PADDING, height + padding.bottom + PADDING);
            mDrawableWhite.draw(canvas);
        }


    }


    @Override
    public void onClick(View v) {

        MediaPlayerService.playSound(mContext, MediaPlayerService.ONCLICK);
        if (mCartoonItem != null) {
            Intent intent = new Intent(mContext, SubSeriesActivity.class);

            intent.putExtra("subId", mCartoonItem.getSubId());
            intent.putExtra("SubName", mCartoonItem.getSubName());
            intent.putExtra("subDescription", mCartoonItem.getSubDescription());
            intent.putExtra("subImg", mCartoonItem.getSubImg());

            mContext.startActivity(intent);
        } else {
            if (mPlayType == SMART_PLAY) {
                Intent intent = new Intent(mContext, SmartPlayingActivity.class);
                mContext.startActivity(intent);
            } else if (mPlayType == UGC_PLAY) {
                Intent intent = new Intent(mContext, FreePlayingActivity.class);

                String ugc = ((MNJApplication) mContext.getApplicationContext())
                        .getAppConfig("UGCSerieId");
                try {
                    int serieId = Integer.parseInt(ugc);
                    intent.putExtra("serieId", serieId);
                    mContext.startActivity(intent);
                } catch (NumberFormatException e) {

                }
            } else if (mPlayType == FAVORITE_PLAY) {
                Intent intent = new Intent(mContext, FreePlayingActivity.class);
                mContext.startActivity(intent);
            }
        }


    }
}
