package com.yihu.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class Product {
    private Integer id;
    private String name;
    private Integer type;
    private Float price;
    private Float discount;
    private Float proportion;
    private Integer createUser;
    private Integer updateUser;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date updateAt;

    public Product() {
    }

    public Product(Integer id, String name, Integer type,
                   Float price, Float discount, Float proportion,
                   Integer createUser, Integer updateUser, Date createAt, Date updateAt) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.type = type;
        this.discount = discount;
        this.proportion = proportion;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public Product(Integer id, String name, Float price, Float discount, Float proportion, Integer updateUser, Date updateAt) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.discount = discount;
        this.proportion = proportion;
        this.updateUser = updateUser;
        this.updateAt = updateAt;
    }

    public Product(Integer id, String name, Integer type, Float price, Float discount, Float proportion) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.discount = discount;
        this.proportion = proportion;
    }
}
