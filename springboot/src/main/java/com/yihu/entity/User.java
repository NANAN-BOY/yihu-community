package com.yihu.entity;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Setter
@Getter
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

    public User() {

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

    public User(Integer id, String name, String description, String location, Timestamp currentTimestamp) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.updateAt = currentTimestamp;
    }
}