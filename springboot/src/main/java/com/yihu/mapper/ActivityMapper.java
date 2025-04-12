package com.yihu.mapper;



import com.github.pagehelper.PageInfo;
import com.yihu.dto.ActivityDTO;
import com.yihu.entity.Activity;
import com.yihu.entity.File;

import java.util.List;

public interface ActivityMapper {
    int create(Activity activity);

    // 根据活动ID删除活动记录
    void deleteById(Integer id);

    void update(Activity activity);

    List<Activity> queryByCreateId(Integer createId, ActivityDTO activityDTO);

    void submit(Activity activity);
}
