package com.taku.safe.protocol.respond;

import java.util.List;

/**
 * Created by colin on 2017/8/15.
 */

public class RespClassList extends RespBaseBean {

    private List<ClassListBean> classList;

    public List<ClassListBean> getClassList() {
        return classList;
    }

    public void setClassList(List<ClassListBean> classList) {
        this.classList = classList;
    }

    public static class ClassListBean {
        /**
         * classId : 1
         * className : 美院一班
         * unusualNum : 4
         * studentList : [{"studentName":"琼瑶","unusualNum":4},{"studentName":"周勇","unusualNum":3},{"studentName":"李春风","unusualNum":1},{"studentName":"陈小牛","unusualNum":1}]
         */

        private int classId;
        private String className;
        private int unusualNum;
        private List<StudentListBean> studentList;

        public int getClassId() {
            return classId;
        }

        public void setClassId(int classId) {
            this.classId = classId;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public int getUnusualNum() {
            return unusualNum;
        }

        public void setUnusualNum(int unusualNum) {
            this.unusualNum = unusualNum;
        }

        public List<StudentListBean> getStudentList() {
            return studentList;
        }

        public void setStudentList(List<StudentListBean> studentList) {
            this.studentList = studentList;
        }

        public static class StudentListBean {
            /**
             * studentName : 琼瑶
             * unusualNum : 4
             */

            private String studentName;
            private int unusualNum;

            public String getStudentName() {
                return studentName;
            }

            public void setStudentName(String studentName) {
                this.studentName = studentName;
            }

            public int getUnusualNum() {
                return unusualNum;
            }

            public void setUnusualNum(int unusualNum) {
                this.unusualNum = unusualNum;
            }
        }
    }
}
