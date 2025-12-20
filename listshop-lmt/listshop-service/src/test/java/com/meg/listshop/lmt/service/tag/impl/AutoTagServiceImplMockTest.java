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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AutoTagServiceImplMockTest {


    AutoTagService autoTagService;

    @Mock
    DishService dishService;

    @Mock
    TagRepository tagRepository;

    @Mock
    ShadowTagRepository shadowTagRepository;

    @Mock
    UserService userService;

    @Mock
    TagService tagService;

    private AutoTagProcessor mockProcessor;

    @BeforeEach
    void setUp() {
        mockProcessor = Mockito.mock(AutoTagProcessor.class);
        List<AutoTagProcessor> processorList = Collections.singletonList(mockProcessor);
        autoTagService = new AutoTagServiceImpl(tagRepository, shadowTagRepository, userService, dishService, tagService, processorList);
    }


    @Test
    void getDishesToAutotag() {
        // test max number
        // test limit = 5 (test configuration)
        Mockito.when(dishService.getDishesToAutotag(0L, 5)).thenReturn(new ArrayList<DishEntity>());
        List<DishEntity> result = autoTagService.getDishesToAutotag(5);
        Assertions.assertTrue(result.isEmpty());

    }


    @Test
    void doAutoTag() {
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

        autoTagService.doAutoTag(dishEntity, override);

        // verify that subject has been filled correctly, which is fed to autoprocessors
        AutoTagSubject subjectCheck = subjectCapture.getValue();
        Assertions.assertNotNull(subjectCheck);
        Assertions.assertEquals(tagIdsForDish, subjectCheck.getTagIdsForDish());
        Assertions.assertEquals(shadowTags, subjectCheck.getShadowTags());

        // verify that processed by correctly sent to save in dish
        DishEntity dishResult = dishCapture.getValue();
        Assertions.assertNotNull(dishResult);
        Assertions.assertEquals(newTagId, dishResult.getAutoTagStatus());


    }


}
