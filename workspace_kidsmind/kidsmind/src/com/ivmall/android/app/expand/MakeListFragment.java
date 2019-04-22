package com.ivmall.android.app.expand;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.R;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.CartoonItem;
import com.ivmall.android.app.entity.CartoonRoleResponse;
import com.ivmall.android.app.entity.MainPlayListRequest;
import com.ivmall.android.app.entity.MakeEpisodeListRequest;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class MakeListFragment extends Fragment {
    private RecyclerView recycler;
    private SerieListAdapter adapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.content_main, container, false);

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


        recycler = (RecyclerView) view.findViewById(R.id.main_recycler);

        adapter = new SerieListAdapter(getActivity());
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setAdapter(adapter);

        reqMainPlayList();
    }


    public void sentEpisodeList() {
        String strList = "";
        List<SerieListItem> lists = adapter.getCheckItems();
        for (SerieListItem item : lists) {
            if (item.isChecked()) {
                strList += item.getEpisodeId() + ",";
            }
        }
        makeEpisodeList(strList);
    }

    /**
     * 1.17 获取首页剧集列表
     */
    private void reqMainPlayList() {
        String url = AppConfig.MAIN_PLAYLIST_V2;
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
                    List<SerieListItem> items = new ArrayList<>();
                    for (CartoonItem item : list) {
                        SerieListItem serieListItem = new SerieListItem(item.getTitle());
                        serieListItem.setSerieId(item.getSerieId());
                        serieListItem.setItemType(SerieListItem.TYPE_HEADER);
                        items.add(serieListItem);
                    }
                    adapter.setHeardItems(items);

                }
            }

        });
    }

    /**
     * 1.87 发送定制节目列表接口
     */
    private void makeEpisodeList(String list) {
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
                    Snackbar snack = Snackbar.make(recycler, R.string.custom_program_listing_sucess, Snackbar.LENGTH_LONG);
                    View view = snack.getView();
                    TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(getResources().getColor(R.color.primary));
                    tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    snack.show();
                }
            }

        });
    }

}
