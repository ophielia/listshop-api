package com.meg.atable.data.entity;

import javax.persistence.*;

@Entity
@Table(name = "tag_search_group")
@SequenceGenerator(name="tag_search_group_sequence", sequenceName = "tag_search_group_sequence")
public class TagSearchGroupEntity {

    @Id
    @GeneratedValue( strategy=GenerationType.SEQUENCE, generator="tag_search_group_sequence")
    @Column(name = "tag_search_group_id")
    private Long id;

    private Long groupId;

    private Long memberId;

    public TagSearchGroupEntity(Long groupId, Long id) {
        this.groupId = groupId;
        this.memberId = id;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
}