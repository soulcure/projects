package com.taku.safe.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taku.safe.BasePermissionActivity;
import com.taku.safe.R;
import com.taku.safe.TakuApp;
import com.taku.safe.protocol.respond.RespSchoolList;
import com.taku.safe.protocol.respond.RespStudentInfo;
import com.taku.safe.protocol.respond.RespTeacherInfo;
import com.umeng.analytics.MobclickAgent;

import java.util.List;


/**
 * Created by colin on 2017/6/8.
 */

public class MySchoolActivity extends BasePermissionActivity {


    private LinearLayout linear_college;
    private LinearLayout linear_major;
    private LinearLayout linear_class;

    private TextView tv_name;
    private TextView tv_identity_type;
    private TextView tv_code;

    private TextView tv_school;
    private TextView tv_college;
    private TextView tv_major;
    private TextView tv_class;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_school);
        initView();
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("我的学校");
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_identity_type = (TextView) findViewById(R.id.tv_identity_type);
        tv_code = (TextView) findViewById(R.id.tv_code);

        linear_college = (LinearLayout) findViewById(R.id.linear_college);
        linear_major = (LinearLayout) findViewById(R.id.linear_major);
        linear_class = (LinearLayout) findViewById(R.id.linear_class);

        tv_school = (TextView) findViewById(R.id.tv_school);
        tv_college = (TextView) findViewById(R.id.tv_college);
        tv_major = (TextView) findViewById(R.id.tv_major);
        tv_class = (TextView) findViewById(R.id.tv_class);

        initTeacherData();

    }


    /**
     * 设置老师数据
     */
    private void initTeacherData() {
        RespTeacherInfo teacherInfo = getIntent().getParcelableExtra(MapActivity.SIGN_INFO);
        if (teacherInfo != null) {
            String name = teacherInfo.getName();
            tv_name.setText(name);

            tv_identity_type.setText("教师编号");
            tv_code.setText(teacherInfo.getTeacherNo());

            switch (teacherInfo.getPrivilegeLevel()) {
                case 1: {
                    String schoolName = teacherInfo.getSchool().getSchoolName();
                    tv_school.setText(schoolName);

                    linear_college.setVisibility(View.GONE);
                    linear_major.setVisibility(View.GONE);
                    linear_class.setVisibility(View.GONE);

                }
                break;
                case 2: {
                    String schoolName = teacherInfo.getSchool().getSchoolName();
                    String collegeName = teacherInfo.getSchool().getCollegeList()
                            .get(0).getCollegeName();

                    tv_school.setText(schoolName);
                    tv_college.setText(collegeName);

                    linear_major.setVisibility(View.GONE);
                    linear_class.setVisibility(View.GONE);
                }
                break;
                case 3: {
                    String schoolName = teacherInfo.getSchool().getSchoolName();
                    String collegeName = teacherInfo.getSchool().getCollegeList()
                            .get(0).getCollegeName();
                    String majorName = teacherInfo.getSchool().getCollegeList()
                            .get(0).getMajorList().get(0).getMajorName();

                    tv_school.setText(schoolName);
                    tv_college.setText(collegeName);
                    tv_major.setText(majorName);

                    linear_major.setVisibility(View.GONE);  //需求要求不显示专业
                    linear_class.setVisibility(View.GONE);
                }
                break;
                case 4: {
                    String schoolName = teacherInfo.getSchool().getSchoolName();
                    String collegeName = teacherInfo.getSchool().getCollegeList()
                            .get(0).getCollegeName();
                    String majorName = teacherInfo.getSchool().getCollegeList()
                            .get(0).getMajorList().get(0).getMajorName();
                    String className = teacherInfo.getSchool().getCollegeList()
                            .get(0).getMajorList().get(0).getClassList().get(0).getClasseName();

                    tv_school.setText(schoolName);
                    tv_college.setText(collegeName);
                    tv_major.setText(majorName);
                    tv_class.setText(className);

                    linear_major.setVisibility(View.GONE);  //需求要求不显示专业
                    linear_class.setVisibility(View.GONE);  //需求要求不显班级
                }
                break;
            }


        }
    }


    /**
     * 设置学生数据
     */
    private void initStudentData() {
        RespStudentInfo signInfo = getIntent().getParcelableExtra(MapActivity.SIGN_INFO);
        if (signInfo != null && signInfo.getBaseInfo() != null) {
            tv_name.setText(signInfo.getBaseInfo().getStudentName());
            tv_identity_type.setText("学号");
            tv_code.setText(signInfo.getBaseInfo().getStudentNo());

            int schoolId = signInfo.getBaseInfo().getSchoolId();
            int collegeId = signInfo.getBaseInfo().getCollegeId();
            int majorId = signInfo.getBaseInfo().getMajorId();
            int classId = signInfo.getBaseInfo().getClasseId();
            schoolList(schoolId, collegeId, majorId, classId);

            //linear_major.setVisibility(View.GONE);  //需求要求不显示专业
            //linear_class.setVisibility(View.GONE);  //需求要求不显班级

        }
    }

    /**
     * 获取学校列表
     */
    private void schoolList(final int schoolId, final int collegeId, final int majorId, final int classId) {
        mTakuApp.reqSchoolList(new TakuApp.SchoolInfo() {
            @Override
            public void success(RespSchoolList list) {
                if (!isFinishing()) {
                    if (list != null && list.isSuccess()) {
                        StudentInfo studentInfo = parseStudentInfo(list, schoolId, collegeId, majorId, classId);

                        tv_school.setText(studentInfo.schoolName);
                        tv_college.setText(studentInfo.collegeName);
                        tv_major.setText(studentInfo.majorName);
                        tv_class.setText(studentInfo.className);

                    }
                }
            }
        });
    }


    /**
     * 获取拥有查看权限的某个班级
     *
     * @param list
     * @param schoolId
     * @param collegeId
     * @param majorId
     * @param classId
     * @return
     */
    public StudentInfo parseStudentInfo(RespSchoolList list, int schoolId, int collegeId, int majorId, int classId) {
        StudentInfo studentInfo = new StudentInfo();
        List<RespSchoolList.SchoolListBean> schoolList = list.getSchoolList();
        for (RespSchoolList.SchoolListBean itemSchool : schoolList) {
            if (schoolId == itemSchool.getSchoolId()) {
                studentInfo.schoolName = itemSchool.getSchoolName();
                List<RespSchoolList.SchoolListBean.CollegeListBean> collegeList = itemSchool.getCollegeList();
                for (RespSchoolList.SchoolListBean.CollegeListBean itemCollege : collegeList) {
                    if (collegeId == itemCollege.getCollegeId()) {
                        studentInfo.collegeName = itemCollege.getCollegeName();
                        List<RespSchoolList.SchoolListBean.CollegeListBean.MajorListBean> majorList = itemCollege.getMajorList();
                        for (RespSchoolList.SchoolListBean.CollegeListBean.MajorListBean majorItem : majorList) {
                            if (majorId == majorItem.getMajorId()) {
                                studentInfo.majorName = majorItem.getMajorName();
                                List<RespSchoolList.SchoolListBean.CollegeListBean.MajorListBean.ClassListBean> classList = majorItem.getClassList();
                                for (RespSchoolList.SchoolListBean.CollegeListBean.MajorListBean.ClassListBean classItem : classList) {
                                    if (classId == classItem.getClasseId()) {
                                        studentInfo.className = classItem.getClasseName();
                                        return studentInfo;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return studentInfo;
    }


    private class StudentInfo {
        String schoolName;
        String collegeName;
        String majorName;
        String className;
    }

}
