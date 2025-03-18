package com.yihu.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserQueryDTO {
    private String name;
    private String phone;
    private Integer role;
    private String location;
}
