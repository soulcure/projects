package com.mykj.andr.ui.tabactivity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;





import com.mykj.andr.model.HallDataManager;
import com.mykj.andr.model.UserInfo;
import com.mykj.game.ddz.R;
import com.mykj.game.utils.AppConfig;


/**
 * *
 * 服务中心-帮助网页
 */
public class ServerHelpFragment extends Fragment {
    private Activity mAct;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mAct = activity;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.server_help_tab, container, false);
        initView(rootView);

        return rootView;
    }


    private void initView(View view) {
        WebView webViewHelp = (WebView) view.findViewById(R.id.webViewHelp);
        webViewHelp.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webViewHelp.loadUrl(getUrl());

    }

    private String getUrl() {
//        int userId = FiexedViewHelper.getInstance().getUserId();
//        String finalUri = CenterUrlHelper.getUrl(CenterUrlHelper.getWapUrl(20029), userId);
//        String finalUri = "http://qpwap3.139game.com/help/index.php?uid=99304415&cid=42002ANDROID1003&channelid=8420&gameid=100&ts=635552048077438241&verifystring=a26e8cf4232b23141e6acec0f4f8efed";
//        String finalUri = AppConfig.SERVER_HELP + "?" +"uid=99304415&cid=42002ANDROID1003&channelid=8420&gameid=100&ts=635552048077438241&verifystring=a26e8cf4232b23141e6acec0f4f8efed";
    	StringBuilder sb = new StringBuilder();
    	sb.append(AppConfig.SERVER_HELP).append("?");
    	sb.append("uid=").append(HallDataManager.getInstance().getUserMe().userID);
    	sb.append("&cid=").append("42002ANDROID1003");
    	sb.append("&channelid=").append(8420);
    	sb.append("&gameid=").append(AppConfig.gameId);
    	sb.append("&ts=").append(System.currentTimeMillis());
    	sb.append("&verifystring=").append("a26e8cf4232b23141e6acec0f4f8efed");
        return sb.toString();
    }


}
