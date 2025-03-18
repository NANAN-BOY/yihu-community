package com.yihu.mapper;

import com.yihu.entity.File;

public interface FileMapper {
    int insert(File newFile);

    int updateQuoteByid(File existingFile);

    File findByMd5(String md5);

    File selectById(Integer id);
}
