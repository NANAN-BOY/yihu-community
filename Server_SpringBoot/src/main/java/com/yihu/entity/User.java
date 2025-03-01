package com.yihu.entity;

import java.sql.Date;

public class User {
    private int id;//用户id

    private String user_name;//用户名

    private String user_phoneNumber;//用户手机号

    private String user_description;//用户描述，可为空

    private String user_hashPassword;//用户密码

    private int user_role;//用户角色

    private int user_accountStatus;//用户账号状态

    private float user_balance;//用户余额

    private int Inviter_id;//邀请人id，可为空

    private Date create_at;//创建时间

    private int updater_id;//更新者id，可为空

    private Date update_at;//更新时间，可为空

    private int delete_flag;//删除标志

    private int deleter_id;//删除人id，可为空

    private Date delete_at;//删除时间，可为空

    public User() {
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_phoneNumber() {
        return user_phoneNumber;
    }

    public void setUser_phoneNumber(String user_phoneNumber) {
        this.user_phoneNumber = user_phoneNumber;
    }

    public String getUser_description() {
        return user_description;
    }

    public void setUser_description(String user_description) {
        this.user_description = user_description;
    }

    public String getUser_hashPassword() {
        return user_hashPassword;
    }

    public void setUser_hashPassword(String user_hashPassword) {
        this.user_hashPassword = user_hashPassword;
    }

    public int getUser_role() {
        return user_role;
    }

    public void setUser_role(int user_role) {
        this.user_role = user_role;
    }

    public int getUser_accountStatus() {
        return user_accountStatus;
    }

    public void setUser_accountStatus(int user_accountStatus) {
        this.user_accountStatus = user_accountStatus;
    }

    public float getUser_balance() {
        return user_balance;
    }

    public void setUser_balance(float user_balance) {
        this.user_balance = user_balance;
    }

    public int getInviter_id() {
        return Inviter_id;
    }

    public void setInviter_id(int inviter_id) {
        Inviter_id = inviter_id;
    }

    public Date getCreate_at() {
        return create_at;
    }

    public void setCreate_at(Date create_at) {
        this.create_at = create_at;
    }

    public int getUpdater_id() {
        return updater_id;
    }

    public void setUpdater_id(int updater_id) {
        this.updater_id = updater_id;
    }

    public Date getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(Date update_at) {
        this.update_at = update_at;
    }

    public int getDelete_flag() {
        return delete_flag;
    }

    public void setDelete_flag(int delete_flag) {
        this.delete_flag = delete_flag;
    }

    public int getDeleter_id() {
        return deleter_id;
    }

    public void setDeleter_id(int deleter_id) {
        this.deleter_id = deleter_id;
    }

    public Date getDelete_at() {
        return delete_at;
    }

    public void setDelete_at(Date delete_at) {
        this.delete_at = delete_at;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", user_name='" + user_name + '\'' +
                ", user_phoneNumber='" + user_phoneNumber + '\'' +
                ", user_description='" + user_description + '\'' +
                ", user_hashPassword='" + user_hashPassword + '\'' +
                ", user_role=" + user_role +
                ", user_accountStatus=" + user_accountStatus +
                ", user_balance=" + user_balance +
                ", Inviter_id=" + Inviter_id +
                ", create_at=" + create_at +
                ", updater_id=" + updater_id +
                ", update_at=" + update_at +
                ", delete_flag=" + delete_flag +
                ", deleter_id=" + deleter_id +
                ", delete_at=" + delete_at +
                '}';
    }
}
