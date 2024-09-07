package com.meg.listshop.lmt.data.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "list_tag_stats")
public class ListTagStatistic {

    @Id
    @Tsid
    @Column(name = "list_tag_stat_id")
    private Long listTagStatId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "tag_id")
    private Long tagId;

    @Column(name = "added_count")
    private Integer addedCount;

    @Column(name = "removed_count")
    private Integer removedCount;

    @Column(name = "added_to_dish")
    private Integer addedToDish;

    public Long getListTagStatId() {
        return listTagStatId;
    }

    public void setListTagStatId(Long listTagStatId) {
        this.listTagStatId = listTagStatId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public Integer getAddedCount() {
        return addedCount;
    }

    public void setAddedCount(Integer addedCount) {
        this.addedCount = addedCount;
    }

    public Integer getRemovedCount() {
        return removedCount;
    }

    public void setRemovedCount(Integer removedCount) {
        this.removedCount = removedCount;
    }

    public Integer getAddedToDishCount() {
        return addedToDish;
    }

    public void setAddedToDishCount(Integer addedToDish) {
        this.addedToDish = addedToDish;
    }
}