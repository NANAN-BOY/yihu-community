package com.yihu.mapper;



import com.github.pagehelper.PageInfo;
import com.yihu.dto.ActivityAuditDTO;
import com.yihu.dto.ActivityDTO;
import com.yihu.entity.Activity;
import com.yihu.entity.File;

import java.util.List;

public interface ActivityMapper {
    int create(Activity activity);

    // 根据活动ID删除活动记录
    void deleteById(Activity activity);

    void update(Activity activity);

    List<Activity> queryByProjectId(Activity activity);

    List<Activity> queryAllSubmited(Activity activity);

    void submit(Activity activity);

    Activity findById(Integer id);

    void updateStatus(Activity activity);


    List<Activity> findByProjectId(Integer projectId);

    List<? extends ActivityAuditDTO> getActivityAuditList(ActivityAuditDTO activityAuditDTO);

    Activity getActivityByQuestionnaireId(Integer questionnaireId);
}
