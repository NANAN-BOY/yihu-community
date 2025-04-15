package com.yihu.mapper;

import com.yihu.entity.Product;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface ProductMapper {

    Boolean addProduct(Product product);

    Boolean deleteProduct(@Param("id") Integer id);

    List<Product> findProduct();

    Boolean updateProduct(Product product);

    List<Product> findProductList(@Param(("type")) Integer type);
}
