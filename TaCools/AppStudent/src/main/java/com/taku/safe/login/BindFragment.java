package com.taku.safe.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.taku.safe.AccountInfoActivity;
import com.taku.safe.BasePermissionFragment;
import com.taku.safe.R;
import com.taku.safe.TakuApp;
import com.taku.safe.config.AppConfig;
import com.taku.safe.dialog.ChoiceDialog;
import com.taku.safe.entity.ChoiceDialogItem;
import com.taku.safe.http.IPostListener;
import com.taku.safe.http.OkHttpConnector;
import com.taku.safe.protocol.respond.RespBaseBean;
import com.taku.safe.protocol.respond.RespSchoolList;
import com.taku.safe.utils.DeviceUtils;
import com.taku.safe.utils.GsonUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class BindFragment extends BasePermissionFragment implements View.OnClickListener {

    public final static String TAG = BindFragment.class.getSimpleName();

    private static final int HANDLER_COUNT_SECOND = 0;

    private UIHandler mHandler;

    private EditText et_name;
    private EditText et_serial;

    private TextView tv_school;
    private TextView tv_college;
    private TextView tv_field;
    private TextView tv_class;

    private RespSchoolList mRespSchoolList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new UIHandler(this);
        schoolList();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bind, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initTitle(view);
        initView(view);
    }


    private void initTitle(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("");

        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText(R.string.bind);

        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTakuApp.setToken("");
                mTakuApp.setPhoneNum("");
                mTakuApp.setBind(false);
                mTakuApp.setExpire(0);
                mTakuApp.setTs(0);
                ((AccountInfoActivity) getActivity()).skipToFragment(LoginFragment.TAG, null);
            }
        });
    }


    private void initView(View view) {
        et_name = (EditText) view.findViewById(R.id.et_name);
        et_serial = (EditText) view.findViewById(R.id.et_serial);

        tv_school = (TextView) view.findViewById(R.id.tv_school);
        tv_school.setOnClickListener(this);

        tv_college = (TextView) view.findViewById(R.id.tv_college);
        tv_college.setOnClickListener(this);

        tv_field = (TextView) view.findViewById(R.id.tv_field);
        tv_field.setOnClickListener(this);

        tv_class = (TextView) view.findViewById(R.id.tv_class);
        tv_class.setOnClickListener(this);

        view.findViewById(R.id.btn_commit).setOnClickListener(this);
    }


    private void showChoiceDialog(final TextView view, final List<ChoiceDialogItem> list) {

        new ChoiceDialog.Builder(getContext())
                .setList(list)
                .callBack(new ChoiceDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        ChoiceDialogItem item = list.get(position);
                        view.setText(item.getTitle());
                        view.setTag(R.id.key_position, position);
                        view.setTag(R.id.key_id, item.getId());
                    }
                })
                .builder()
                .show();
    }


    private void bind() {
        String url = AppConfig.STUD_BIND;

        String name = et_name.getText().toString();
        String studentNo = et_serial.getText().toString();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "请填写姓名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(studentNo)) {
            Toast.makeText(getContext(), "请填写学号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tv_school.getTag(R.id.key_id) == null) {
            Toast.makeText(getContext(), "请选择学校", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tv_college.getTag(R.id.key_id) == null) {
            Toast.makeText(getContext(), "请选择学院", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tv_field.getTag(R.id.key_id) == null) {
            Toast.makeText(getContext(), "请选择专业", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tv_class.getTag(R.id.key_id) == null) {
            Toast.makeText(getContext(), "请选择班级", Toast.LENGTH_SHORT).show();
            return;
        }

        int schoolId = (int) tv_school.getTag(R.id.key_id);
        int collegeId = (int) tv_college.getTag(R.id.key_id);
        int majorId = (int) tv_field.getTag(R.id.key_id);
        int classId = (int) tv_class.getTag(R.id.key_id);

        String phoneMac = DeviceUtils.getMacAddress(getContext());

        ContentValues header = new ContentValues();
        header.put("token", mTakuApp.getToken());

        ContentValues params = new ContentValues();
        params.put("name", name);
        params.put("studentNo", studentNo);
        params.put("schoolId", schoolId);
        params.put("collegeId", collegeId);
        params.put("majorId", majorId);
        params.put("classId", classId);
        params.put("phoneMac", phoneMac);

        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("正在绑定，请稍后...");
        dialog.show();
        OkHttpConnector.httpPost(header, url, params, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                if (isAdded()) { //网络回调需刷新UI,添加此判断
                    RespBaseBean baseBean = GsonUtil.parse(response, RespBaseBean.class);
                    if (baseBean != null && baseBean.isSuccess()) {
                        mTakuApp.setBind(true);
                        mTakuApp.reqUserInfo(null);
                        Toast.makeText(getContext(), "绑定成功", Toast.LENGTH_SHORT).show();

                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                    } else {
                        if (baseBean != null && baseBean.getMsg() != null) {
                            Toast.makeText(getContext(), baseBean.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    dialog.dismiss();
                }

            }
        });
    }


    /**
     * 获取学校列表
     */
    private void schoolList() {
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("正在请求绑定数据，请稍后...");
        dialog.show();
        mTakuApp.reqSchoolList(new TakuApp.SchoolInfo() {
            @Override
            public void success(RespSchoolList list) {
                if (list != null && list.isSuccess()) {
                    mRespSchoolList = list;
                }
                dialog.dismiss();
            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_school: {
                final ArrayList<ChoiceDialogItem> list = new ArrayList<>();
                for (RespSchoolList.SchoolListBean item : mRespSchoolList.getSchoolList()) {
                    list.add(new ChoiceDialogItem(item.getSchoolName(), item.getSchoolId()));
                }
                showChoiceDialog(tv_school, list);
            }
            break;
            case R.id.tv_college: {
                if (selectToast(1)) {

                    final ArrayList<ChoiceDialogItem> list = new ArrayList<>();
                    int index1 = (int) tv_school.getTag(R.id.key_position);

                    List<RespSchoolList.SchoolListBean.CollegeListBean> collegeList =
                            mRespSchoolList.getSchoolList().get(index1).getCollegeList();

                    for (RespSchoolList.SchoolListBean.CollegeListBean item : collegeList) {
                        list.add(new ChoiceDialogItem(item.getCollegeName(), item.getCollegeId()));
                    }
                    showChoiceDialog(tv_college, list);
                }

            }
            break;
            case R.id.tv_field: {
                if (selectToast(2)) {
                    int index1 = (int) tv_school.getTag(R.id.key_position);
                    int index2 = (int) tv_college.getTag(R.id.key_position);

                    List<RespSchoolList.SchoolListBean.CollegeListBean.MajorListBean> majorList =
                            mRespSchoolList.getSchoolList().get(index1).getCollegeList().get(index2).getMajorList();

                    final ArrayList<ChoiceDialogItem> list = new ArrayList<>();

                    for (RespSchoolList.SchoolListBean.CollegeListBean.MajorListBean item : majorList) {
                        list.add(new ChoiceDialogItem(item.getMajorName(), item.getMajorId()));
                    }
                    showChoiceDialog(tv_field, list);
                }
            }
            break;
            case R.id.tv_class: {
                if (selectToast(3)) {
                    int index1 = (int) tv_school.getTag(R.id.key_position);
                    int index2 = (int) tv_college.getTag(R.id.key_position);
                    int index3 = (int) tv_field.getTag(R.id.key_position);

                    List<RespSchoolList.SchoolListBean.CollegeListBean.MajorListBean.ClassListBean> classList =
                            mRespSchoolList.getSchoolList().get(index1).getCollegeList().
                                    get(index2).getMajorList().get(index3).getClassList();
                    final ArrayList<ChoiceDialogItem> list = new ArrayList<>();

                    for (RespSchoolList.SchoolListBean.CollegeListBean.MajorListBean.ClassListBean item : classList) {
                        list.add(new ChoiceDialogItem(item.getClasseName(), item.getClasseId()));
                    }
                    showChoiceDialog(tv_class, list);
                }
            }
            break;
            case R.id.btn_commit:
                bind();
                break;
        }

    }


    private boolean selectToast(int positon) {
        Object school = tv_school.getTag(R.id.key_position);
        Object college = tv_college.getTag(R.id.key_position);
        Object field = tv_field.getTag(R.id.key_position);

        if (school == null) {
            Toast.makeText(getContext(), "请先选择学校！", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (positon == 1) {
            return true;
        }

        if (college == null) {
            Toast.makeText(getContext(), "请先选择学校！", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (positon == 2) {
            return true;
        }

        if (field == null) {
            Toast.makeText(getContext(), "请先选择专业！", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    /**
     * service handler
     */
    public static class UIHandler extends Handler {
        private final WeakReference<BindFragment> mTarget;

        UIHandler(BindFragment target) {
            mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            BindFragment fragment = mTarget.get();
            switch (msg.what) {
                case HANDLER_COUNT_SECOND:

                    break;
                default:
                    break;
            }
        }
    }


}
