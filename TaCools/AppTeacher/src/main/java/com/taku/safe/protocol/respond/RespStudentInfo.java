package com.taku.safe.protocol.respond;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by colin on 2017/6/6.
 */

public class RespStudentInfo extends RespBaseBean implements Parcelable {

    private int activeSosId;
    private BaseInfoBean baseInfo;
    private RestSignInfoBean restSignInfo;
    private InternshipInfoBean internshipInfo;

    public int getActiveSosId() {
        return activeSosId;
    }

    public void setActiveSosId(int activeSosId) {
        this.activeSosId = activeSosId;
    }

    public BaseInfoBean getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(BaseInfoBean baseInfo) {
        this.baseInfo = baseInfo;
    }

    public RestSignInfoBean getRestSignInfo() {
        return restSignInfo;
    }

    public void setRestSignInfo(RestSignInfoBean restSignInfo) {
        this.restSignInfo = restSignInfo;
    }

    public InternshipInfoBean getInternshipInfo() {
        return internshipInfo;
    }

    public void setInternshipInfo(InternshipInfoBean internshipInfo) {
        this.internshipInfo = internshipInfo;
    }

    public static class BaseInfoBean implements android.os.Parcelable {

        private int studentId;
        private String studentName;
        private String studentNo;
        private String studentPhone;
        private int sexId;
        private int schoolId;
        private int collegeId;
        private int majorId;
        private int classeId;
        private int buildingId;
        private int authStatus;
        private String createTime;
        private int status;
        private int emergencyId;

        public int getStudentId() {
            return studentId;
        }

        public void setStudentId(int studentId) {
            this.studentId = studentId;
        }

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public String getStudentNo() {
            return studentNo;
        }

        public void setStudentNo(String studentNo) {
            this.studentNo = studentNo;
        }

        public String getStudentPhone() {
            return studentPhone;
        }

        public void setStudentPhone(String studentPhone) {
            this.studentPhone = studentPhone;
        }

        public int getSexId() {
            return sexId;
        }

        public void setSexId(int sexId) {
            this.sexId = sexId;
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

        public int getMajorId() {
            return majorId;
        }

        public void setMajorId(int majorId) {
            this.majorId = majorId;
        }

        public int getClasseId() {
            return classeId;
        }

        public void setClasseId(int classeId) {
            this.classeId = classeId;
        }

        public int getBuildingId() {
            return buildingId;
        }

        public void setBuildingId(int buildingId) {
            this.buildingId = buildingId;
        }

        public int getAuthStatus() {
            return authStatus;
        }

        public void setAuthStatus(int authStatus) {
            this.authStatus = authStatus;
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

        public int getEmergencyId() {
            return emergencyId;
        }

        public void setEmergencyId(int emergencyId) {
            this.emergencyId = emergencyId;
        }

        public BaseInfoBean() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.studentId);
            dest.writeString(this.studentName);
            dest.writeString(this.studentNo);
            dest.writeString(this.studentPhone);
            dest.writeInt(this.sexId);
            dest.writeInt(this.schoolId);
            dest.writeInt(this.collegeId);
            dest.writeInt(this.majorId);
            dest.writeInt(this.classeId);
            dest.writeInt(this.buildingId);
            dest.writeInt(this.authStatus);
            dest.writeString(this.createTime);
            dest.writeInt(this.status);
            dest.writeInt(this.emergencyId);
        }

        protected BaseInfoBean(Parcel in) {
            this.studentId = in.readInt();
            this.studentName = in.readString();
            this.studentNo = in.readString();
            this.studentPhone = in.readString();
            this.sexId = in.readInt();
            this.schoolId = in.readInt();
            this.collegeId = in.readInt();
            this.majorId = in.readInt();
            this.classeId = in.readInt();
            this.buildingId = in.readInt();
            this.authStatus = in.readInt();
            this.createTime = in.readString();
            this.status = in.readInt();
            this.emergencyId = in.readInt();
        }

        public static final Creator<BaseInfoBean> CREATOR = new Creator<BaseInfoBean>() {
            @Override
            public BaseInfoBean createFromParcel(Parcel source) {
                return new BaseInfoBean(source);
            }

            @Override
            public BaseInfoBean[] newArray(int size) {
                return new BaseInfoBean[size];
            }
        };
    }

    public static class RestSignInfoBean implements android.os.Parcelable {

        private int validDistanceGps;
        private int validDistanceBase;
        private int needSign;
        private int signStatus;
        private int signValid;
        private double longitude;
        private double latitude;
        private String startTime;
        private String endTime;
        private int collegeId;
        private int buildingId;

        public int getValidDistanceGps() {
            return validDistanceGps;
        }

        public void setValidDistanceGps(int validDistanceGps) {
            this.validDistanceGps = validDistanceGps;
        }

        public int getValidDistanceBase() {
            return validDistanceBase;
        }

        public void setValidDistanceBase(int validDistanceBase) {
            this.validDistanceBase = validDistanceBase;
        }

        public int getNeedSign() {
            return needSign;
        }

        public void setNeedSign(int needSign) {
            this.needSign = needSign;
        }

        public int getSignStatus() {
            return signStatus;
        }

        public void setSignStatus(int signStatus) {
            this.signStatus = signStatus;
        }

        public int getSignValid() {
            return signValid;
        }

