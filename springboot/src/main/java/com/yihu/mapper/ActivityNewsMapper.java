package com.yihu.mapper;


import com.yihu.entity.ActivityNews;

public interface ActivityNewsMapper {
    int insert(ActivityNews activityNews);

    // 根据活动ID删除新闻记录
    void deleteByActivityId(Integer activityId);
}
