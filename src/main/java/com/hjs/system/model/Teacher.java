package com.hjs.system.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;

public class Teacher implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer tid;

    private String teacherId;

    private String realName;

    private String password;

    private Boolean sex;

    private Date createTime;

    private String mobile;

    private String email;

    private String picimg;

    private String remark;

    private String githubName;

    public Integer getTid() {
        return tid;
    }

    public void setTid(Integer tid) {
        this.tid = tid;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicimg() {
        return picimg;
    }

    public void setPicimg(String picimg) {
        this.picimg = picimg;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getGithubName() {
        return githubName;
    }

    public void setGithubName(String githubName) {
        this.githubName = githubName;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "tid=" + tid +
                ", teacherId='" + teacherId + '\'' +
                ", realName='" + realName + '\'' +
                ", password='" + password + '\'' +
                ", sex=" + sex +
                ", createTime=" + createTime +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", picimg='" + picimg + '\'' +
                ", remark='" + remark + '\'' +
                ", githubName='" + githubName + '\'' +
                '}';
    }
}