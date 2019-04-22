package com.mykj.andr.ui.tabactivity;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mykj.andr.model.ChatMsgEntity;
import com.mykj.andr.ui.adapter.ChatMsgAdapter;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.ddz.R;
import com.mykj.game.ddz.api.AnalyticsUtils;
import com.mykj.game.utils.AppConfig;
import com.mykj.game.utils.CenterUrlHelper;
import com.mykj.game.utils.Log;
import com.mykj.game.utils.Util;
import com.mykj.game.utils.UtilHelper;

/**
 * *
 * 服务中心
 */
public class ServerCenterFragment extends Fragment implements OnClickListener {
    private static final String TAG = "ServerCenterFragment";
    private Activity mAct;
    private ListView mListView;
    private ChatMsgAdapter mAdapter;
    private EditText etQuestion;
    private TextView tvServerTel, tvQQGroup, tvWeixin;
    
    private List<ChatMsgEntity> itemInfos;        // 接收来自web的数据


    public static String SERVER_PHONE = "400-777-9996";
    public static String SERVER_QQ = "233721138";
    public static String SERVER_WX = "mykjgame";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mAct = activity;
//        try {
//            mListener = (OnRankArticleSelectedListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnRankArticleSelectedListener");
//        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.server_center_tab, container, false);
        initView(rootView);

