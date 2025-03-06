package com.yihu.entity;

import java.util.Date;

public class User {
    private Integer id;//用户id（自增）
    private String name;//用户名
    private String phone;//用户手机号
    private String description;//用户描述(可为空)
    private String password;//用户密码
    private Integer role;//身份
    private Integer status;//用户状态
    private float balance;//余额
    private Date createAt;//创建日期
    private Date lastLoginTime;//最后登录时间
    private Integer updaterId;//更新者id
    private Date updateAt;//更新时间
    private Integer deleteFlag;//删除标志
    private Date deleteAt;//删除时间（可为空）
    private Integer deleteId;//删除者id（可为空）
    private String location;//用户所在位置


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Integer getUpdaterId() {
        return updaterId;
    }

    public void setUpdaterId(Integer updaterId) {
        this.updaterId = updaterId;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public Integer getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public Date getDeleteAt() {
        return deleteAt;
    }

    public void setDeleteAt(Date deleteAt) {
        this.deleteAt = deleteAt;
    }

    public Integer getDeleteId() {
        return deleteId;
    }

    public void setDeleteId(Integer deleteId) {
        this.deleteId = deleteId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public User(String name, String phone, String password, String location, Integer role,
                Integer status, float balance, Date createAt, Date lastLoginTime,
                Integer updaterId, Date updateAt, Integer deleteFlag) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.status = status;
        this.balance = balance;
        this.createAt = createAt;
        this.lastLoginTime = lastLoginTime;
        this.updaterId = updaterId;
        this.updateAt = updateAt;
        this.deleteFlag = deleteFlag;
        this.location = location;
    }

    public User(Integer id, String name, String phone, String description, String password, Integer role, String location,
                Integer status, float balance, Date createAt, Date lastLoginTime, Integer updaterId, Date updateAt,
                Integer deleteFlag, Date deleteAt, Integer deleteId) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.description = description;
        this.password = password;
        this.role = role;
        this.status = status;
        this.balance = balance;
        this.createAt = createAt;
        this.lastLoginTime = lastLoginTime;
        this.updaterId = updaterId;
        this.updateAt = updateAt;
        this.deleteFlag = deleteFlag;
        this.deleteAt = deleteAt;
        this.deleteId = deleteId;
        this.location = location;
    }
}