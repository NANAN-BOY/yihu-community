package com.yihu.mapper;


import com.yihu.entity.ActivityFiles;

public interface ActivityFilesMapper {
    int insert(ActivityFiles activityFiles);

    // 根据活动ID删除文件记录
    void deleteByActivityId(Integer activityId);

}
