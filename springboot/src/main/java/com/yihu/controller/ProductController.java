package com.yihu.controller;

import com.yihu.common.Result;
import com.yihu.dto.ProductDTO;
import com.yihu.entity.Product;
import com.yihu.entity.User;
import com.yihu.service.ProductService;
import com.yihu.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/product/add")
    public Result addProduct(@RequestBody Product product) {
        User user = TokenUtils.getCurrentUser();
        if (user == null) {
            return Result.error(401, "未授权，请登录");
        }
        if (user.getRole() != 1) {
            return Result.error(403, "无权限");
        }
        return Result.success(productService.addProduct(product, user.getId()));
    }

    @DeleteMapping("/product/delete/{id}")
    public Result deleteProduct(@PathVariable Integer id) {
        User user = TokenUtils.getCurrentUser();
        if (user == null) {
            return Result.error(401, "未授权，请登录");
        }
        if (user.getRole() != 1) {
            return Result.error(403, "无权限");
        }
        return Result.success(productService.deleteProduct(id));
    }

    @PutMapping("/product/update")
    public Result updateProduct(@RequestBody ProductDTO productDTO) {
        User user = TokenUtils.getCurrentUser();
        if (user == null) {
            return Result.error(401, "未授权，请登录");
        }
        if (user.getRole() != 1) {
            return Result.error(403, "无权限");
        }
        return Result.success(productService.updateProduct(productDTO, user.getId()));
    }

    @GetMapping("/product/get")
    public Result findProduct() {
        User user = TokenUtils.getCurrentUser();
        if (user == null) {
            return Result.error(401, "未授权，请登录");
        }
        if (user.getRole() != 1) {
            return Result.error(403, "无权限");
        }
        return Result.success(productService.findProduct());
    }


}
