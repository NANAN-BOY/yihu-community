package com.yihu.mapper;



import com.yihu.entity.Activity;
import com.yihu.entity.File;

public interface ActivityMapper {
    int create(Activity activity);

    // 根据活动ID删除活动记录
    void deleteById(Integer id);

    void update(Activity activity);
}
