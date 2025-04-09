package com.yihu.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ActivityCreateDTO {
    private Integer id;
    private Integer status;
    private String title;
    private String noticeContent;
    private Integer staffCount;
    private Integer volunteerCount;
    private Integer serviceObjectCount;
    private List<NewsItem> news; // 包含 news 数据

    @Data
    public static class NewsItem {
        private String platform;
        private String link;
    }
}
