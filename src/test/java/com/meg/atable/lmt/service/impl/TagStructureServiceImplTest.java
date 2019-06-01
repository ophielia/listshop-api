package com.meg.atable.lmt.service.impl;

import com.meg.atable.Application;
import com.meg.atable.lmt.api.model.FatTag;
import com.meg.atable.lmt.api.model.TagType;
import com.meg.atable.lmt.service.tag.TagStructureService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Transactional
@Sql(value = {"/sql/com/meg/atable/lmt/service/impl/TagStructureServiceTest-rollback.sql",
        "/sql/com/meg/atable/lmt/service/impl/TagStructureServiceTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/com/meg/atable/lmt/service/impl/TagStructureServiceTest-rollback.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class TagStructureServiceImplTest {


    @Autowired
    private TagStructureService tagStructureService;

    @Test
    public void testGetTagsWithChildren() {
        List<FatTag> results = tagStructureService.getTagsWithChildren(null);

        Assert.assertNotNull(results);
        FatTag meatCategory = results.stream().filter(t -> t.getId().equals(371L)).findFirst().orElse(null);
        Assert.assertNotNull(meatCategory);
        FatTag beefCategory = meatCategory.getChildren().stream().filter(t -> t.getId().equals(372L)).findFirst().orElse(null);
        Assert.assertNotNull(beefCategory);
        FatTag stewMeatCategory = beefCategory.getChildren().stream().filter(t -> t.getId().equals(251L)).findFirst().orElse(null);
        Assert.assertNotNull(stewMeatCategory);

        results = tagStructureService.getTagsWithChildren(Collections.singletonList(TagType.Rating));
        boolean allgood = true;
        for (FatTag resultTag : results) {
            if (!resultTag.getTagType().equals(TagType.Rating)) {
                allgood = false;
                break;
            }
        }
        Assert.assertTrue(allgood);
    }



    @Test
    public void testToDeleteTagsNotIncluded() {
        // tag cod, tag_id = 506 is marked as to delete.  Should not be returned
        List<FatTag> results = tagStructureService.getTagsWithChildren(null);

        // gather all keys in FatTag
        Set<Long> allTagIds = new HashSet<>();
        for (FatTag tag : results) {
            allTagIds.add(tag.getId());
            allTagIds.addAll(getChildrenIds(tag));
        }

        Optional<Long> testFind = allTagIds.stream().filter(t-> t.equals(506L)).findFirst();
        Assert.assertFalse(testFind.isPresent());
    }

    @Test
    public void testGetChangedTagsWithChildren() {
        // setup - tag structure
        // tag 4004
        //      tag 4000
        //          tag 4001
        //              tag 4002
        //          tag 4003
        //          tag 4005

        // tag 4002 and 4003 changed
        LocalDateTime changedAfter = LocalDateTime.now().minusHours(1);
        List<FatTag> changed = tagStructureService.getChangedTagsWithChildren(java.sql.Timestamp.valueOf(changedAfter));

        Assert.assertNotNull(changed);
        Assert.assertEquals(1, changed.size());

        List<FatTag> resultList = changed.stream()
                .flatMap(FatTag::flattened)
                .collect(Collectors.toList());
        Map<Long, FatTag> resultMap = resultList.stream().collect(Collectors.toMap(f -> f.getId(), f -> f));

        Assert.assertFalse(resultMap.containsKey(1400005L));
        Assert.assertTrue(resultMap.containsKey(1400003L));
        Assert.assertTrue(resultMap.containsKey(1400002L));
    }

    private Set<Long> getChildrenIds(FatTag tag) {
        Set<Long> childrenIds = new HashSet<>();
        childrenIds.add(tag.getId());
        for (FatTag child : tag.getChildren()) {

           childrenIds.addAll(getChildrenIds(child));
        }
        return childrenIds;
    }
}