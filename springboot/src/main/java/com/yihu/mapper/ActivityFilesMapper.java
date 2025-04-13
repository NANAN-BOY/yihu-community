package com.yihu.mapper;


import com.yihu.entity.ActivityFiles;

import java.util.List;

public interface ActivityFilesMapper {
    int insert(ActivityFiles activityFiles);

    // 根据活动ID删除文件记录
    void deleteByActivityId(Integer activityId);

    ActivityFiles findById(Integer id);

    void deleteById(Integer id);

    List<ActivityFiles> findByActivityId(Integer activityId);
}
