package com.yihu.service;

import com.yihu.dto.ProductDTO;
import com.yihu.entity.Product;

import java.util.List;

public interface ProductService {
    Boolean addProduct(Product product, Integer userId);

    Boolean deleteProduct(Integer id);

    List<Product> findProduct();

    Boolean updateProduct(ProductDTO productDTO, Integer userId);

    List<Product> findProductList(Integer type);
}
