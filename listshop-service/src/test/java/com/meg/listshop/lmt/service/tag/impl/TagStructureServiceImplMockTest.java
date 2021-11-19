package com.meg.listshop.lmt.service.tag.impl;

import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.entity.TagRelationEntity;
import com.meg.listshop.lmt.data.repository.TagRelationRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.lmt.data.repository.TagSearchGroupRepository;
import com.meg.listshop.lmt.service.tag.TagCache;
import com.meg.listshop.lmt.service.tag.TagStructureService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class TagStructureServiceImplMockTest {

    private TagStructureService tagStructureService;


    @MockBean
    private TagCache tagCache;
    @MockBean
    private TagSearchGroupRepository tagSearchGroupRepository;
    @MockBean
    private TagRelationRepository tagRelationRepository;
    @MockBean
    private TagRepository tagRepository;


    @Before
    public void setUp() {

        tagStructureService = new TagStructureServiceImpl(
                tagCache,
                tagSearchGroupRepository,
                tagRelationRepository,
                tagRepository
        );
    }


    @Test
    public void testFillInRelationshipInfo() throws Exception {
        // tag 1
        // -- tag 11
        // -- tag 12
        // tag 2
        // -- tag 22
        // -- -- tag 221
        // -- -- tag 222

        // need list of tags - 1, 2, 3
        TagEntity tag1 = createTag(1L);
        TagEntity tag11 = createTag(11L);
        TagEntity tag12 = createTag(12L);
        TagEntity tag2 = createTag(2L);
        TagEntity tag22 = createTag(22L);
        TagEntity tag221 = createTag(221L);
        TagEntity tag222 = createTag(222L);

        List<TagEntity> startTagList = Arrays.asList(tag1, tag11,
                tag12, tag2, tag22, tag221, tag222);
        List<TagRelationEntity> childrenTag1 = Arrays.asList(createRelation(tag1, tag11),
                createRelation(tag1, tag12));
        List<TagRelationEntity> childrenTag2 = Arrays.asList(createRelation(tag2, tag22));
        List<TagRelationEntity> childrenTag22 = Arrays.asList(createRelation(tag22, tag221),
                createRelation(tag22, tag222));

        Mockito.when(tagRelationRepository.findByChild(tag1)).thenReturn(Optional.ofNullable(null));
        Mockito.when(tagRelationRepository.findByParent(tag1)).thenReturn(childrenTag1);
        Mockito.when(tagRelationRepository.findByChild(tag11)).thenReturn(Optional.of(createRelation(tag1, tag11)));
        Mockito.when(tagRelationRepository.findByParent(tag11)).thenReturn(new ArrayList<>());
        Mockito.when(tagRelationRepository.findByChild(tag12)).thenReturn(Optional.of(createRelation(tag1, tag12)));
        Mockito.when(tagRelationRepository.findByParent(tag12)).thenReturn(new ArrayList<>());
        Mockito.when(tagRelationRepository.findByChild(tag2)).thenReturn(Optional.ofNullable(null));
        Mockito.when(tagRelationRepository.findByParent(tag2)).thenReturn(childrenTag2);
        Mockito.when(tagRelationRepository.findByChild(tag22)).thenReturn(Optional.of(createRelation(tag2, tag22)));
        Mockito.when(tagRelationRepository.findByParent(tag22)).thenReturn(childrenTag22);
        Mockito.when(tagRelationRepository.findByChild(tag221)).thenReturn(Optional.of(createRelation(tag22, tag221)));
        Mockito.when(tagRelationRepository.findByParent(tag221)).thenReturn(new ArrayList<>());
        Mockito.when(tagRelationRepository.findByChild(tag221)).thenReturn(Optional.of(createRelation(tag22, tag221)));
        Mockito.when(tagRelationRepository.findByParent(tag221)).thenReturn(new ArrayList<>());


        // call under test
        List<TagEntity> results = tagStructureService.fillInRelationshipInfo(startTagList);

        // assertions
        Assert.assertEquals(startTagList.size(), results.size());
        // tag 1 shouldn't have a parent - but should have 2 children
        Optional<TagEntity> resultTag1 = results.stream().filter(tag -> tag.getId().equals(1L)).findFirst();
        Assert.assertTrue(resultTag1.isPresent());
        Assert.assertTrue(resultTag1.get().getParentId() == null);
        Assert.assertTrue(resultTag1.get().getChildrenIds().size() == 2);
        // tag 22 should have a parent (tag2), and 2 children
        Optional<TagEntity> resultTag22 = results.stream().filter(tag -> tag.getId().equals(22L)).findFirst();
        Assert.assertTrue(resultTag22.isPresent());
        Assert.assertTrue(resultTag22.get().getParentId() == 2);
        Assert.assertTrue(resultTag22.get().getChildrenIds().size() == 2);
    }

    private TagEntity createTag(Long id) {
        TagEntity tag = new TagEntity(id);
        tag.setName("tag" + id);
        return tag;
    }

    private TagRelationEntity createRelation(TagEntity parent, TagEntity child) {
        TagRelationEntity relationEntity = new TagRelationEntity();
        relationEntity.setParent(parent);
        relationEntity.setChild(child);
        return relationEntity;
    }




}