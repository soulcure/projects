package com.taku.safe.protocol.respond;

import java.util.List;

/**
 * Created by colin on 2017/7/10.
 */

public class RespSchoolList extends RespBaseBean {

    private List<SchoolListBean> schoolList;

    public List<SchoolListBean> getSchoolList() {
        return schoolList;
    }

    public void setSchoolList(List<SchoolListBean> schoolList) {
        this.schoolList = schoolList;
    }

    public static class SchoolListBean {

        private int schoolId;
        private String schoolNo;
        private String schoolName;
        private int provinceId;
        private String provinceName;
        private int cityId;
        private String cityName;
        private String createTime;
        private String address;
        private int status;
        private List<CollegeListBean> collegeList;

        public int getSchoolId() {
            return schoolId;
        }

        public void setSchoolId(int schoolId) {
            this.schoolId = schoolId;
        }

        public String getSchoolNo() {
            return schoolNo;
        }

        public void setSchoolNo(String schoolNo) {
            this.schoolNo = schoolNo;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public int getProvinceId() {
            return provinceId;
        }

        public void setProvinceId(int provinceId) {
            this.provinceId = provinceId;
        }

        public String getProvinceName() {
            return provinceName;
        }

        public void setProvinceName(String provinceName) {
            this.provinceName = provinceName;
        }

        public int getCityId() {
            return cityId;
        }

        public void setCityId(int cityId) {
            this.cityId = cityId;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public List<CollegeListBean> getCollegeList() {
            return collegeList;
        }

        public void setCollegeList(List<CollegeListBean> collegeList) {
            this.collegeList = collegeList;
        }

        public static class CollegeListBean {

            private int collegeId;
            private String collegeName;
            private String collegeNo;
            private int schoolId;
            private String createTime;
            private int status;
            private Object school;
            private List<MajorListBean> majorList;


            public MajorListBean findMajorList(int index) {
                if (majorList != null) {
                    return majorList.get(index);
                } else {
                    return null;
                }
            }


            public int getCollegeId() {
                return collegeId;
            }

            public void setCollegeId(int collegeId) {
                this.collegeId = collegeId;
            }

            public String getCollegeName() {
                return collegeName;
            }

            public void setCollegeName(String collegeName) {
                this.collegeName = collegeName;
            }

            public String getCollegeNo() {
                return collegeNo;
            }

            public void setCollegeNo(String collegeNo) {
                this.collegeNo = collegeNo;
            }

            public int getSchoolId() {
                return schoolId;
            }

            public void setSchoolId(int schoolId) {
                this.schoolId = schoolId;
            }

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public Object getSchool() {
                return school;
            }

            public void setSchool(Object school) {
                this.school = school;
            }

            public List<MajorListBean> getMajorList() {
                return majorList;
            }

            public void setMajorList(List<MajorListBean> majorList) {
                this.majorList = majorList;
            }

            public static class MajorListBean {
                /**
                 * majorId : 1
                 * majorNo : ZY000001
                 * majorName : 专业1
                 * schoolId : 1
                 * collegeId : 2
                 * createTime : 2017-06-03 18:31:23
                 * status : 0
                 * classList : [{"classeId":1,"classeName":"美院一班","classeNo":"ZY000001","majorId":null,"collegeId":2,"schoolId":1,"teacherId":1,"createTime":"2017-06-03 18:31:23","status":0}]
                 */

                private int majorId;
                private String majorNo;
                private String majorName;
                private int schoolId;
                private int collegeId;
                private String createTime;
                private int status;
                private List<ClassListBean> classList;

                public int getMajorId() {
                    return majorId;
                }

                public void setMajorId(int majorId) {
                    this.majorId = majorId;
                }

                public String getMajorNo() {
                    return majorNo;
                }

                public void setMajorNo(String majorNo) {
                    this.majorNo = majorNo;
                }

                public String getMajorName() {
                    return majorName;
                }

                public void setMajorName(String majorName) {
                    this.majorName = majorName;
                }

                public int getSchoolId() {
                    return schoolId;
                }

                public void setSchoolId(int schoolId) {
                    this.schoolId = schoolId;
                }

                public int getCollegeId() {
                    return collegeId;
                }

                public void setCollegeId(int collegeId) {
                    this.collegeId = collegeId;
                }

                public String getCreateTime() {
                    return createTime;
                }

                public void setCreateTime(String createTime) {
                    this.createTime = createTime;
                }

                public int getStatus() {
                    return status;
                }

                public void setStatus(int status) {
                    this.status = status;
                }

                public List<ClassListBean> getClassList() {
                    return classList;
                }

                public void setClassList(List<ClassListBean> classList) {
                    this.classList = classList;
                }

                public static class ClassListBean {
                    /**
                     * classeId : 1
                     * classeName : 美院一班
                     * classeNo : ZY000001
                     * majorId : null
                     * collegeId : 2
                     * schoolId : 1
                     * teacherId : 1
                     * createTime : 2017-06-03 18:31:23
                     * status : 0
                     */

                    private int classeId;
                    private String classeName;
                    private String classeNo;
                    private Object majorId;
                    private int collegeId;
                    private int schoolId;
                    private int teacherId;
                    private String createTime;
                    private int status;

                    public int getClasseId() {
                        return classeId;
                    }

                    public void setClasseId(int classeId) {
                        this.classeId = classeId;
                    }

                    public String getClasseName() {
                        return classeName;
                    }

                    public void setClasseName(String classeName) {
                        this.classeName = classeName;
                    }

                    public String getClasseNo() {
                        return classeNo;
                    }

                    public void setClasseNo(String classeNo) {
                        this.classeNo = classeNo;
                    }

                    public Object getMajorId() {
                        return majorId;
                    }

                    public void setMajorId(Object majorId) {
                        this.majorId = majorId;
                    }

                    public int getCollegeId() {
                        return collegeId;
                    }

                    public void setCollegeId(int collegeId) {
                        this.collegeId = collegeId;
                    }

                    public int getSchoolId() {
                        return schoolId;
                    }

                    public void setSchoolId(int schoolId) {
                        this.schoolId = schoolId;
                    }

                    public int getTeacherId() {
                        return teacherId;
                    }

                    public void setTeacherId(int teacherId) {
                        this.teacherId = teacherId;
                    }

                    public String getCreateTime() {
                        return createTime;
                    }

                    public void setCreateTime(String createTime) {
                        this.createTime = createTime;
                    }

                    public int getStatus() {
                        return status;
                    }

                    public void setStatus(int status) {
                        this.status = status;
                    }
                }
            }
        }
    }
}