        return rootView;
    }


    private void initView(View view) {
        mListView = (ListView) view.findViewById(R.id.server_list);
        etQuestion = (EditText) view.findViewById(R.id.server_edit);
        tvServerTel = (TextView) view.findViewById(R.id.server_tel);
        tvServerTel.setOnClickListener(this);
        tvQQGroup = (TextView) view.findViewById(R.id.server_qqgroup);
        tvWeixin = (TextView) view.findViewById(R.id.server_weixin);


        view.findViewById(R.id.server_getscreen).setOnClickListener(this);
        view.findViewById(R.id.server_send).setOnClickListener(this);


        String url = getUrl(null);
        new ServerFAQAsyncTask().execute(url);

        mAdapter = new ChatMsgAdapter(mAct);
        mListView.setAdapter(mAdapter);

    }

    @Override
    public void onClick(View v) {
    	FiexedViewHelper.getInstance().playKeyClick();
        switch (v.getId()) {
            case R.id.server_getscreen:

                break;

            case R.id.server_send:
                String content = etQuestion.getText().toString();
                if (!Util.isEmptyStr(content)) {
                    String url = getUrl(content);
                    new ServerFAQAsyncTask().execute(url);
                }
                //点击帮助
                AnalyticsUtils.onClickEvent(mAct, "093");
                break;
                	
            case R.id.server_tel:
            	Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" + SERVER_PHONE));
            	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            	startActivity(intent);
            default:
                break;
        }
    }

    private void onBottom() {
        mListView.setSelection(mListView.getBottom());
    }


    /**
     * @param content content is null 表示获取客户反馈历史数据
     *                content 有数据 表示发送客户反馈数据
     * @return
     */
    private String getUrl(String content) {
        StringBuffer sb = new StringBuffer();
        if (Util.isEmptyStr(content)) {
            sb.append("apiname=").append("ChatOnline");
        } else {
            try {
                content = URLEncoder.encode(content, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                content = "";
            }
            sb.append("apiname=").append("InsertChatOnline");
            sb.append('&').append("content=").append(content);
        }

        sb.append('&').append("uid=").append(FiexedViewHelper.getInstance().getUserId());
        sb.append('&').append("gameid=").append(AppConfig.gameId);
        sb.append('&').append("op=").append(121);
        sb.append('&').append("format=").append("xml");
        String param = sb.toString();


        String sign = CenterUrlHelper.getSign(param, CenterUrlHelper.secret);

        String url = AppConfig.SERVER_ONLINE_PATH + "?" + param + sign;
        return url;
    }


    private class ServerFAQAsyncTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            int result = 1;
            String url = params[0];
            String res = null;
            try {
                res = Util.getConfigXmlByHttp(url);
                //res = Util.readFromFile("/mnt/sdcard/1.txt"); //for test
                //res = Util.readFromFile("/mnt/sdcard/2.txt"); //for test
                // 定义工厂
                XmlPullParserFactory f = XmlPullParserFactory.newInstance();
                // 定义解析器
                XmlPullParser p = f.newPullParser();
                // 获取xml输入数据
                //p.setInput(new InputStreamReader(conn.getInputStream()));
                if (Util.isEmptyStr(res)) {
                    throw new NullPointerException("http访问异常!");
                }
                p.setInput(new StringReader(res));
                ChatMsgEntity itemInfo = null;
                // 解析事件
                int eventType = p.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            itemInfos = new ArrayList<ChatMsgEntity>();
                            break;
                        case XmlPullParser.START_TAG:
                            String tagName = p.getName();
                            if (tagName.equals("status")) {
                                String status = p.nextText();
                                if (!status.equals("0")) {
                                    return -1;
                                }
                            } else if (tagName.equals("statusnote")) {
                                String statusNote = p.nextText();
                            } else if (tagName.equals("data")) {
                                result = 2;
                            } else if (tagName.equals("element")) {
                                itemInfo = new ChatMsgEntity();
                            }


                            if (itemInfo != null) {
                                if (tagName.equals("id")) {
                                    itemInfo.setId(p.nextText());
                                } else if (tagName.equals("uid")) {
                                    itemInfo.setUid(p.nextText());
                                } else if (tagName.equals("game_id")) {
                                    itemInfo.setGameId(p.nextText());
                                } else if (tagName.equals("content")) {
                                    itemInfo.setContent(p.nextText());
                                } else if (tagName.equals("f_time")) {
                                    itemInfo.setReferTime(p.nextText());
                                } else if (tagName.equals("op_name")) {
                                    itemInfo.setOperator(p.nextText());
                                } else if (tagName.equals("memo")) {
                                    itemInfo.setReply(p.nextText());
                                } else if (tagName.equals("op_time")) {
                                    itemInfo.setOperateTime(p.nextText());
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if (p.getName().equals("element")) {
                                itemInfos.add(itemInfo);
                                itemInfo = null;
                            }
                            break;
                        default:
                            break;
                    }
                    // 用next方法处理下一个事件，否则会造成死循环。
                    eventType = p.next();
                }

            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                return -1;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return -1;
            } catch (UnknownHostException e) {
                e.printStackTrace();
                return -1;
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                return -1;
            } catch (NullPointerException e) {
                e.printStackTrace();
                return -1;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == -1) {
                // Toast.makeText(mAct, "对不起,网络出现了异常", Toast.LENGTH_SHORT).show();
            } else if (result == 1) {
                String content = etQuestion.getText().toString();
                if (!Util.isEmptyStr(content)) {
                    Toast.makeText(mAct, "发送留言信息成功", Toast.LENGTH_SHORT).show();

                    ChatMsgEntity itemInfo = new ChatMsgEntity();  //add local 
                    itemInfo.setContent(content);
                    String time=Util.getCurrentTime();
                    itemInfo.setReferTime(time);

                    mAdapter.addItem(itemInfo);
                    mListView.setSelection(mListView.getCount() - 1);
                    etQuestion.setText("");
                }
            } else if (result == 2) {
                if (!itemInfos.isEmpty()) {
                    mAdapter.setList(itemInfos);
                    mListView.setSelection(mListView.getCount() - 1);
                }
            } else {
                Log.e(TAG, "ServerFAQAsyncTask is error");
            }
        }

    }


    /**
     * 获取在线客服url
     *
     * @param context
     */
    public static void reqOnlineServerUrl(Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append(AppConfig.onlineServer).append('?');
        sb.append("cid=").append(AppConfig.channelId).append('&');
        sb.append("scid=").append(AppConfig.childChannelId).append('&');
        sb.append("gameid=").append(AppConfig.gameId).append('&');
        sb.append("version=").append(Util.getVersionName(context));
        String url = sb.toString();
        String content = Util.getConfigXmlByHttp(url);


        SERVER_PHONE = UtilHelper.parseStatusXml(content, "tel");
        SERVER_QQ = UtilHelper.parseStatusXml(content, "qq");
        SERVER_WX = UtilHelper.parseStatusXml(content, "wx");
    }


}
