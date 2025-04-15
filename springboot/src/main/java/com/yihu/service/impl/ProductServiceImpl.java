package com.yihu.service.impl;

import com.yihu.dto.ProductDTO;
import com.yihu.entity.Product;
import com.yihu.mapper.ProductMapper;
import com.yihu.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {


    private final ProductMapper productMapper;

    @Autowired
    public ProductServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public Boolean addProduct(Product product, Integer userId) {
        product.setCreateUser(userId);
        product.setCreateAt(new Date());
        return productMapper.addProduct(product);
    }

    @Override
    public Boolean deleteProduct(Integer id) {
        return productMapper.deleteProduct(id);
    }

    @Override
    public List<Product> findProduct() {
        return productMapper.findProduct();
    }

    @Override
    public Boolean updateProduct(ProductDTO productDTO, Integer userId) {
        Product product = new Product(productDTO.getId(),
                productDTO.getName(),
                productDTO.getPrice(),
                productDTO.getDiscount(),
                productDTO.getProportion(),
                userId,
                new Date());
        return productMapper.updateProduct(product);
    }
}
