package com.yihu.mapper;


import com.yihu.entity.ActivityNews;

import java.util.List;

public interface ActivityNewsMapper {
    int insert(ActivityNews activityNews);

    // 根据活动ID删除新闻记录
    void deleteByActivityId(Integer activityId);

    List<ActivityNews> findByActivityId(Integer activityId);

    void physicalDeleteByActivityId(Integer activityId);
}
