package com.mykj.andr.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.mykj.andr.model.HallDataManager;
import com.mykj.andr.model.RoomData;
import com.mykj.andr.task.GameTask;
import com.mykj.andr.ui.widget.LoginAssociatedWidget;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.ddz.R;


public class LoadingDialog extends AlertDialog implements View.OnClickListener {

    private NodeDataType mType = NodeDataType.NODE_TYPE_NOT_DO;
    private TextView tvLoadingInfo;
    private Context mContext;


    public enum NodeDataType {
        NODE_TYPE_101, NODE_TYPE_109, NODE_TYPE_111, NODE_TYPE_NOT_DO;
    }


    public LoadingDialog(Context context) {
        super(context);
        mContext = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loading_dialog);
        initView();
    }

    private void initView() {
        tvLoadingInfo = (TextView) this.findViewById(R.id.tvLoadingInfo);
        findViewById(R.id.btnCancel).setOnClickListener(this);

    }

    public void setLoadingInfo(String loading) {
        if (tvLoadingInfo != null && isShowing()) {
            tvLoadingInfo.setText(loading);
        }
    }

    public void setLoadingType(String loading, NodeDataType type) {
        if (tvLoadingInfo != null && isShowing()) {
            tvLoadingInfo.setText(loading);
        }
        if (type != null) {
            this.mType = type;
        }
    }


    public void cancelLoading() {
        switch (mType) {
            case NODE_TYPE_101: // 普通节点(自由、约占)
                // 自由战区/智运会
                RoomData room = HallDataManager.getInstance()
                        .getCurrentRoomData(); // 获取速配成功进入房间保存的房间信息
                if (room != null) {
                    // 请求离开房间
                    GameTask.getInstance().clrearTask();
                    LoginAssociatedWidget.getInstance().exitRoom(room.RoomID);
                }
                break;
            case NODE_TYPE_109: // 报名节点
                // 发送退出登录 发送201----106
                if (FiexedViewHelper.getInstance().amusementFragment != null) {
                    FiexedViewHelper.getInstance().amusementFragment.exitLogin();
                }
                break;
            case NODE_TYPE_111: // 约战节点
                //				if (FiexedViewHelper.getInstance().challengeFragment != null) {
                //					FiexedViewHelper.getInstance().challengeFragment
                //					.leaveChallenge();
                //				}
                break;
            case NODE_TYPE_NOT_DO: // 不进行操作
                FiexedViewHelper.getInstance().skipToFragment(FiexedViewHelper.LOGON_VIEW);
                break;
            default:
                break;
        }

//        if (FiexedViewHelper.getInstance().cardZoneFragment.gameRoomAssociated != null) {
//            FiexedViewHelper.getInstance().cardZoneFragment.gameRoomAssociated.removeUserStatusListener();
//        }

    }


    @Override
    public void onClick(View v) {
        FiexedViewHelper.getInstance().playKeyClick();
        int id = v.getId();
        if (id == R.id.btnCancel) {
            cancelLoading();
            dismiss();
        }
    }


}
