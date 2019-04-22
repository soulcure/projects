package com.taku.safe.protocol.respond;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by colin on 2017/8/10.
 */

public class RespTeacherInfo extends RespBaseBean implements Parcelable {

    private int role;
    private int privilegeLevel;
    private String name;
    private String teacherNo;
    private String phoneNo;
    private SchoolBean school;


    public int getDeptId(int level, int indexCollege, int indexMarjor, int indexClass) {
        int res = 0;
        try {
            if (role == 1) {
                switch (level) {
                    case 1:
                        res = school.getSchoolId();
                        break;
                    case 2:
                        res = school.getCollegeList().get(indexCollege).getCollegeId();
                        break;
                    case 3:
                        res = school.getCollegeList().get(indexCollege).getMajorList().
                                get(indexMarjor).getMajorId();
                        break;
                    case 4:
                        res = school.getCollegeList().get(indexCollege).getMajorList().
                                get(indexMarjor).getClassList().get(indexClass).getClasseId();
                        break;
                }
            } else {
                res = school.getCollegeList().get(indexCollege).getMajorList().
                        get(indexMarjor).getClassList().get(indexClass).getClasseId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    public String getDeptName(int level, int indexCollege, int indexMarjor, int indexClass) {
        String res = null;
        try {
            if (role == 1) {
                switch (level) {
                    case 1:
                        res = school.getSchoolName();
                        break;
                    case 2:
                        res = school.getCollegeList().get(indexCollege).getCollegeName();
                        break;
                    case 3:
                        res = school.getCollegeList().get(indexCollege).getMajorList().
                                get(indexMarjor).getMajorName();
                        break;
                    case 4:
                        res = school.getCollegeList().get(indexCollege).getMajorList().
                                get(indexMarjor).getClassList().get(indexClass).getClasseName();
                        break;
                }
            } else {
                res = school.getCollegeList().get(indexCollege).getMajorList().
                        get(indexMarjor).getClassList().get(indexClass).getClasseName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public List<String> parseSpinnerData(int indexCollege, int indexMarjor, int indexClass) {
        List<String> list = new ArrayList<>();

        if (privilegeLevel == 1) {
            List<SchoolBean.CollegeListBean> collegeList = school.getCollegeList();
            for (SchoolBean.CollegeListBean item : collegeList) {
                list.add(item.getCollegeName());
            }
        } else if (privilegeLevel == 2) {
            List<SchoolBean.CollegeListBean.MajorListBean> majorList = school.getCollegeList()
                    .get(indexCollege).getMajorList();
            for (SchoolBean.CollegeListBean.MajorListBean item : majorList) {
                list.add(item.getMajorName());
            }
        } else if (privilegeLevel == 3) {
            List<SchoolBean.CollegeListBean.MajorListBean.ClassListBean> classList = school.getCollegeList()
                    .get(indexCollege).getMajorList().get(indexMarjor).getClassList();
            for (SchoolBean.CollegeListBean.MajorListBean.ClassListBean item : classList) {
                list.add(item.getClasseName());
            }
        } else if (privilegeLevel == 4) {
            //do nothing
        }
        return list;
    }


    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getPrivilegeLevel() {
        return privilegeLevel;
    }

    public void setPrivilegeLevel(int privilegeLevel) {
        this.privilegeLevel = privilegeLevel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeacherNo() {
        return teacherNo;
    }

    public void setTeacherNo(String teacherNo) {
        this.teacherNo = teacherNo;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }


    public SchoolBean getSchool() {
        return school;
    }

    public void setSchool(SchoolBean school) {
        this.school = school;
    }

    public static class SchoolBean implements Parcelable {

        private int schoolId;
        private String schoolName;
        private List<CollegeListBean> collegeList;

        public int getSchoolId() {
            return schoolId;
        }

        public void setSchoolId(int schoolId) {
            this.schoolId = schoolId;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public List<CollegeListBean> getCollegeList() {
            return collegeList;
        }

        public void setCollegeList(List<CollegeListBean> collegeList) {
            this.collegeList = collegeList;
        }

        public static class CollegeListBean implements Parcelable {

            private int collegeId;
            private String collegeName;
            private List<MajorListBean> majorList;

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

            public List<MajorListBean> getMajorList() {
                return majorList;
            }

            public void setMajorList(List<MajorListBean> majorList) {
                this.majorList = majorList;
            }

            public static class MajorListBean implements Parcelable {

                private int majorId;
                private String majorName;
                private List<ClassListBean> classList;

                public int getMajorId() {
                    return majorId;
                }

                public void setMajorId(int majorId) {
                    this.majorId = majorId;
                }

                public String getMajorName() {
                    return majorName;
                }

                public void setMajorName(String majorName) {
                    this.majorName = majorName;
                }

                public List<ClassListBean> getClassList() {
                    return classList;
                }

                public void setClassList(List<ClassListBean> classList) {
                    this.classList = classList;
                }

                public static class ClassListBean implements Parcelable {

                    private int classeId;
                    private String classeName;

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

                    @Override
                    public int describeContents() {
                        return 0;
                    }

                    @Override
                    public void writeToParcel(Parcel dest, int flags) {
                        dest.writeInt(this.classeId);
                        dest.writeString(this.classeName);
                    }

                    public ClassListBean() {
                    }

                    protected ClassListBean(Parcel in) {
                        this.classeId = in.readInt();
                        this.classeName = in.readString();
                    }

                    public static final Creator<ClassListBean> CREATOR = new Creator<ClassListBean>() {
                        @Override
                        public ClassListBean createFromParcel(Parcel source) {
                            return new ClassListBean(source);
                        }

                        @Override
                        public ClassListBean[] newArray(int size) {
                            return new ClassListBean[size];
                        }
                    };
                }

                @Override
                public int describeContents() {
                    return 0;
                }

                @Override
                public void writeToParcel(Parcel dest, int flags) {
                    dest.writeInt(this.majorId);
                    dest.writeString(this.majorName);
                    dest.writeList(this.classList);
                }

                public MajorListBean() {
                }

                protected MajorListBean(Parcel in) {
                    this.majorId = in.readInt();
                    this.majorName = in.readString();
                    this.classList = new ArrayList<>();
                    in.readList(this.classList, ClassListBean.class.getClassLoader());
                }

                public static final Creator<MajorListBean> CREATOR = new Creator<MajorListBean>() {
                    @Override
                    public MajorListBean createFromParcel(Parcel source) {
                        return new MajorListBean(source);
                    }

                    @Override
                    public MajorListBean[] newArray(int size) {
                        return new MajorListBean[size];
                    }
                };
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.collegeId);
                dest.writeString(this.collegeName);
                dest.writeList(this.majorList);
            }

            public CollegeListBean() {
            }

            protected CollegeListBean(Parcel in) {
                this.collegeId = in.readInt();
                this.collegeName = in.readString();
                this.majorList = new ArrayList<>();
                in.readList(this.majorList, MajorListBean.class.getClassLoader());
            }

            public static final Creator<CollegeListBean> CREATOR = new Creator<CollegeListBean>() {
                @Override
                public CollegeListBean createFromParcel(Parcel source) {
                    return new CollegeListBean(source);
                }

                @Override
                public CollegeListBean[] newArray(int size) {
                    return new CollegeListBean[size];
                }
            };
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.schoolId);
            dest.writeString(this.schoolName);
            dest.writeList(this.collegeList);
        }

        public SchoolBean() {
        }

        protected SchoolBean(Parcel in) {
            this.schoolId = in.readInt();
            this.schoolName = in.readString();
            this.collegeList = new ArrayList<>();
            in.readList(this.collegeList, CollegeListBean.class.getClassLoader());
        }

        public static final Creator<SchoolBean> CREATOR = new Creator<SchoolBean>() {
            @Override
            public SchoolBean createFromParcel(Parcel source) {
                return new SchoolBean(source);
            }

            @Override
            public SchoolBean[] newArray(int size) {
                return new SchoolBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.msg);
        dest.writeInt(this.code);
        dest.writeInt(this.role);
        dest.writeInt(this.privilegeLevel);
        dest.writeString(this.name);
        dest.writeString(this.teacherNo);
        dest.writeString(this.phoneNo);
        dest.writeLong(this.ts);
        dest.writeParcelable(this.school, flags);
    }

    public RespTeacherInfo() {
    }

    protected RespTeacherInfo(Parcel in) {
        this.msg = in.readString();
        this.code = in.readInt();
        this.role = in.readInt();
        this.privilegeLevel = in.readInt();
        this.name = in.readString();
        this.teacherNo = in.readString();
        this.phoneNo = in.readString();
        this.ts = in.readLong();
        this.school = in.readParcelable(SchoolBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<RespTeacherInfo> CREATOR = new Parcelable.Creator<RespTeacherInfo>() {
        @Override
        public RespTeacherInfo createFromParcel(Parcel source) {
            return new RespTeacherInfo(source);
        }

        @Override
        public RespTeacherInfo[] newArray(int size) {
            return new RespTeacherInfo[size];
        }
    };
}
