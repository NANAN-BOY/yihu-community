package com.yihu.mapper;



import com.yihu.entity.Activity;
import com.yihu.entity.Project;

import java.util.List;

public interface ProjectMapper {
    int create(Project project);

    // 根据活动ID删除活动记录
    void deleteById(Project project);

    void update(Project project);

    List<Project> queryByCreateId(Project project);

    List<Project> queryAllSubmited(Project project);

    Project findById(Integer id);

    void updateStatus(Project project);


}