        public void setSignValid(int signValid) {
            this.signValid = signValid;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public int getCollegeId() {
            return collegeId;
        }

        public void setCollegeId(int collegeId) {
            this.collegeId = collegeId;
        }

        public int getBuildingId() {
            return buildingId;
        }

        public void setBuildingId(int buildingId) {
            this.buildingId = buildingId;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.validDistanceGps);
            dest.writeInt(this.validDistanceBase);
            dest.writeInt(this.needSign);
            dest.writeInt(this.signStatus);
            dest.writeInt(this.signValid);
            dest.writeDouble(this.longitude);
            dest.writeDouble(this.latitude);
            dest.writeString(this.startTime);
            dest.writeString(this.endTime);
            dest.writeInt(this.collegeId);
            dest.writeInt(this.buildingId);
        }

        public RestSignInfoBean() {
        }

        protected RestSignInfoBean(Parcel in) {
            this.validDistanceGps = in.readInt();
            this.validDistanceBase = in.readInt();
            this.needSign = in.readInt();
            this.signStatus = in.readInt();
            this.signValid = in.readInt();
            this.longitude = in.readDouble();
            this.latitude = in.readDouble();
            this.startTime = in.readString();
            this.endTime = in.readString();
            this.collegeId = in.readInt();
            this.buildingId = in.readInt();
        }

        public static final Creator<RestSignInfoBean> CREATOR = new Creator<RestSignInfoBean>() {
            @Override
            public RestSignInfoBean createFromParcel(Parcel source) {
                return new RestSignInfoBean(source);
            }

            @Override
            public RestSignInfoBean[] newArray(int size) {
                return new RestSignInfoBean[size];
            }
        };
    }

    public static class InternshipInfoBean implements android.os.Parcelable {

        private int companyId;
        private String companyName;
        private int validDistanceGps;
        private int validDistanceBase;
        private double longitude;
        private double latitude;
        private String teacherName;
        private String teacherPhoneNo;
        private int needSign;
        private int signStatus;
        private int signValid;

        public int getCompanyId() {
            return companyId;
        }

        public void setCompanyId(int companyId) {
            this.companyId = companyId;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public int getValidDistanceGps() {
            return validDistanceGps;
        }

        public void setValidDistanceGps(int validDistanceGps) {
            this.validDistanceGps = validDistanceGps;
        }

        public int getValidDistanceBase() {
            return validDistanceBase;
        }

        public void setValidDistanceBase(int validDistanceBase) {
            this.validDistanceBase = validDistanceBase;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public String getTeacherName() {
            return teacherName;
        }

        public void setTeacherName(String teacherName) {
            this.teacherName = teacherName;
        }

        public String getTeacherPhoneNo() {
            return teacherPhoneNo;
        }

        public void setTeacherPhoneNo(String teacherPhoneNo) {
            this.teacherPhoneNo = teacherPhoneNo;
        }

        public int getNeedSign() {
            return needSign;
        }

        public void setNeedSign(int needSign) {
            this.needSign = needSign;
        }

        public int getSignStatus() {
            return signStatus;
        }

        public void setSignStatus(int signStatus) {
            this.signStatus = signStatus;
        }

        public int getSignValid() {
            return signValid;
        }

        public void setSignValid(int signValid) {
            this.signValid = signValid;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.companyId);
            dest.writeString(this.companyName);
            dest.writeInt(this.validDistanceGps);
            dest.writeInt(this.validDistanceBase);
            dest.writeDouble(this.longitude);
            dest.writeDouble(this.latitude);
            dest.writeString(this.teacherName);
            dest.writeString(this.teacherPhoneNo);
            dest.writeInt(this.needSign);
            dest.writeInt(this.signStatus);
            dest.writeInt(this.signValid);
        }

        public InternshipInfoBean() {
        }

        protected InternshipInfoBean(Parcel in) {
            this.companyId = in.readInt();
            this.companyName = in.readString();
            this.validDistanceGps = in.readInt();
            this.validDistanceBase = in.readInt();
            this.longitude = in.readDouble();
            this.latitude = in.readDouble();
            this.teacherName = in.readString();
            this.teacherPhoneNo = in.readString();
            this.needSign = in.readInt();
            this.signStatus = in.readInt();
            this.signValid = in.readInt();
        }

        public static final Creator<InternshipInfoBean> CREATOR = new Creator<InternshipInfoBean>() {
            @Override
            public InternshipInfoBean createFromParcel(Parcel source) {
                return new InternshipInfoBean(source);
            }

            @Override
            public InternshipInfoBean[] newArray(int size) {
                return new InternshipInfoBean[size];
            }
        };
    }

    public RespStudentInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.activeSosId);
        dest.writeParcelable(this.baseInfo, flags);
        dest.writeParcelable(this.restSignInfo, flags);
        dest.writeParcelable(this.internshipInfo, flags);
        dest.writeInt(this.code);
        dest.writeString(this.msg);
        dest.writeLong(this.ts);
    }

    protected RespStudentInfo(Parcel in) {
        this.activeSosId = in.readInt();
        this.baseInfo = in.readParcelable(BaseInfoBean.class.getClassLoader());
        this.restSignInfo = in.readParcelable(RestSignInfoBean.class.getClassLoader());
        this.internshipInfo = in.readParcelable(InternshipInfoBean.class.getClassLoader());
        this.code = in.readInt();
        this.msg = in.readString();
        this.ts = in.readLong();
    }

    public static final Creator<RespStudentInfo> CREATOR = new Creator<RespStudentInfo>() {
        @Override
        public RespStudentInfo createFromParcel(Parcel source) {
            return new RespStudentInfo(source);
        }

        @Override
        public RespStudentInfo[] newArray(int size) {
            return new RespStudentInfo[size];
        }
    };
}
