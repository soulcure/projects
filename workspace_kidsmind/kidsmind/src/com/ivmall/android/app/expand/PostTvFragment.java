package com.ivmall.android.app.expand;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.ivmall.android.app.BaseActivity;
import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.R;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.CartoonItem;
import com.ivmall.android.app.entity.CartoonRoleResponse;
import com.ivmall.android.app.entity.MainPlayListRequest;
import com.ivmall.android.app.entity.MakeEpisodeListRequest;
import com.ivmall.android.app.entity.SelectItem;
import com.ivmall.android.app.entity.SerieInfoRequest;
import com.ivmall.android.app.entity.SerieInfoResponse;
import com.ivmall.android.app.entity.SerieItem;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.views.SmoothCheckBox;
import com.jauker.widget.BadgeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koen on 2016/4/8.
 */
public class PostTvFragment extends Fragment implements View.OnClickListener {

    private RecyclerView groupListView;
    private RecyclerView childListView;
    private RecyclerView selectedListView;
    private PopupWindow mpopuWindow;
    private GroupAdapter groupAdapter;
    private ChildAdapter childAdapter;
    private SelectedAdapter selectedAdapter;
    private ImageView selectCar; //节目车
    private ViewGroup animMaskLayout; //动画层
    private ImageView selectImg; //在界面上飞行的图片
    private BadgeView selectNumView; //显示购买数量的控件
    private Context mContent;
    private Button sendBtn;
    private Button sendBtnPopu;
    private BaseActivity activity;
    private static final int DURATION = 500;
    private List<SelectItem> selectItemList;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContent = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.post_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        activity = (BaseActivity) getActivity();
        init(view);
        reqMainPlayList();
    }


    private void init(View view) {
        selectItemList = new ArrayList<>();
        selectCar = (ImageView) view.findViewById(R.id.select_car);
        selectCar.setOnClickListener(this);
        sendBtn = (Button) view.findViewById(R.id.program_send);
        sendBtn.setOnClickListener(this);
        selectNumView = new BadgeView(mContent);
        selectNumView.setTargetView(selectCar);
        groupAdapter = new GroupAdapter(mContent);
        childAdapter = new ChildAdapter(mContent);
        selectedAdapter = new SelectedAdapter(mContent);

        groupListView = (RecyclerView) view.findViewById(R.id.list_group);
        childListView = (RecyclerView) view.findViewById(R.id.list_child);
        groupListView.setLayoutManager(new LinearLayoutManager(mContent));
        childListView.setLayoutManager(new LinearLayoutManager(mContent));

        selectedAdapter.setOnItemClickLitener(new SelectedAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(SelectItem item) {
                deleteInfo(item.getEpisodeId());

            }
        });
        groupAdapter.setOnItemClickLitener(new GroupAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position, int serieId) {
                // 先清空子list
                childAdapter.setChildItem(null, 0, null);
                // 请求子list
                reqPlayListInfo(serieId);
                // 刷新页面
                groupAdapter.setmCurSelectPosion(position);
            }
        });

        childAdapter.setOnItemCheckChangedLitener(new ChildAdapter.OnItemCheckChangedLitener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox smoothCheckBox, boolean checked, SerieItem item, int SerieId, boolean isSelected) {
                // 排除重复选择的，为了防止重复加载子list时，自动选中已选list，导致该方法重复被调用。
                if (checked) {
                    if(!isSelected) {
                        // 开始动画
                        startAnim(smoothCheckBox);
                        // 将已选项添加至list
                        addInfo(SerieId, item);
                    }
                } else {
                    // 取消选中状态
                    if (!selectItemList.isEmpty()) {
                        deleteInfo(item.getEpisodeId());
                        selectNumView.setText(selectNum() + "");
                        if (selectNum() == 0) {
                            sendBtn.setEnabled(false);
                            sendBtn.setText(R.string.please_choose_program);
                        }
                    }
                }
            }
        });
        groupListView.setLayoutManager(new LinearLayoutManager(mContent));
        groupListView.setAdapter(groupAdapter);
        childListView.setAdapter(childAdapter);
    }

    // 开始动画
    private void startAnim(SmoothCheckBox smoothCheckBox) {
        int[] start_location = new int[2];
        smoothCheckBox.getLocationInWindow(start_location);
        selectImg = new ImageView(mContent);
        selectImg.setImageResource(R.drawable.ic_log_movement);
        setAnim(selectImg, start_location);
    }

    // 抛物线动画
    private void setAnim(final View v, int[] start_location) {
        animMaskLayout = null;
        animMaskLayout = createAnimLayout();
        animMaskLayout.addView(v); //把运动的图片添加到动画层
        final View view = addViewToAnimLayout(animMaskLayout, v, start_location);
        int[] end_location = new int[2]; //动画结束位置的X,Y坐标
        selectCar.getLocationInWindow(end_location);

        // 计算位移
        int endX = 0 - start_location[0] + end_location[0] + 25; //动画位移的X坐标
        int endY = end_location[1] - start_location[1]; //动画位移的Y坐标
        TranslateAnimation translateAnimationX = new TranslateAnimation(0,
                endX, 0, 0);
        translateAnimationX.setInterpolator(new LinearInterpolator());
        translateAnimationX.setRepeatCount(0);
        translateAnimationX.setFillAfter(true); //动画结束后不返回

        TranslateAnimation translateAnimationY = new TranslateAnimation(0, 0,
                0, endY);
        translateAnimationY.setInterpolator(new MyInterpolator());

        translateAnimationY.setRepeatCount(0);
        translateAnimationY.setFillAfter(true);

        AnimationSet set = new AnimationSet(false);
        set.setFillAfter(false);
        set.addAnimation(translateAnimationX);
        set.addAnimation(translateAnimationY);
        set.setDuration(DURATION);
        view.startAnimation(set);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
                selectNumView.setText(selectNum() + "");
                if (selectNum() >= 1) {
                    sendBtn.setEnabled(true);
                    sendBtn.setText(R.string.send);
                }
                ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 0.8f, 1.0f
                        , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation.setInterpolator(new DecelerateInterpolator());
                scaleAnimation.setDuration(350);//设置动画持续时间
                selectCar.startAnimation(scaleAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /**
     * @return ViewGroup
     * @Description: 创建动画层
     */
    private ViewGroup createAnimLayout() {
        ViewGroup rootView = (ViewGroup) getActivity().getWindow().getDecorView();
        LinearLayout animLayout = new LinearLayout(mContent);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        animLayout.setLayoutParams(lp);
        animLayout.setBackgroundResource(R.color.transparent);
        rootView.addView(animLayout);
        return animLayout;
    }

    /**
     * 设置小飞行图标的起始位置
     *
     * @param vg
     * @param view
     * @param location
     * @return
     */
    private View addViewToAnimLayout(final ViewGroup vg, final View view,
                                     int[] location) {
        int x = location[0];
        int y = location[1];
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = x;
        lp.topMargin = y;
        view.setLayoutParams(lp);
        return view;
    }


    /**
     * 1.17 获取总剧集列表
     */
    private void reqMainPlayList() {
        activity.startProgress();
        String url = AppConfig.MAIN_PLAYLIST;
        MainPlayListRequest request = new MainPlayListRequest();
        String token = ((KidsMindApplication) getActivity().getApplication()).getToken();
        request.setToken(token);
        request.setStartIndex(0);
        request.setOffset(1000);
        request.setCategory("cartoon");  //中文动画，儿歌

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                CartoonRoleResponse resp = GsonUtil.parse(response,
                        CartoonRoleResponse.class);
                if (resp != null && resp.isSucess()) {

                    List<CartoonItem> list = resp.getData().getList();
                    groupAdapter.setCheckItems(list);
                    activity.stopProgress();
                    reqPlayListInfo(list.get(0).getSerieId());
                }
            }

        });
    }

    /**
     * 1.18 获取分剧集
     *
     * @param serieId
     */
    private void reqPlayListInfo(final int serieId) {
        activity.startProgress();
        String url = AppConfig.SERIE_INFO;
        SerieInfoRequest request = new SerieInfoRequest();

        String token = ((KidsMindApplication) getActivity().getApplication()).getToken();
        request.setToken(token);
        request.setSerieId(serieId);
        request.setStartIndex(0);
        request.setOffset(1000);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                SerieInfoResponse resp = GsonUtil.parse(response,
                        SerieInfoResponse.class);
                if (resp.isSucess()) {
                    List<SerieItem> lists = resp.getData().getList();
                    childAdapter.setChildItem(lists, serieId, selectItemList);
                    activity.stopProgress();
                }
            }

        });
    }

    /**
     * 1.87 发送定制节目列表接口
     */
    private void makeEpisodeList(String list) {
        activity.startProgress();
        String url = AppConfig.EPISODE_LIST;
        MakeEpisodeListRequest request = new MakeEpisodeListRequest();
        String token = ((KidsMindApplication) getActivity().getApplication()).getToken();
        request.setToken(token);
        request.setEpisodeList(list);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                CartoonRoleResponse resp = GsonUtil.parse(response,
                        CartoonRoleResponse.class);
                if (resp != null && resp.isSucess()) {
                    activity.showTips(R.string.custom_program_listing_sucess);
                    activity.stopProgress();
                    sendBtn.setEnabled(true);
                    selectCar.setEnabled(true);
                    clearAll();
                    if (sendBtnPopu != null)
                        sendBtnPopu.setEnabled(true);
                }
            }
        });
    }

    // 用于发送成功后，清除并刷新
    private void clearAll() {
        selectItemList.clear();
        selectNumView.setText(selectNum() + "");
        childAdapter.setSelectedList(selectItemList);
    }

    /**
     * 专门用于存储选中信息的方法。
     */

    private void addInfo(int serieId, SerieItem item) {

        if (!contain(item.getEpisodeId())) {
            SelectItem selectItem = new SelectItem();
            selectItem.setSerieId(serieId);
            selectItem.setEpisodeId(item.getEpisodeId());
            selectItem.setEpisodeName(item.getTitle());
            selectItemList.add(selectItem);
        }
    }

    private boolean contain(int id) {
        for (SelectItem i : selectItemList) {
            if (id == i.getEpisodeId())
                return true;
        }
        return false;
    }

    /**
     * 用于删除选中的信息
     */
    private void deleteInfo(int id) {
        SelectItem item = new SelectItem();
        for (SelectItem i : selectItemList) {
            if (id == i.getEpisodeId()) {
                item = i;
            }
        }
        if (item != null) {
            selectItemList.remove(item);
        }

    }

    /**
     * 用于返回选中数量
     */
    private int selectNum() {
        return selectItemList.size();
    }

    /**
     * 用于返回选中的剧集值
     */
    private String selectStr() {
        String str = "";
        for (SelectItem item : selectItemList) {
            str += item.getEpisodeId() + ",";
        }
        return str;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.program_send) {
            String str = selectStr();
            if (!str.isEmpty()) {
                sendBtn.setEnabled(false);
                selectCar.setEnabled(false);
                makeEpisodeList(str);
            }
        } else if (id == R.id.program_send_popu) {
            String str = selectStr();
            if (!str.isEmpty()) {
                sendBtnPopu.setEnabled(false);
                sendBtn.setEnabled(false);
                makeEpisodeList(str);
                mpopuWindow.dismiss();
            }
        } else if (id == R.id.select_car) {
            if (selectNum() > 0) {
                showPopMenu();
            }
        }
    }

    private void showPopMenu() {
        View view = View.inflate(mContent, R.layout.select_popup_menu, null);
        selectedListView = (RecyclerView) view.findViewById(R.id.selected_recyclerview);
        sendBtnPopu = (Button) view.findViewById(R.id.program_send_popu);
        sendBtnPopu.setOnClickListener(this);
        selectedListView.setLayoutManager(new LinearLayoutManager(mContent));
        selectedListView.setItemAnimator(new DefaultItemAnimator());
        selectedAdapter.setSelectItem(selectItemList);
        selectedListView.setAdapter(selectedAdapter);
        view.startAnimation(AnimationUtils.loadAnimation(mContent, R.anim.fade_in));
        LinearLayout selected_view = (LinearLayout) view.findViewById(R.id.selected_view);
        selected_view.startAnimation(AnimationUtils.loadAnimation(mContent, R.anim.push_bottom_in));

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mpopuWindow.dismiss();
                selectNumView.setText(selectNum() + "");
                childAdapter.setSelectedList(selectItemList);
            }
        });

        if (mpopuWindow == null) {
            mpopuWindow = new PopupWindow(mContent);
            mpopuWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            mpopuWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            mpopuWindow.setOutsideTouchable(true);
        }

        mpopuWindow.setContentView(view);
        mpopuWindow.showAtLocation(selectCar, Gravity.BOTTOM, 0, 0);
        mpopuWindow.update();
    }

    private class MyInterpolator implements Interpolator {

        @Override
        public float getInterpolation(float input) {
            return input * input;
        }
    }

}
