package com.meg.atable.lmt.service.tag.impl;

import com.meg.atable.lmt.api.model.ListLayoutType;
import com.meg.atable.lmt.api.model.TagType;
import com.meg.atable.lmt.data.entity.ListLayoutCategoryEntity;
import com.meg.atable.lmt.data.entity.ListLayoutEntity;
import com.meg.atable.lmt.data.entity.TagEntity;
import com.meg.atable.lmt.service.ListLayoutService;
import com.meg.atable.lmt.service.impl.ServiceTestUtils;
import com.meg.atable.lmt.service.tag.TagService;
import com.meg.atable.lmt.service.tag.TagStructureService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.isA;

/**
 * Created by margaretmartin on 21/03/2018.
 */
@RunWith(SpringRunner.class)
public class TagChangeToCategoryListenerTest {

    @TestConfiguration
    static class TagChangeToCategoryListenerTestConfiguration {

        @Bean
        public TagChangeToCategoryListener tagChangeToCategoryListener() {
            return new TagChangeToCategoryListener();
        }
    }

@Autowired
private TagChangeToCategoryListener tagChangeToCategoryListener;

    @MockBean
    private TagService tagService;

    @MockBean
    private TagStructureService tagStructureService;

    @MockBean
    private ListLayoutService listLayoutService;

    TagEntity origParent;
    TagEntity newParent;
    TagEntity childTag;
    List<TagEntity> siblings;
    List<ListLayoutCategoryEntity> categories;

    @Before
    public void setup() {

         // MM finish this test - make sure it's really testing
//     public void onParentChange(TagEntity origParentTag, TagEntity newParentTag, TagEntity childTag) {
siblings = new ArrayList<TagEntity>();
categories = new ArrayList<ListLayoutCategoryEntity>();
        // set up original parent tag
        origParent = ServiceTestUtils.buildTag("origParent", TagType.Ingredient);

        // set up new parent tag
        newParent = ServiceTestUtils.buildTag("newParent", TagType.Ingredient);

        // set up child tag
        childTag = ServiceTestUtils.buildTag(55L,"childTag", TagType.Ingredient);


        // set up siblings
        siblings.add( ServiceTestUtils.buildTag("sibling1", TagType.Ingredient));
        siblings.add( ServiceTestUtils.buildTag("sibling2", TagType.Ingredient));
        siblings.add( ServiceTestUtils.buildTag("sibling3", TagType.Ingredient));

        // set up categories
        for (int i=0;i<3;i++) {

        ListLayoutEntity listLayout = ServiceTestUtils.buildListLayout(Long.valueOf(""+i),"listLayout" + 1, ListLayoutType.All);
        ListLayoutCategoryEntity category = ServiceTestUtils.buildListCategory(Long.valueOf(""+i),"category" + i, listLayout);
        categories.add(category);
        }
    }

    @Test
    public void testOnParentChange_NotFromDefault() {
        origParent.setTagTypeDefault(false);

        tagChangeToCategoryListener.onParentChange(origParent,newParent,childTag);
    }

    @Test
    public void testOnParentChange_HappyPath() {
        origParent.setTagTypeDefault(true);
        List<Long> toAdd = new ArrayList<>();
        toAdd.add(childTag.getId());

        Mockito.when(tagStructureService.getDescendantTags(newParent,false))
                .thenReturn(siblings);

        Mockito.when(listLayoutService.getCategoriesForTag(siblings.get(0)))
                .thenReturn(categories);

        for (ListLayoutCategoryEntity categoryEntity: categories) {
            Mockito.doNothing().when(
                    listLayoutService).addTagsToCategory(isA(Long.class),isA(Long.class),isA(List.class));
        }

        tagChangeToCategoryListener.onParentChange(origParent,newParent,childTag);
    }
}