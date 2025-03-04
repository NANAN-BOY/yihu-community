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
    private Date create_at;//创建日期
    private Date last_login_time;//最后登录时间
    private Integer updater_id;//更新者id
    private Date update_at;//更新时间
    private Integer delete_flag;//删除标志
    private Date delete_at;//删除时间（可为空）
    private Integer delete_id;//删除者id（可为空）
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

    public Date getCreate_at() {
        return create_at;
    }

    public void setCreate_at(Date create_at) {
        this.create_at = create_at;
    }

    public Date getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(Date last_login_time) {
        this.last_login_time = last_login_time;
    }

    public Integer getUpdater_id() {
        return updater_id;
    }

    public void setUpdater_id(Integer updater_id) {
        this.updater_id = updater_id;
    }

    public Date getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(Date update_at) {
        this.update_at = update_at;
    }

    public Integer getDelete_flag() {
        return delete_flag;
    }

    public void setDelete_flag(Integer delete_flag) {
        this.delete_flag = delete_flag;
    }

    public Date getDelete_at() {
        return delete_at;
    }

    public void setDelete_at(Date delete_at) {
        this.delete_at = delete_at;
    }

    public Integer getDelete_id() {
        return delete_id;
    }

    public void setDelete_id(Integer delete_id) {
        this.delete_id = delete_id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public User(String name, String phone,String password, String location, Integer role,
                Integer status, float balance, Date create_at, Date last_login_time,
                Integer updater_id, Date update_at, Integer delete_flag) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.status = status;
        this.balance = balance;
        this.create_at = create_at;
        this.last_login_time = last_login_time;
        this.updater_id = updater_id;
        this.update_at = update_at;
        this.delete_flag = delete_flag;
        this.location = location;
    }
}