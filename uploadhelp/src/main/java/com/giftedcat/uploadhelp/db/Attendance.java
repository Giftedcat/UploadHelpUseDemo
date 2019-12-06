package com.giftedcat.uploadhelp.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;

@Entity
public class Attendance {

    @Id(autoincrement = true)
    public Long id;

    /**
     * 是否已上传
     * */
    public Boolean isUpload;

    /**
     * 文件路径
     * */
    public String path;

    /**
     * 姓名
     * */
    private String name;

    /**
     * 考勤时间
     * */
    private Date attendanceDate;

    @Generated(hash = 1560272539)
    public Attendance(Long id, Boolean isUpload, String path, String name,
            Date attendanceDate) {
        this.id = id;
        this.isUpload = isUpload;
        this.path = path;
        this.name = name;
        this.attendanceDate = attendanceDate;
    }

    @Generated(hash = 812698609)
    public Attendance() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getAttendanceDate() {
        return this.attendanceDate;
    }

    public void setAttendanceDate(Date attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsUpload() {
        return this.isUpload;
    }

    public void setIsUpload(Boolean isUpload) {
        this.isUpload = isUpload;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
