package com.applidium.nickelodeon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.applidium.nickelodeon.adapter.RecyclerGridViewAdapter;
import com.applidium.nickelodeon.config.AppConfig;
import com.applidium.nickelodeon.entity.ContentItem;
import com.applidium.nickelodeon.entity.ContentListRequest;
import com.applidium.nickelodeon.entity.ContentListResponse;
import com.applidium.nickelodeon.player.FreePlayingActivity;
import com.applidium.nickelodeon.service.MediaPlayerService;
import com.applidium.nickelodeon.uitls.AppUtils;
import com.applidium.nickelodeon.uitls.GlideRoundTransform;
import com.applidium.nickelodeon.uitls.GsonUtil;
import com.applidium.nickelodeon.uitls.HttpConnector;
import com.applidium.nickelodeon.uitls.IPostListener;
import com.applidium.nickelodeon.uitls.ScreenUtils;
import com.applidium.nickelodeon.uitls.StringUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class SubSeriesActivity extends Activity implements View.OnClickListener {

    private static final String TAG = SubSeriesActivity.class.getSimpleName();


    private Context mContext;

    private ImageView img_serie;
    private TextView tv_name;
    private TextView tv_desc;
    private ImageButton imgbtn_play;
    private ImageButton imgbtn_back;
    private RecyclerView mRecyclerView;

    private int mSerieId;
    //列
    private int adaperColumn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subseries_activity);
        mContext = this;
        Intent intent = getIntent();

        mSerieId = intent.getIntExtra("subId", 0);
        String subName = intent.getStringExtra("SubName");
        String subDescription = intent.getStringExtra("subDescription");
        String subImg = intent.getStringExtra("subImg");

        img_serie = (ImageView) findViewById(R.id.img_serie);
        tv_name = (TextView) findViewById(R.id.tv_name);
        imgbtn_back = (ImageButton) findViewById(R.id.btn_back);
        if (ScreenUtils.isTv(mContext)) {
            imgbtn_back.setVisibility(View.GONE);
        } else {
            imgbtn_back.setOnClickListener(this);
        }
        tv_name.setText(subName);

        tv_desc = (TextView) findViewById(R.id.tv_desc);
        tv_desc.setText(subDescription);

        imgbtn_play = (ImageButton) findViewById(R.id.imgbtn_play);
        imgbtn_play.setOnClickListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        int srceen_w = ScreenUtils.getWidthPixels(this);  //屏幕宽度
        int item_w = getResources().getDimensionPixelSize(R.dimen.SUBSERIES_SIZE_WIDTH); //列宽度

        int padding_w = getResources().getDimensionPixelSize(R.dimen.SUBSERIES_PADDING);  //RecyclerView 左右padding

        adaperColumn = srceen_w / (item_w + 2 * padding_w);

        // 创建一个网格布局管理器
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, adaperColumn);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(layoutManager);

        int roundPx = getResources().getDimensionPixelSize(
                R.dimen.image_round_size);
        Glide.with(this)
                .load(subImg)
                .centerCrop()
                .bitmapTransform(new GlideRoundTransform(this, roundPx)) //设置图片圆角
                .placeholder(R.drawable.ic_launcher)  //占位图片
                .error(R.drawable.ic_launcher)        //下载失败
                .into(img_serie);

        reqContentList(mSerieId);
    }


    @Override
    public void onClick(View v) {

        MediaPlayerService.playSound(mContext, MediaPlayerService.ONCLICK);
        int id = v.getId();
        if (id == R.id.imgbtn_play) {

            Intent intent = new Intent(mContext, FreePlayingActivity.class);
            intent.putExtra("serieId", mSerieId);

            RecyclerGridViewAdapter adapter = (RecyclerGridViewAdapter) mRecyclerView.getAdapter();
            ArrayList<ContentItem> list = adapter.getList();

            String record = AppUtils.getStringSharedPreferences(mContext, "PLAY_RECORD" + mSerieId, "");
            if (!StringUtils.isEmpty(record)) {
                String[] strs = record.split("#");
                if (strs != null && strs.length == 3) {
                    int episodeId = Integer.parseInt(strs[0]);
                    int lastPos = Integer.parseInt(strs[1]);
                    int langIndex = Integer.parseInt(strs[2]);
                    for (int i = 0; i < list.size(); i++) {
                        if (episodeId == list.get(i).getContentId()) {
                            intent.putExtra("position", i);
                            intent.putExtra("lastPos", lastPos);
                            intent.putExtra("langIndex", langIndex);
                            break;
                        }
                    }
                }

            }

            intent.putParcelableArrayListExtra("series", list);
            mContext.startActivity(intent);

        } else if (id == R.id.btn_back) {
            finish();
        }

    }

    public void reqContentList(int subId) {
        String url = AppConfig.CONTENT_LIST;
        ContentListRequest request = new ContentListRequest();

        String token = ((MNJApplication) getApplication()).getToken();

        request.setToken(token);
        request.setSubSeriesId(subId);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                ContentListResponse resp = GsonUtil.parse(response,
                        ContentListResponse.class);
                if (resp.isSucess()) {
                    ArrayList<ContentItem> list = resp.getList();
                    RecyclerGridViewAdapter adapter = new RecyclerGridViewAdapter(mContext,
                            list, mSerieId);
                    mRecyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(mContext, resp.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        });
    }


}