package com.ivmall.android.app.expand;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.SerieInfoRequest;
import com.ivmall.android.app.entity.SerieInfoResponse;
import com.ivmall.android.app.entity.SerieItem;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.uitls.ListUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class ExpandableAdapter extends RecyclerView.Adapter<ExpandableAdapter.ViewHolder> {

    protected Context mContext;

    protected List<SerieListItem> visibleItems;
    private List<Integer> indexList;  //保存动态索引
    private SparseIntArray expandMap;  //保存折叠状态


    private SparseArray expandListMap;  //保存二级折叠状态的数据

    private static final int ARROW_ROTATION_DURATION = 150;


    public ExpandableAdapter(Context context) {
        mContext = context;

        indexList = new ArrayList<>();
        expandMap = new SparseIntArray();

        expandListMap = new SparseArray();
    }


    /**
     * 设置一级节目列表
     *
     * @param items
     */
    public void setHeardItems(List<SerieListItem> items) {
        visibleItems = items;
        expandMap.clear();
        indexList.clear();

        for (int i = 0; i < items.size(); i++) {
            indexList.add(i);
        }
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return visibleItems == null ? 0 : visibleItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return visibleItems.get(position).getItemType();
    }


    protected View inflate(int resourceID, ViewGroup viewGroup) {
        return LayoutInflater.from(mContext).inflate(resourceID, viewGroup, false);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }
    }

    public class HeaderViewHolder extends ViewHolder {
        ImageView arrow;

        public HeaderViewHolder(View view, final ImageView arrow) {
            super(view);

            this.arrow = arrow;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleClick();
                }
            });
        }

        protected void handleClick() {
            if (toggleExpandedItems(getLayoutPosition(), false)) {
                openArrow(arrow);
            } else {
                closeArrow(arrow);
            }
        }

        public void bind(int position) {
            arrow.setRotation(isExpanded(position) ? 90 : 0);
        }
    }

    public boolean toggleExpandedItems(int position, boolean notify) {
        if (isExpanded(position)) {
            collapseItems(position, notify);//收缩
            return false;
        } else {
            expandItems(position, notify);  //展开
            return true;
        }
    }


    /**
     * 展开
     *
     * @param position
     * @param notify
     */
    @SuppressWarnings("unchecked")
    public void expandItems(int position, boolean notify) {
        int insert = position;
        int count = 0;

        SerieListItem item = visibleItems.get(position);
        int serieId = item.getSerieId();

        List<SerieListItem> items = (List<SerieListItem>) expandListMap.get(position);

        if (ListUtils.isEmpty(items)) {
            reqPlayListInfo(position, notify, serieId);
        } else {
            for (int i = 0; i < items.size(); i++) {
                insert++;
                count++;
                visibleItems.add(insert, items.get(i));
                indexList.add(insert, i);
            }

            notifyItemRangeInserted(position + 1, count);

            int allItemsPosition = indexList.get(position);
            expandMap.put(allItemsPosition, 1);

            if (notify) {
                notifyItemChanged(position);
            }
        }

    }


    /**
     * 收缩
     *
     * @param position
     * @param notify
     */
    @SuppressWarnings("unchecked")
    public void collapseItems(int position, boolean notify) {
        int count = 0;

        List<SerieListItem> list = (List<SerieListItem>) expandListMap.get(position);

        for (int i = 0; i < list.size(); i++) {
            count++;
            visibleItems.remove(position + 1);
            indexList.remove(position + 1);
        }

        notifyItemRangeRemoved(position + 1, count);

        int allItemsPosition = indexList.get(position);
        expandMap.delete(allItemsPosition);

        if (notify) {
            notifyItemChanged(position);
        }
    }


    protected boolean isExpanded(int position) {
        int allItemsPosition = indexList.get(position);
        return expandMap.get(allItemsPosition, -1) >= 0;
    }


    public static void openArrow(View view) {
        view.animate().setDuration(ARROW_ROTATION_DURATION).rotation(90);
    }

    public static void closeArrow(View view) {
        view.animate().setDuration(ARROW_ROTATION_DURATION).rotation(0);
    }


    /**
     * 1.18 获取总剧集详细内容
     *
     * @param serieId
     */
    private void reqPlayListInfo(final int position, final boolean notify, int serieId) {


        String url = AppConfig.SERIE_INFO;
        SerieInfoRequest request = new SerieInfoRequest();

        String token = ((KidsMindApplication) mContext.getApplicationContext()).getToken();
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
                if (resp != null && resp.isSucess()) {
                    int count = 0;
                    int insert = position;

                    List<SerieItem> lists = resp.getData().getList();

                    List<SerieListItem> items = new ArrayList<>();

                    for (SerieItem item : lists) {
                        SerieListItem serieListItem = new SerieListItem(item.getTitle());
                        serieListItem.setEpisodeId(item.getEpisodeId());
                        serieListItem.setItemType(SerieListItem.TYPE_BODY);
                        items.add(serieListItem);
                    }

                    synchronized (visibleItems) {

                        expandListMap.put(position, items);

                        for (int i = 0; i < items.size(); i++) {
                            insert++;
                            count++;
                            visibleItems.add(insert, items.get(i));
                            indexList.add(insert, i);
                        }

                        notifyItemRangeInserted(position + 1, count);

                        int allItemsPosition = indexList.get(position);
                        expandMap.put(allItemsPosition, 1);

                        if (notify) {
                            notifyItemChanged(position);
                        }
                    }
                }

            }

        });
    }
}
