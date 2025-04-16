package com.yihu.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {
    private Integer id;
    private String name;
    private Float price;
    private Float discount;
    private Float proportion;
    private Integer vipTime;
}
