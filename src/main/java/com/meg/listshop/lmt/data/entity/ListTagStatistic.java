package com.meg.listshop.lmt.data.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "list_tag_stats")
@GenericGenerator(
        name = "list_tag_stats_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value="list_tag_stats_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value="1")}
)
public class ListTagStatistic {

    @Id
    @GeneratedValue( strategy=GenerationType.SEQUENCE, generator="list_tag_stats_sequence")
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