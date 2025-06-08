package com.meg.listshop.lmt.service.tag.impl;

import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.ShadowTags;
import com.meg.listshop.lmt.data.pojos.AutoTagSubject;
import com.meg.listshop.lmt.data.repository.ShadowTagRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.lmt.dish.DishService;
import com.meg.listshop.lmt.service.ServiceTestUtils;
import com.meg.listshop.lmt.service.tag.AutoTagProcessor;
import com.meg.listshop.lmt.service.tag.AutoTagService;
import com.meg.listshop.lmt.service.tag.TagService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;


@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class AutoTagServiceImplMockTest {


    AutoTagService autoTagService;

    @MockBean
    DishService dishService;

    @MockBean
    TagRepository tagRepository;

    @MockBean
    ShadowTagRepository shadowTagRepository;

    @MockBean
    UserService userService;

    @MockBean
    TagService tagService;

    private AutoTagProcessor mockProcessor;

    @Before
    public void setUp() {
        mockProcessor = Mockito.mock(AutoTagProcessor.class);
        List<AutoTagProcessor> processorList = Collections.singletonList(mockProcessor);
        autoTagService = new AutoTagServiceImpl(tagRepository, shadowTagRepository, userService, dishService, tagService, processorList);
    }


    @Test
    public void getDishesToAutotag() {
        // test max number
        // test limit = 5 (test configuration)
        Mockito.when(dishService.getDishesToAutotag(105L, 5)).thenReturn(new ArrayList<DishEntity>());
        List<DishEntity> result = autoTagService.getDishesToAutotag(5);
        Assert.assertTrue(result.isEmpty());

    }


    @Test
    public void doAutoTag() throws Exception {
        Long userId = 20L;
        String userName = "george";
        Long dishId = 64L;
        String dishName = "Chef John's Chicken Kiev";
        Long shadowTagId = 111L;
        Long newTagId = 13L;

        DishEntity dishEntity = ServiceTestUtils.buildDishWithTags(userId, dishName, new ArrayList<>());
        dishEntity.setId(dishId);
        boolean override = false;

        UserEntity testUser = ServiceTestUtils.buildUser(userId, userName);

        List<Long> tagList = Arrays.asList(19L, 47L, 62L, 69L, 247L, 320L, 334L, 344L, 346L, 347L, 348L, 350L, 360L, 368L, 406L);
        Set<Long> tagIdsForDish = new HashSet(tagList);
        List<ShadowTags> shadowTags = Arrays.asList(ServiceTestUtils.buildShadowTag(shadowTagId, dishId));
        List<ShadowTags> resultShadowTags = Arrays.asList(ServiceTestUtils.buildShadowTag(newTagId, dishId));
        Set<Long> processedBy = new HashSet<Long>();
        processedBy.add(newTagId);

        AutoTagSubject processedSubject = new AutoTagSubject(dishEntity, override);
        processedSubject.setShadowTags(shadowTags);
        processedSubject.setProcessedBySet(processedBy);
        processedSubject.addToTagIdsToAssign(newTagId);
        Set<Long> tagIdsToAssign = new HashSet<>();
        tagIdsToAssign.add(newTagId);

        ArgumentCaptor<AutoTagSubject> subjectCapture = ArgumentCaptor.forClass(AutoTagSubject.class);
        ArgumentCaptor<DishEntity> dishCapture = ArgumentCaptor.forClass(DishEntity.class);
        Mockito.when(userService.getUserById(userId)).thenReturn(testUser);
        Mockito.when(tagRepository.getTagIdsForDish(dishId)).thenReturn(tagIdsForDish);
        Mockito.when(shadowTagRepository.findShadowTagsByDishId(dishId)).thenReturn(shadowTags);
        Mockito.when(mockProcessor.autoTagSubject(subjectCapture.capture())).thenReturn(processedSubject);
        Mockito.when(dishService.save(dishCapture.capture(), Mockito.eq(false))).thenReturn(dishEntity);
        Mockito.doNothing().when(tagService).addTagsToDish(userId, dishId, tagIdsToAssign);
        Mockito.when(shadowTagRepository.saveAll(resultShadowTags)).thenReturn(resultShadowTags);

        autoTagService.doAutoTag(dishEntity, override);

        // verify that subject has been filled correctly, which is fed to autoprocessors
        AutoTagSubject subjectCheck = subjectCapture.getValue();
        Assert.assertNotNull(subjectCheck);
        Assert.assertEquals(tagIdsForDish, subjectCheck.getTagIdsForDish());
        Assert.assertEquals(shadowTags, subjectCheck.getShadowTags());

        // verify that processed by correctly sent to save in dish
        DishEntity dishResult = dishCapture.getValue();
        Assert.assertNotNull(dishResult);
        Assert.assertEquals(Long.valueOf(newTagId), dishResult.getAutoTagStatus());


    }


}
