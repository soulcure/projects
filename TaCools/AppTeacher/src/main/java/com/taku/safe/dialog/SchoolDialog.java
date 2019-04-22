package com.taku.safe.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.taku.safe.R;
import com.taku.safe.TakuApp;
import com.taku.safe.adapter.ChoiceAdapter;
import com.taku.safe.protocol.respond.RespTeacherInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/5/27.
 * 学校选择对话框
 */

public class SchoolDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = SchoolDialog.class.getSimpleName();

    private TakuApp mTakuApp;
    private Context mContext;

    private TextView tv_university;
    private TextView tv_college;
    private TextView tv_major;
    private TextView tv_class;

    private OnConfim onConfim;

    private int level;
    private int indexCollege;
    private int indexMarjor;
    private int indexClass;

    private String content;

    private RecyclerView recycler_view;
    private ChoiceAdapter mAdapter;


    private RespTeacherInfo teacherInfo;

    private int mPosition = 1;  //保存上一次处于checked状态的控件位置


    public interface OnConfim {
        void onConfirm(String content);
    }


    public static class Builder {
        private Context context;
        private OnConfim onConfim;
        private int level;
        private int indexCollege;
        private int indexMarjor;
        private int indexClass;

        private RespTeacherInfo teacherInfo;

        public Builder(Context context) {
            this.context = context;
        }


        public Builder teacherInfo(RespTeacherInfo teacherInfo) {
            this.teacherInfo = teacherInfo;
            return this;
        }

        public Builder onConfim(OnConfim onConfim) {
            this.onConfim = onConfim;
            return this;
        }

        public Builder level(int level) {
            this.level = level;
            return this;
        }

        public Builder indexCollege(int indexCollege) {
            this.indexCollege = indexCollege;
            return this;
        }

        public Builder indexMarjor(int indexMarjor) {
            this.indexMarjor = indexMarjor;
            return this;
        }

        public Builder indexClass(int indexClass) {
            this.indexClass = indexClass;
            return this;
        }


        public SchoolDialog builder() {
            return new SchoolDialog(this);
        }
    }

    private SchoolDialog(Builder builder) {
        super(builder.context, R.style.custom_dialog);
        mTakuApp = (TakuApp) builder.context.getApplicationContext();

        mContext = builder.context;
        teacherInfo = builder.teacherInfo;
        onConfim = builder.onConfim;
        level = builder.level;
        indexCollege = builder.indexCollege;
        indexMarjor = builder.indexMarjor;
        indexClass = builder.indexClass;

        setCanceledOnTouchOutside(true);
        setCancelable(false);
    }


    private ChoiceAdapter.CallBack mCallBack = new ChoiceAdapter.CallBack() {
        @Override
        public void selectItem(int postion, int index, String name, boolean isNext) {
            switch (postion) {
                case 1:
                    if (name.equals("全部")) {
                        mTakuApp.setLevel(1);
                    } else {
                        mTakuApp.setLevel(2);
                        mTakuApp.setIndexCollege(index - 1);
                    }
                    tv_university.setEnabled(true);
                    tv_college.setEnabled(false);

                    content = name;
                    if (index > 0 && isNext) {
                        tv_college.setText(name);
                        tv_college.setVisibility(View.VISIBLE);
                        choiceMajor(index - 1);
                    }
                    break;
                case 2:
                    if (name.equals("全部")) {
                        mTakuApp.setLevel(2);
                    } else {
                        mTakuApp.setLevel(3);
                        mTakuApp.setIndexMarjor(index - 1);
                    }

                    tv_university.setEnabled(true);
                    tv_college.setEnabled(true);
                    tv_major.setEnabled(false);

                    content = name;
                    if (index > 0 && isNext) {
                        tv_major.setText(name);
                        tv_major.setVisibility(View.VISIBLE);
                        choiceClass(mTakuApp.getIndexCollege(), index - 1);
                    }
                    break;
                case 3:
                    if (name.equals("全部")) {
                        mTakuApp.setLevel(3);
                    }
                    tv_university.setEnabled(true);
                    tv_college.setEnabled(true);
                    tv_major.setEnabled(true);
                    tv_class.setEnabled(false);

                    content = name;
                    if (index > 0) {
                        mTakuApp.setLevel(4);
                        mTakuApp.setIndexClass(index - 1);
                    }
                    break;
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_school);
        setDialogFeature();
        initView();
        setListener();
        initLevel();
    }


    /**
     * 设置对话框特征
     */
    private void setDialogFeature() {
        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(wlp);
        }
    }


    private void initView() {
        tv_university = (TextView) findViewById(R.id.tv_university);
        tv_college = (TextView) findViewById(R.id.tv_college);
        tv_major = (TextView) findViewById(R.id.tv_major);
        tv_class = (TextView) findViewById(R.id.tv_class);

        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new ChoiceAdapter(getContext(), mCallBack);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setAdapter(mAdapter);

        tv_university.setText(teacherInfo.getSchool().getSchoolName());
    }


    private void initLevel() {
        if (level == 1) {
            tv_college.setVisibility(View.VISIBLE);

            choiceCollege();

        } else if (level == 2) {
            tv_college.setVisibility(View.VISIBLE);
            tv_major.setVisibility(View.VISIBLE);

            //tv_university.setEnabled(false);
            //tv_college.setEnabled(false);

            int indexCollege = mTakuApp.getIndexCollege();

            String collegeName = mTakuApp.getTeacherInfo().getSchool()
                    .getCollegeList().get(indexCollege).getCollegeName();
            tv_college.setText(collegeName);

            choiceMajor(indexCollege);

        } else if (level == 3) {
            tv_college.setVisibility(View.VISIBLE);
            tv_major.setVisibility(View.VISIBLE);
            tv_class.setVisibility(View.VISIBLE);

            //tv_university.setEnabled(false);
            //tv_college.setEnabled(false);
            //tv_major.setEnabled(false);

            int indexCollege = mTakuApp.getIndexCollege();
            int indexMarjor = mTakuApp.getIndexMarjor();

            String collegeName = mTakuApp.getTeacherInfo().getSchool()
                    .getCollegeList().get(indexCollege).getCollegeName();
            tv_college.setText(collegeName);


            String majorName = mTakuApp.getTeacherInfo().getSchool()
                    .getCollegeList().get(indexCollege).getMajorList().get(indexMarjor).getMajorName();
            tv_major.setText(majorName);

            choiceClass(indexCollege, indexMarjor);

        } else if (level == 4) {
            tv_college.setVisibility(View.VISIBLE);
            tv_major.setVisibility(View.VISIBLE);
            tv_class.setVisibility(View.VISIBLE);

            //tv_university.setEnabled(false);
            //tv_college.setEnabled(false);
            //tv_major.setEnabled(false);
            //tv_class.setEnabled(false);
            int indexCollege = mTakuApp.getIndexCollege();
            int indexMarjor = mTakuApp.getIndexMarjor();
            int indexClass = mTakuApp.getIndexClass();


            String collegeName = mTakuApp.getTeacherInfo().getSchool()
                    .getCollegeList().get(indexCollege).getCollegeName();
            tv_college.setText(collegeName);

            String majorName = mTakuApp.getTeacherInfo().getSchool()
                    .getCollegeList().get(indexCollege).getMajorList().get(indexMarjor).getMajorName();
            tv_major.setText(majorName);

            String className = mTakuApp.getTeacherInfo().getSchool()
                    .getCollegeList().get(indexCollege).getMajorList().get(indexMarjor)
                    .getClassList().get(indexClass).getClasseName();
            tv_class.setText(className);

        }
    }

    private void setListener() {
        tv_university.setOnClickListener(this);
        tv_college.setOnClickListener(this);
        tv_major.setOnClickListener(this);
        tv_class.setOnClickListener(this);

        findViewById(R.id.tv_cancel).setOnClickListener(this);
        findViewById(R.id.tv_confirm).setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_university:
                mTakuApp.setLevel(1);

                mTakuApp.setIndexCollege(0);
                mTakuApp.setIndexMarjor(0);
                mTakuApp.setIndexClass(0);
                tv_major.setVisibility(View.INVISIBLE);
                tv_class.setVisibility(View.INVISIBLE);

                tv_college.setText("选择学院");
                choiceCollege();

                break;
            case R.id.tv_college:
                mTakuApp.setLevel(2);

                mTakuApp.setIndexCollege(0);
                mTakuApp.setIndexMarjor(0);
                mTakuApp.setIndexClass(0);
                tv_major.setVisibility(View.INVISIBLE);
                tv_class.setVisibility(View.INVISIBLE);

                tv_college.setText("选择学院");
                choiceCollege();

                break;
            case R.id.tv_major:
                mTakuApp.setLevel(3);

                mTakuApp.setIndexMarjor(0);
                mTakuApp.setIndexClass(0);
                tv_class.setVisibility(View.INVISIBLE);

                tv_major.setText("选择专业");
                choiceMajor(0);

                break;
            case R.id.tv_class:
                mTakuApp.setLevel(4);

                mTakuApp.setIndexClass(0);
                tv_class.setVisibility(View.INVISIBLE);

                choiceClass(0, 0);

                break;
            case R.id.tv_cancel:
                //mTakuApp.setLevel(level);
                mTakuApp.revertDeptId(level, indexCollege, indexMarjor, indexClass);
                Log.v(TAG, "level:" + mTakuApp.getLevel() + " and deptId:" + mTakuApp.getDeptId());
                dismiss();
                break;
            case R.id.tv_confirm:
                if (teacherInfo.getRole() != 1) {
                    if (mPosition < 3) {
                        Toast.makeText(mContext, "请选择到具体的班级!", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if (mTakuApp.getLevel() < 4) {
                            Toast.makeText(mContext, "请选择到具体的班级!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                } else {
                    if (mTakuApp.getPrivilegeLevel() > mTakuApp.getLevel()) {
                        Toast.makeText(mContext, "您无权限查询此栏目数据!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (onConfim != null) {
                    if (content == null || content.equals("全部")) {
                        onConfim.onConfirm(mTakuApp.getDeptName());
                    } else {
                        onConfim.onConfirm(content);
                    }
                }

                mTakuApp.saveIndex();

                Log.v(TAG, "level:" + mTakuApp.getLevel() + " and deptId:" + mTakuApp.getDeptId());
                dismiss();
                break;
        }
    }


    private void choiceCollege() {
        mPosition = 1;
        mAdapter.setPosition(mPosition);
        List<RespTeacherInfo.SchoolBean.CollegeListBean> list = teacherInfo.getSchool().getCollegeList();

        ArrayList<String> collegeList = new ArrayList<>();
        collegeList.add("全部");
        for (RespTeacherInfo.SchoolBean.CollegeListBean item : list) {
            collegeList.add(item.getCollegeName());
        }

        mAdapter.setList(collegeList);
    }


    private void choiceMajor(int index) {
        mPosition = 2;
        mAdapter.setPosition(mPosition);

        List<RespTeacherInfo.SchoolBean.CollegeListBean.MajorListBean> list = teacherInfo
                .getSchool().getCollegeList().get(index).getMajorList();

        ArrayList<String> majorList = new ArrayList<>();
        majorList.add("全部");
        for (RespTeacherInfo.SchoolBean.CollegeListBean.MajorListBean item : list) {
            majorList.add(item.getMajorName());
        }

        mAdapter.setList(majorList);
    }

    private void choiceClass(int indexCollege, int index) {
        mPosition = 3;
        mAdapter.setPosition(mPosition);

        List<RespTeacherInfo.SchoolBean.CollegeListBean.MajorListBean.ClassListBean> list = teacherInfo
                .getSchool().getCollegeList().get(indexCollege)
                .getMajorList().get(index).getClassList();

        ArrayList<String> classList = new ArrayList<>();
        classList.add("全部");
        for (RespTeacherInfo.SchoolBean.CollegeListBean.MajorListBean.ClassListBean item : list) {
            classList.add(item.getClasseName());
        }

        mAdapter.setList(classList);
    }


}
