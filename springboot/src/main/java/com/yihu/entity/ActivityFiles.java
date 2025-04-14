package com.yihu.entity;

public class ActivityFiles {
    private Integer id;
    private Integer activityId;
    private String name;
    private String storageName;
    private Integer fileSort;
    private String fileUrl;
    private String delFlag;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStorageName() {
        return storageName;
    }
    public void setStorageName(String storageNamel) {
        this.storageName = storageNamel;
    }

    public Integer getFileSort() {
        return fileSort;
    }

    public void setFileSort(Integer fileSort) {
        this.fileSort = fileSort;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }



    @Override
    public String toString() {
        return "ActivityFiles{" +
                "id=" + id +
                ", activityId=" + activityId +
                ", name='" + name + '\'' +
                ", storageName='" + storageName + '\'' +
                ", fileSort=" + fileSort +
                ", fileUrl='" + fileUrl + '\'' +
                ", delFlag='" + delFlag + '\'' +
                '}';
    }
}
