package com.meg.listshop.lmt.data.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "tag_search_group")
@GenericGenerator(
        name = "tag_search_group_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value = "tag_search_group_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value = "1")}
)
@Deprecated
public class TagSearchGroupEntity {
//MM tag work
    //MM remove safely

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tag_search_group_sequence")
    @Column(name = "tag_search_group_id")
    private Long id;

    private Long groupId;

    private Long memberId;

    public TagSearchGroupEntity(Long groupId, Long id) {
        this.groupId = groupId;
        this.memberId = id;
    }

    public TagSearchGroupEntity() {
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