package com.yihu.dto;

import com.yihu.entity.Activity;
import com.yihu.entity.ActivityFiles;
import com.yihu.entity.ActivityNews;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ActivityQueryDTO {
    private Activity activity; // 活动基本信息
    private List<ActivityFiles> files; // 活动相关文件列表
    private List<ActivityNews> news; // 活动相关新闻列表
}
