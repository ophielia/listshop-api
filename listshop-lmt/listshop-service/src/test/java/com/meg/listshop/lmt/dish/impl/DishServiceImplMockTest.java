package com.meg.listshop.lmt.dish.impl;

import com.meg.listshop.auth.data.repository.UserRepository;
import com.meg.listshop.common.FlatStringUtils;
import com.meg.listshop.conversion.service.ConversionService;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.model.FractionType;
import com.meg.listshop.lmt.api.model.RatingUpdateInfo;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.DishItemEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.pojos.DishDTO;
import com.meg.listshop.lmt.data.pojos.DishItemDTO;
import com.meg.listshop.lmt.data.repository.DishItemRepository;
import com.meg.listshop.lmt.data.repository.DishRepository;
import com.meg.listshop.lmt.dish.DishService;
import com.meg.listshop.lmt.service.ServiceTestUtils;
import com.meg.listshop.lmt.service.food.AmountService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class DishServiceImplMockTest {

    private DishService dishService;

    @Mock
    private DishRepository dishRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AutoTagService autoTagService;
    @Mock
    private TagService tagService;
    @Mock
    private DishItemRepository dishItemRepository;
    @Mock
    private AmountService amountService;
    @Mock
    private ConversionService conversionService;


    @BeforeEach
    public void setUp() {

        dishService = new DishServiceImpl(dishRepository,
                userRepository,
                autoTagService,
                tagService,
                dishItemRepository,
                amountService,
                conversionService
        );
    }


    @Test
    void testCreateDish() {
        String testDishName = "test mock dish";
        Long userId = 99L;
        DishEntity dish = new DishEntity();
        dish.setDishName(testDishName);
        dish.setReference("reference");
        dish.setUserId(99L);

        ArgumentCaptor<DishEntity> argument = ArgumentCaptor.forClass(DishEntity.class);

        Mockito.when(dishRepository.findByUserIdAndDishName(99L, testDishName))
                .thenReturn(new ArrayList<>());

        Mockito.when(dishRepository.save(argument.capture()))
                .thenReturn(dish);

        dishService.createDish(userId, dish);

        DishEntity dishSaved = argument.getValue();
        Assertions.assertEquals(testDishName, dishSaved.getDishName());
        Assertions.assertEquals(Long.valueOf(99L), dishSaved.getUserId());
        Assertions.assertEquals("reference", dishSaved.getReference());
    }

    @Test
    void testCreateDish_ExistingName() {
        String testDishName = "test mock dish";
        Long testUserId = 99L;
        DishEntity dish = new DishEntity();
        dish.setDishName(testDishName);
        dish.setUserId(99L);

        // make found duplicate names
        DishEntity existingDish1 = new DishEntity();
        existingDish1.setDishName(testDishName + " " + 2);
        existingDish1.setUserId(99L);
        DishEntity existingDish2 = new DishEntity();
        existingDish2.setDishName(testDishName + " " + 8);
        existingDish2.setUserId(99L);
        DishEntity existingDish3 = new DishEntity();
        existingDish3.setDishName(testDishName + "Abracadabra");
        existingDish3.setUserId(99L);

        ArgumentCaptor<DishEntity> argument = ArgumentCaptor.forClass(DishEntity.class);

        Mockito.when(dishRepository.findByUserIdAndDishName(99L, testDishName))
                .thenReturn(Collections.singletonList(dish));
        Mockito.when(dishRepository.findByUserIdAndDishNameLike(99L, testDishName + "%"))
                .thenReturn(Arrays.asList(dish, existingDish1, existingDish2, existingDish3));

        Mockito.when(dishRepository.save(argument.capture()))
                .thenReturn(dish);

        dishService.createDish(testUserId, dish);

        DishEntity dishSaved = argument.getValue();
        Assertions.assertEquals(testDishName + " " + 4, dishSaved.getDishName());
        Assertions.assertEquals(Long.valueOf(99L), dishSaved.getUserId());

    }

    @Test
    void testGetDishesToAutotag() {
        Long statusFlag = 105L;
        int dishLimit = 15;

        Pageable expectedPageable = PageRequest.of(0, dishLimit);

        dishService.getDishesToAutotag(statusFlag, dishLimit);

        Mockito.verify(dishRepository, Mockito.times(1)).findDishesToAutotag(statusFlag, expectedPageable);


    }

    @Test
    void testAddIngredientToDish_Simple() {
        Long userId = 99L;
        Long dishId = 999L;
        Long tagId = 9999L;
        Long unitId = 99999L;
        DishItemDTO dishItemDto = new DishItemDTO();
        dishItemDto.setTagId(tagId);
        dishItemDto.setWholeQuantity(1);
        dishItemDto.setFractionalQuantity(FractionType.OneHalf);
        dishItemDto.setUnitId(unitId);

        DishEntity dishEntity = new DishEntity();
        dishEntity.setUserId(userId);
        dishEntity.setId(dishId);

        TagEntity tag = new TagEntity();
        tag.setId(tagId);

        ArgumentCaptor<DishItemEntity> itemCaptor = ArgumentCaptor.forClass(DishItemEntity.class);
        ArgumentCaptor<DishEntity> dishCaptor = ArgumentCaptor.forClass(DishEntity.class);

        Mockito.when(dishRepository.findByDishIdForUser(userId, dishId))
                .thenReturn(Optional.of(dishEntity));
        Mockito.when(tagService.getTagById(tagId))
                .thenReturn(tag);
        Mockito.when(dishItemRepository.save(itemCaptor.capture()))
                .thenReturn(new DishItemEntity());
        Mockito.when(dishRepository.save(dishCaptor.capture()))
                .thenReturn(new DishEntity());

        dishService.addIngredientToDish(userId, dishId, dishItemDto);
        DishEntity dishResult = dishCaptor.getValue();
        DishItemEntity ingredientResult = itemCaptor.getValue();
        Assertions.assertNotNull(ingredientResult);
        Assertions.assertNotNull(dishResult);
        // check dish result
        Assertions.assertEquals(1, dishResult.getItems().size());
        Assertions.assertEquals(dishResult.getItems().get(0).getTag(), tag);
        Assertions.assertEquals(1.5, dishResult.getItems().get(0).getQuantity(), 0.001);
        // check ingredient
        Assertions.assertNull(ingredientResult.getMarker());
        Assertions.assertEquals("medium", ingredientResult.getUnitSize());
        Assertions.assertFalse(ingredientResult.getUserSize());
        Assertions.assertNull(ingredientResult.getRawModifiers());
        Assertions.assertEquals(ingredientResult.getWholeQuantity(), (Integer) 1);
        Assertions.assertEquals(FractionType.OneHalf, ingredientResult.getFractionalQuantity());
        Assertions.assertEquals(1.5, ingredientResult.getQuantity(), 0.001);
        Assertions.assertEquals(unitId, ingredientResult.getUnitId());
        Assertions.assertNotNull(ingredientResult.getUnitId());
        Assertions.assertTrue(ingredientResult.getModifiersProcessed());
        Assertions.assertNull(dishResult.getItems().get(0).getTag().getConversionId());
    }

    @Test
    void testAddIngredientToDish_ConversionIdNoMarkers() {
        Long userId = 99L;
        Long dishId = 999L;
        Long tagId = 9999L;
        Long unitId = 99999L;
        Long conversionId = 999999L;

        DishItemDTO dishItemDto = new DishItemDTO();
        dishItemDto.setTagId(tagId);
        dishItemDto.setWholeQuantity(1);
        dishItemDto.setUnitId(unitId);

        DishEntity dishEntity = new DishEntity();
        dishEntity.setUserId(userId);
        dishEntity.setId(dishId);

        TagEntity tag = new TagEntity();
        tag.setId(tagId);
        tag.setConversionId(conversionId);

        ArgumentCaptor<DishItemEntity> itemCaptor = ArgumentCaptor.forClass(DishItemEntity.class);
        ArgumentCaptor<DishEntity> dishCaptor = ArgumentCaptor.forClass(DishEntity.class);

        Mockito.when(dishRepository.findByDishIdForUser(userId, dishId))
                .thenReturn(Optional.of(dishEntity));
        Mockito.when(tagService.getTagById(tagId))
                .thenReturn(tag);
        Mockito.when(dishItemRepository.save(itemCaptor.capture()))
                .thenReturn(new DishItemEntity());
        Mockito.when(dishRepository.save(dishCaptor.capture()))
                .thenReturn(new DishEntity());

        dishService.addIngredientToDish(userId, dishId, dishItemDto);


        DishEntity dishResult = dishCaptor.getValue();
        DishItemEntity ingredientResult = itemCaptor.getValue();
        Assertions.assertNotNull(ingredientResult);
        Assertions.assertNotNull(dishResult);
        // check dish result
        Assertions.assertEquals(1, dishResult.getItems().size());
        Assertions.assertEquals(dishResult.getItems().get(0).getTag(), tag);
        Assertions.assertEquals(1.0, dishResult.getItems().get(0).getQuantity(), 0.001);
        // check ingredient
        Assertions.assertNull(ingredientResult.getMarker());
        Assertions.assertNotNull(ingredientResult.getUnitSize());
        Assertions.assertEquals("medium", ingredientResult.getUnitSize());
        Assertions.assertNull(ingredientResult.getRawModifiers());
        Assertions.assertNotNull(dishResult.getItems().get(0).getTag().getConversionId());
        Assertions.assertNotNull(ingredientResult.getUnitId());
        Assertions.assertEquals(conversionId, dishResult.getItems().get(0).getTag().getConversionId());
        Assertions.assertEquals(ingredientResult.getWholeQuantity(), (Integer) 1);
        Assertions.assertNull(ingredientResult.getFractionalQuantity());
        Assertions.assertEquals(1, ingredientResult.getQuantity(), 0.001);
        Assertions.assertEquals(unitId, ingredientResult.getUnitId());
        Assertions.assertTrue(ingredientResult.getModifiersProcessed());
    }

    @Test
    void testAddIngredientToDish_ConversionMarkers() {
        Long userId = 99L;
        Long dishId = 999L;
        Long tagId = 9999L;
        Long unitId = 99999L;
        Long conversionId = 999999L;
        String rawModifiers = "chopped extra large";
        List<String> modifierTokens = List.of("chopped", "extra large");
        String marker = "chopped";
        String unitSize = "extra large";

        DishItemDTO dishItemDto = new DishItemDTO();
        dishItemDto.setTagId(tagId);
        dishItemDto.setWholeQuantity(1);
        dishItemDto.setFractionalQuantity(FractionType.OneEighth);
        dishItemDto.setUnitId(unitId);
        dishItemDto.setRawModifiers(modifierTokens);

        DishEntity dishEntity = new DishEntity();
        dishEntity.setUserId(userId);
        dishEntity.setId(dishId);

        TagEntity tag = new TagEntity();
        tag.setId(tagId);
        tag.setConversionId(conversionId);

        ArgumentCaptor<DishItemEntity> itemCaptor = ArgumentCaptor.forClass(DishItemEntity.class);
        ArgumentCaptor<DishEntity> dishCaptor = ArgumentCaptor.forClass(DishEntity.class);

        Mockito.when(dishRepository.findByDishIdForUser(userId, dishId))
                .thenReturn(Optional.of(dishEntity));
        Mockito.when(tagService.getTagById(tagId))
                .thenReturn(tag);
        Mockito.when(dishItemRepository.save(itemCaptor.capture()))
                .thenReturn(new DishItemEntity());
        Mockito.when(dishRepository.save(dishCaptor.capture()))
                .thenReturn(new DishEntity());
        Mockito.when(amountService.pullMarkersForModifers(modifierTokens, conversionId))
                .thenReturn(List.of(marker));
        Mockito.when(amountService.pullUnitSizesForModifiers(modifierTokens, conversionId))
                .thenReturn(List.of(unitSize));

        dishService.addIngredientToDish(userId, dishId, dishItemDto);


        DishEntity dishResult = dishCaptor.getValue();
        DishItemEntity ingredientResult = itemCaptor.getValue();
        Assertions.assertNotNull(ingredientResult);
        Assertions.assertNotNull(dishResult);
        // check dish result
        Assertions.assertEquals(1, dishResult.getItems().size());
        Assertions.assertEquals(dishResult.getItems().get(0).getTag(), tag);
        Assertions.assertEquals(1.125, dishResult.getItems().get(0).getQuantity(), 0.001);
        // check ingredient
        Assertions.assertNotNull(ingredientResult.getMarker());
        Assertions.assertNotNull(ingredientResult.getUnitSize());
        Assertions.assertNotNull(ingredientResult.getRawModifiers());
        Assertions.assertNotNull(dishResult.getItems().get(0).getTag().getConversionId());
        Assertions.assertNotNull(ingredientResult.getUnitId());
        Assertions.assertNotNull(ingredientResult.getFractionalQuantity());
        Assertions.assertEquals(conversionId, dishResult.getItems().get(0).getTag().getConversionId());
        Assertions.assertEquals(ingredientResult.getWholeQuantity(), (Integer) 1);
        Assertions.assertEquals(FractionType.OneEighth, ingredientResult.getFractionalQuantity());
        Assertions.assertEquals(marker, ingredientResult.getMarker());
        Assertions.assertEquals(unitSize, ingredientResult.getUnitSize());
        Assertions.assertEquals(FlatStringUtils.flattenListToString(modifierTokens,"|"), ingredientResult.getRawModifiers());
        Assertions.assertEquals(1.125, ingredientResult.getQuantity(), 0.001);
        Assertions.assertEquals(unitId, ingredientResult.getUnitId());
        Assertions.assertTrue(ingredientResult.getModifiersProcessed());
    }

    @Test
    void testAddIngredientToDish_ConversionMarkersMultiple() {
        Long userId = 99L;
        Long dishId = 999L;
        Long tagId = 9999L;
        Long unitId = 99999L;
        Long conversionId = 999999L;
        String rawModifiers = "chopped extra large and other diced ignored";
        List<String> modifierTokens = List.of("chopped", "extra large", "and", "other", "diced", "ignored");
        String marker = "chopped";
        String unitSize = "extra large";

        DishItemDTO dishItemDto = new DishItemDTO();
        dishItemDto.setTagId(tagId);
        dishItemDto.setWholeQuantity(1);
        dishItemDto.setFractionalQuantity(FractionType.OneEighth);
        dishItemDto.setUnitId(unitId);
        dishItemDto.setUnitSize(unitSize);
        dishItemDto.setRawModifiers(modifierTokens);

        DishEntity dishEntity = new DishEntity();
        dishEntity.setUserId(userId);
        dishEntity.setId(dishId);

        TagEntity tag = new TagEntity();
        tag.setId(tagId);
        tag.setConversionId(conversionId);

        ArgumentCaptor<DishItemEntity> itemCaptor = ArgumentCaptor.forClass(DishItemEntity.class);
        ArgumentCaptor<DishEntity> dishCaptor = ArgumentCaptor.forClass(DishEntity.class);

        Mockito.when(dishRepository.findByDishIdForUser(userId, dishId))
                .thenReturn(Optional.of(dishEntity));
        Mockito.when(tagService.getTagById(tagId))
                .thenReturn(tag);
        Mockito.when(dishItemRepository.save(itemCaptor.capture()))
                .thenReturn(new DishItemEntity());
        Mockito.when(dishRepository.save(dishCaptor.capture()))
                .thenReturn(new DishEntity());
        Mockito.when(amountService.pullMarkersForModifers(modifierTokens, conversionId))
                .thenReturn(List.of(marker, "diced"));
        Mockito.when(amountService.pullUnitSizesForModifiers(modifierTokens, conversionId))
                .thenReturn(List.of(unitSize));

        dishService.addIngredientToDish(userId, dishId, dishItemDto);


        DishEntity dishResult = dishCaptor.getValue();
        DishItemEntity ingredientResult = itemCaptor.getValue();
        Assertions.assertNotNull(ingredientResult);
        Assertions.assertNotNull(dishResult);
        // check dish result
        Assertions.assertEquals(1, dishResult.getItems().size());
        Assertions.assertEquals(dishResult.getItems().get(0).getTag(), tag);
        Assertions.assertEquals(1.125, dishResult.getItems().get(0).getQuantity(), 0.001);
        // check ingredient
        Assertions.assertNotNull(ingredientResult.getMarker());
        Assertions.assertNotNull(ingredientResult.getUnitSize());
        Assertions.assertNotNull(ingredientResult.getRawModifiers());
        Assertions.assertNotNull(dishResult.getItems().get(0).getTag().getConversionId());
        Assertions.assertNotNull(ingredientResult.getUnitId());
        Assertions.assertNotNull(ingredientResult.getFractionalQuantity());
        Assertions.assertEquals(conversionId, dishResult.getItems().get(0).getTag().getConversionId());
        Assertions.assertEquals(ingredientResult.getWholeQuantity(), (Integer) 1);
        Assertions.assertEquals(FractionType.OneEighth, ingredientResult.getFractionalQuantity());
        Assertions.assertEquals(marker, ingredientResult.getMarker());
        Assertions.assertEquals(unitSize, ingredientResult.getUnitSize());
        Assertions.assertEquals(FlatStringUtils.flattenListToString(modifierTokens,"|"), ingredientResult.getRawModifiers());
        Assertions.assertEquals(1.125, ingredientResult.getQuantity(), 0.001);
        Assertions.assertEquals(unitId, ingredientResult.getUnitId());
        Assertions.assertTrue(ingredientResult.getModifiersProcessed());
    }


    @Test
    public void testUpdateIngredientInDish_NotFoundMismatch() {
        Long userId = 99L;
        Long dishId = 999L;
        Long tagId = 9999L;
        Long unitId = 99999L;
        Long ingredientId = 999999L;
        DishItemDTO dishItemDto = new DishItemDTO();
        dishItemDto.setTagId(tagId);
        dishItemDto.setWholeQuantity(1);
        dishItemDto.setFractionalQuantity(FractionType.OneHalf);
        dishItemDto.setUnitId(unitId);

        DishItemEntity dishItemEntity = new DishItemEntity();
        dishItemEntity.setDishItemId(11L);

        DishEntity dishEntity = new DishEntity();
        dishEntity.setUserId(userId);
        dishEntity.setId(dishId);
        dishEntity.setItems(Arrays.asList(dishItemEntity));

        TagEntity tag = new TagEntity();
        tag.setId(tagId);

        Assertions.assertThrows(ObjectNotFoundException.class, () -> {
            dishService.updateIngredientInDish(userId, dishId, dishItemDto);
        });

    }

    @Test
    public void testUpdateIngredientInDish_NoExisting() {
        Long userId = 99L;
        Long dishId = 999L;
        Long tagId = 9999L;
        Long unitId = 99999L;
        DishItemDTO dishItemDto = new DishItemDTO();
        dishItemDto.setTagId(tagId);
        dishItemDto.setWholeQuantity(1);
        dishItemDto.setFractionalQuantity(FractionType.OneHalf);
        dishItemDto.setUnitId(unitId);

        DishEntity dishEntity = new DishEntity();
        dishEntity.setUserId(userId);
        dishEntity.setId(dishId);

        TagEntity tag = new TagEntity();
        tag.setId(tagId);

        ArgumentCaptor<DishItemEntity> itemCaptor = ArgumentCaptor.forClass(DishItemEntity.class);
        ArgumentCaptor<DishEntity> dishCaptor = ArgumentCaptor.forClass(DishEntity.class);

        Mockito.when(dishRepository.findByDishIdForUser(userId, dishId))
                .thenReturn(Optional.of(dishEntity));

        Assertions.assertThrows(ObjectNotFoundException.class, () -> {
            dishService.updateIngredientInDish(userId, dishId, dishItemDto);
        });

    }

    @Test
    public void testUpdateIngredientInDish_NoItemId() {
        Long userId = 99L;
        Long dishId = 999L;
        Long tagId = 9999L;
        Long unitId = 99999L;
        DishItemDTO dishItemDto = new DishItemDTO();
        dishItemDto.setTagId(tagId);
        dishItemDto.setWholeQuantity(1);
        dishItemDto.setFractionalQuantity(FractionType.OneHalf);
        dishItemDto.setUnitId(unitId);

        DishEntity dishEntity = new DishEntity();
        dishEntity.setUserId(userId);
        dishEntity.setId(dishId);

        TagEntity tag = new TagEntity();
        tag.setId(tagId);

        Mockito.when(dishRepository.findByDishIdForUser(userId, dishId))
                .thenReturn(Optional.of(dishEntity));
        Assertions.assertThrows(ObjectNotFoundException.class, () -> {
            dishService.updateIngredientInDish(userId, dishId, dishItemDto);
        });

    }

    @Test
    void testUpdateIngredientInDish_Simple() {
        Long userId = 99L;
        Long dishId = 999L;
        Long tagId = 9999L;
        Long unitId = 99999L;
        Long conversionId = 999999L;
        Long ingredientId = 9999999L;

        DishItemDTO dishItemDto = new DishItemDTO();
        dishItemDto.setTagId(tagId);
        dishItemDto.setWholeQuantity(1);
        dishItemDto.setUnitId(unitId);
        dishItemDto.setDishItemId(ingredientId);

        DishItemEntity existingIngredient = new DishItemEntity();
        existingIngredient.setDishItemId(ingredientId);
        existingIngredient.setWholeQuantity(11);
        existingIngredient.setFractionalQuantity(FractionType.FiveEighths);
        existingIngredient.setUnitId(1L);
        List<DishItemEntity> dishItemList = new ArrayList<>();
        dishItemList.add(existingIngredient);

        DishEntity dishEntity = new DishEntity();
        dishEntity.setUserId(userId);
        dishEntity.setId(dishId);
        dishEntity.setItems(dishItemList);

        TagEntity tag = new TagEntity();
        tag.setId(tagId);
        tag.setConversionId(conversionId);

        ArgumentCaptor<DishItemEntity> itemCaptor = ArgumentCaptor.forClass(DishItemEntity.class);
        ArgumentCaptor<DishEntity> dishCaptor = ArgumentCaptor.forClass(DishEntity.class);

        Mockito.when(dishRepository.findByDishIdForUser(userId, dishId))
                .thenReturn(Optional.of(dishEntity));
        Mockito.when(tagService.getTagById(tagId))
                .thenReturn(tag);
        Mockito.when(dishItemRepository.save(itemCaptor.capture()))
                .thenReturn(new DishItemEntity());
        Mockito.when(dishRepository.save(dishCaptor.capture()))
                .thenReturn(new DishEntity());

        dishService.updateIngredientInDish(userId, dishId, dishItemDto);


        DishEntity dishResult = dishCaptor.getValue();
        DishItemEntity ingredientResult = itemCaptor.getValue();
        Assertions.assertNotNull(ingredientResult);
        Assertions.assertNotNull(dishResult);
        // check dish result
        Assertions.assertEquals(1, dishResult.getItems().size());
        Assertions.assertEquals(dishResult.getItems().get(0).getTag(), tag);
        Assertions.assertEquals(1.0, dishResult.getItems().get(0).getQuantity(), 0.001);
        // check ingredient
        Assertions.assertNull(ingredientResult.getMarker());
        Assertions.assertNotNull(ingredientResult.getUnitSize());
        Assertions.assertEquals("medium", ingredientResult.getUnitSize());
        Assertions.assertNull(ingredientResult.getRawModifiers());
        Assertions.assertNotNull(dishResult.getItems().get(0).getTag().getConversionId());
        Assertions.assertNotNull(ingredientResult.getUnitId());
        Assertions.assertEquals(conversionId, dishResult.getItems().get(0).getTag().getConversionId());
        Assertions.assertEquals(ingredientResult.getWholeQuantity(), (Integer) 1);
        Assertions.assertNull(ingredientResult.getFractionalQuantity());
        Assertions.assertEquals(1, ingredientResult.getQuantity(), 0.001);
        Assertions.assertEquals(unitId, ingredientResult.getUnitId());
        Assertions.assertTrue(ingredientResult.getModifiersProcessed());
    }


    @Test
    void testUpdateIngredientInDish_ConversionIdNoMarkers() {
        Long userId = 99L;
        Long dishId = 999L;
        Long tagId = 9999L;
        Long unitId = 99999L;
        Long conversionId = 999999L;
        Long ingredientId = 9999999L;

        DishItemDTO dishItemDto = new DishItemDTO();
        dishItemDto.setTagId(tagId);
        dishItemDto.setWholeQuantity(1);
        dishItemDto.setUnitId(unitId);
        dishItemDto.setDishItemId(ingredientId);

        DishItemEntity existingIngredient = new DishItemEntity();
        existingIngredient.setDishItemId(ingredientId);
        existingIngredient.setWholeQuantity(11);
        existingIngredient.setFractionalQuantity(FractionType.FiveEighths);
        existingIngredient.setUnitId(1L);
        existingIngredient.setMarker("wild and crazy guy");
        existingIngredient.setUnitSize("unspecified");
        existingIngredient.setRawModifiers("present and accounted for");
        List<DishItemEntity> dishItemList = new ArrayList<>();
        dishItemList.add(existingIngredient);

        DishEntity dishEntity = new DishEntity();
        dishEntity.setUserId(userId);
        dishEntity.setId(dishId);
        dishEntity.setItems(dishItemList);

        TagEntity tag = new TagEntity();
        tag.setId(tagId);
        tag.setConversionId(conversionId);

        ArgumentCaptor<DishItemEntity> itemCaptor = ArgumentCaptor.forClass(DishItemEntity.class);
        ArgumentCaptor<DishEntity> dishCaptor = ArgumentCaptor.forClass(DishEntity.class);

        Mockito.when(dishRepository.findByDishIdForUser(userId, dishId))
                .thenReturn(Optional.of(dishEntity));
        Mockito.when(tagService.getTagById(tagId))
                .thenReturn(tag);
        Mockito.when(dishItemRepository.save(itemCaptor.capture()))
                .thenReturn(new DishItemEntity());
        Mockito.when(dishRepository.save(dishCaptor.capture()))
                .thenReturn(new DishEntity());

        dishService.updateIngredientInDish(userId, dishId, dishItemDto);


        DishEntity dishResult = dishCaptor.getValue();
        DishItemEntity ingredientResult = itemCaptor.getValue();
        Assertions.assertNotNull(ingredientResult);
        Assertions.assertNotNull(dishResult);
        // check dish result
        Assertions.assertEquals(1, dishResult.getItems().size());
        Assertions.assertEquals(dishResult.getItems().get(0).getTag(), tag);
        Assertions.assertEquals(1.0, dishResult.getItems().get(0).getQuantity(), 0.001);
        // check ingredient
        Assertions.assertNull(ingredientResult.getMarker());
        Assertions.assertEquals("medium", ingredientResult.getUnitSize());
        Assertions.assertNull(ingredientResult.getRawModifiers());
        Assertions.assertNotNull(dishResult.getItems().get(0).getTag().getConversionId());
        Assertions.assertNotNull(ingredientResult.getUnitId());
        Assertions.assertEquals(conversionId, dishResult.getItems().get(0).getTag().getConversionId());
        Assertions.assertEquals(ingredientResult.getWholeQuantity(), (Integer) 1);
        Assertions.assertNull(ingredientResult.getFractionalQuantity());
        Assertions.assertEquals(1, ingredientResult.getQuantity(), 0.001);
        Assertions.assertEquals(unitId, ingredientResult.getUnitId());
        Assertions.assertTrue(ingredientResult.getModifiersProcessed());
    }
    @Test
    void testUpdateIngredientInDish_ConversionNewMarkers() {
        Long userId = 99L;
        Long dishId = 999L;
        Long tagId = 9999L;
        Long unitId = 99999L;
        Long conversionId = 999999L;
        Long ingredientId = 9999999L;
        String rawModifiers = "chopped extra large";
        List<String> modifierTokens = List.of("chopped", "extra large");
        String marker = "chopped";
        String unitSize = "extra large";

        DishItemDTO dishItemDto = new DishItemDTO();
        dishItemDto.setTagId(tagId);
        dishItemDto.setWholeQuantity(1);
        dishItemDto.setUnitId(unitId);
        dishItemDto.setDishItemId(ingredientId);
        dishItemDto.setRawModifiers(modifierTokens);

        DishItemEntity existingIngredient = new DishItemEntity();
        existingIngredient.setDishItemId(ingredientId);
        existingIngredient.setWholeQuantity(11);
        existingIngredient.setFractionalQuantity(FractionType.FiveEighths);
        existingIngredient.setUnitId(1L);
        existingIngredient.setMarker("wild and crazy guy");
        existingIngredient.setUnitSize("unspecified");
        existingIngredient.setRawModifiers("present and accounted for");
        List<DishItemEntity> dishItemList = new ArrayList<>();
        dishItemList.add(existingIngredient);

        DishEntity dishEntity = new DishEntity();
        dishEntity.setUserId(userId);
        dishEntity.setId(dishId);
        dishEntity.setItems(dishItemList);

        TagEntity tag = new TagEntity();
        tag.setId(tagId);
        tag.setConversionId(conversionId);

        ArgumentCaptor<DishItemEntity> itemCaptor = ArgumentCaptor.forClass(DishItemEntity.class);
        ArgumentCaptor<DishEntity> dishCaptor = ArgumentCaptor.forClass(DishEntity.class);

        Mockito.when(dishRepository.findByDishIdForUser(userId, dishId))
                .thenReturn(Optional.of(dishEntity));
        Mockito.when(tagService.getTagById(tagId))
                .thenReturn(tag);
        Mockito.when(dishItemRepository.save(itemCaptor.capture()))
                .thenReturn(new DishItemEntity());
        Mockito.when(dishRepository.save(dishCaptor.capture()))
                .thenReturn(new DishEntity());
        Mockito.when(amountService.pullMarkersForModifers(modifierTokens, conversionId))
                .thenReturn(List.of(marker));
        Mockito.when(amountService.pullUnitSizesForModifiers(modifierTokens, conversionId))
                .thenReturn(List.of(unitSize));

        dishService.updateIngredientInDish(userId, dishId, dishItemDto);


        DishEntity dishResult = dishCaptor.getValue();
        DishItemEntity ingredientResult = itemCaptor.getValue();
        Assertions.assertNotNull(ingredientResult);
        Assertions.assertNotNull(dishResult);
        // check dish result
        Assertions.assertEquals(1, dishResult.getItems().size());
        Assertions.assertEquals(dishResult.getItems().get(0).getTag(), tag);
        Assertions.assertEquals(1.0, dishResult.getItems().get(0).getQuantity(), 0.001);
        // check ingredient
        Assertions.assertNotNull(ingredientResult.getMarker());
        Assertions.assertNotNull(ingredientResult.getUnitSize());
        Assertions.assertNotNull(ingredientResult.getRawModifiers());
        Assertions.assertNotNull(dishResult.getItems().get(0).getTag().getConversionId());
        Assertions.assertNotNull(ingredientResult.getUnitId());
        Assertions.assertNull(ingredientResult.getFractionalQuantity());
        Assertions.assertEquals(conversionId, dishResult.getItems().get(0).getTag().getConversionId());
        Assertions.assertEquals(ingredientResult.getWholeQuantity(), (Integer) 1);
        Assertions.assertEquals(marker, ingredientResult.getMarker());
        Assertions.assertEquals(unitSize, ingredientResult.getUnitSize());
        Assertions.assertEquals(FlatStringUtils.flattenListToString(modifierTokens,"|"), ingredientResult.getRawModifiers());
        Assertions.assertEquals(1.0, ingredientResult.getQuantity(), 0.001);
        Assertions.assertEquals(unitId, ingredientResult.getUnitId());
        Assertions.assertTrue(ingredientResult.getModifiersProcessed());    }

    @Test
    void testUpdateIngredientInDish_ConversionMarkers() {
        Long userId = 99L;
        Long dishId = 999L;
        Long tagId = 9999L;
        Long unitId = 99999L;
        Long conversionId = 999999L;
        String rawModifiers = "chopped extra large";
        List<String> modifierTokens = List.of("chopped", "extra large");
        String marker = "chopped";
        String unitSize = "extra large";

        DishItemDTO dishItemDto = new DishItemDTO();
        dishItemDto.setTagId(tagId);
        dishItemDto.setWholeQuantity(1);
        dishItemDto.setFractionalQuantity(FractionType.OneEighth);
        dishItemDto.setUnitId(unitId);
        dishItemDto.setRawModifiers(modifierTokens);

        DishEntity dishEntity = new DishEntity();
        dishEntity.setUserId(userId);
        dishEntity.setId(dishId);

        TagEntity tag = new TagEntity();
        tag.setId(tagId);
        tag.setConversionId(conversionId);

        ArgumentCaptor<DishItemEntity> itemCaptor = ArgumentCaptor.forClass(DishItemEntity.class);
        ArgumentCaptor<DishEntity> dishCaptor = ArgumentCaptor.forClass(DishEntity.class);

        Mockito.when(dishRepository.findByDishIdForUser(userId, dishId))
                .thenReturn(Optional.of(dishEntity));
        Mockito.when(tagService.getTagById(tagId))
                .thenReturn(tag);
        Mockito.when(dishItemRepository.save(itemCaptor.capture()))
                .thenReturn(new DishItemEntity());
        Mockito.when(dishRepository.save(dishCaptor.capture()))
                .thenReturn(new DishEntity());
        Mockito.when(amountService.pullMarkersForModifers(modifierTokens, conversionId))
                .thenReturn(List.of(marker));
        Mockito.when(amountService.pullUnitSizesForModifiers(modifierTokens, conversionId))
                .thenReturn(List.of(unitSize));

        dishService.addIngredientToDish(userId, dishId, dishItemDto);


        DishEntity dishResult = dishCaptor.getValue();
        DishItemEntity ingredientResult = itemCaptor.getValue();
        Assertions.assertNotNull(ingredientResult);
        Assertions.assertNotNull(dishResult);
        // check dish result
        Assertions.assertEquals(1, dishResult.getItems().size());
        Assertions.assertEquals(dishResult.getItems().get(0).getTag(), tag);
        Assertions.assertEquals(1.125, dishResult.getItems().get(0).getQuantity(), 0.001);
        // check ingredient
        Assertions.assertNotNull(ingredientResult.getMarker());
        Assertions.assertNotNull(ingredientResult.getUnitSize());
        Assertions.assertNotNull(ingredientResult.getRawModifiers());
        Assertions.assertNotNull(dishResult.getItems().get(0).getTag().getConversionId());
        Assertions.assertNotNull(ingredientResult.getUnitId());
        Assertions.assertNotNull(ingredientResult.getFractionalQuantity());
        Assertions.assertEquals(conversionId, dishResult.getItems().get(0).getTag().getConversionId());
        Assertions.assertEquals(ingredientResult.getWholeQuantity(), (Integer) 1);
        Assertions.assertEquals(FractionType.OneEighth, ingredientResult.getFractionalQuantity());
        Assertions.assertEquals(marker, ingredientResult.getMarker());
        Assertions.assertEquals(unitSize, ingredientResult.getUnitSize());
        Assertions.assertEquals(FlatStringUtils.flattenListToString(modifierTokens,"|"), ingredientResult.getRawModifiers());
        Assertions.assertEquals(1.125, ingredientResult.getQuantity(), 0.001);
        Assertions.assertEquals(unitId, ingredientResult.getUnitId());
        Assertions.assertTrue(ingredientResult.getModifiersProcessed());
    }

    @Test
    void testUpdateIngredientInDish_ConversionMarkersMultiple() {
        Long userId = 99L;
        Long dishId = 999L;
        Long tagId = 9999L;
        Long unitId = 99999L;
        Long conversionId = 999999L;
        String rawModifiers = "chopped extra large and other diced ignored";
        List<String> modifierTokens = List.of("chopped", "extra large", "and", "other", "diced", "ignored");
        String marker = "chopped";
        String unitSize = "extra large";

        DishItemDTO dishItemDto = new DishItemDTO();
        dishItemDto.setTagId(tagId);
        dishItemDto.setWholeQuantity(1);
        dishItemDto.setFractionalQuantity(FractionType.OneEighth);
        dishItemDto.setUnitId(unitId);
        dishItemDto.setRawModifiers(modifierTokens);

        DishEntity dishEntity = new DishEntity();
        dishEntity.setUserId(userId);
        dishEntity.setId(dishId);

        TagEntity tag = new TagEntity();
        tag.setId(tagId);
        tag.setConversionId(conversionId);

        ArgumentCaptor<DishItemEntity> itemCaptor = ArgumentCaptor.forClass(DishItemEntity.class);
        ArgumentCaptor<DishEntity> dishCaptor = ArgumentCaptor.forClass(DishEntity.class);

        Mockito.when(dishRepository.findByDishIdForUser(userId, dishId))
                .thenReturn(Optional.of(dishEntity));
        Mockito.when(tagService.getTagById(tagId))
                .thenReturn(tag);
        Mockito.when(dishItemRepository.save(itemCaptor.capture()))
                .thenReturn(new DishItemEntity());
        Mockito.when(dishRepository.save(dishCaptor.capture()))
                .thenReturn(new DishEntity());
        Mockito.when(amountService.pullMarkersForModifers(modifierTokens, conversionId))
                .thenReturn(List.of(marker, "diced"));
        Mockito.when(amountService.pullUnitSizesForModifiers(modifierTokens, conversionId))
                .thenReturn(List.of(unitSize));

        dishService.addIngredientToDish(userId, dishId, dishItemDto);


        DishEntity dishResult = dishCaptor.getValue();
        DishItemEntity ingredientResult = itemCaptor.getValue();
        Assertions.assertNotNull(ingredientResult);
        Assertions.assertNotNull(dishResult);
        // check dish result
        Assertions.assertEquals(1, dishResult.getItems().size());
        Assertions.assertEquals(dishResult.getItems().get(0).getTag(), tag);
        Assertions.assertEquals(1.125, dishResult.getItems().get(0).getQuantity(), 0.001);
        // check ingredient
        Assertions.assertNotNull(ingredientResult.getMarker());
        Assertions.assertNotNull(ingredientResult.getUnitSize());
        Assertions.assertNotNull(ingredientResult.getRawModifiers());
        Assertions.assertNotNull(dishResult.getItems().get(0).getTag().getConversionId());
        Assertions.assertNotNull(ingredientResult.getUnitId());
        Assertions.assertNotNull(ingredientResult.getFractionalQuantity());
        Assertions.assertEquals(conversionId, dishResult.getItems().get(0).getTag().getConversionId());
        Assertions.assertEquals(ingredientResult.getWholeQuantity(), (Integer) 1);
        Assertions.assertEquals(FractionType.OneEighth, ingredientResult.getFractionalQuantity());
        Assertions.assertEquals(marker, ingredientResult.getMarker());
        Assertions.assertEquals(unitSize, ingredientResult.getUnitSize());
        Assertions.assertEquals(FlatStringUtils.flattenListToString(modifierTokens,"|"), ingredientResult.getRawModifiers());
        Assertions.assertEquals(1.125, ingredientResult.getQuantity(), 0.001);
        Assertions.assertEquals(unitId, ingredientResult.getUnitId());
        Assertions.assertTrue(ingredientResult.getModifiersProcessed());
    }

    @Test
    void testGetDishForV2Display_IngredientsOnly() {
        Long userId = 99L;
        Long dishId = 999L;

        DishEntity dish = new DishEntity();
        dish.setId(dishId);

        Long ingredientId1 = 1L;
        Long ingredientTagId1 = 11L;
        Integer wholeAmount1 = 1;
        FractionType fractionalAmount1 = FractionType.SevenEighths;
        Long unitId1 = 1L;
        Long ingredientId2 = 2L;
        Long ingredientTagId2 = 22L;
        Integer wholeAmount2 = 2;
        FractionType fractionalAmount2 = FractionType.OneHalf;
        Long unitId2 = 1L;
        Long ingredientId3 = 3L;
        Long ingredientTagId3 = 33L;
        Integer wholeAmount3 = 3;
        FractionType fractionalAmount3 = null;
        Long unitId3 = 1L;

        DishItemDTO ingredient1 = buildIngredient(ingredientId1, ingredientTagId1, wholeAmount1, fractionalAmount1, unitId1);
        DishItemDTO ingredient2 = buildIngredient(ingredientId2, ingredientTagId2, wholeAmount2, fractionalAmount2, unitId2);
        DishItemDTO ingredient3 = buildIngredient(ingredientId3, ingredientTagId3, wholeAmount3, fractionalAmount3, unitId3);
        List<DishItemDTO> ingredients = Arrays.asList(ingredient1, ingredient2, ingredient3);

        RatingUpdateInfo updateInfo = ServiceTestUtils.buildDummyRatingUpdateInfo();

        Mockito.when(dishRepository.findByDishIdForUser(userId, dishId))
                        .thenReturn(Optional.of(dish));
        Mockito.when(dishItemRepository.getIngredientsForDish(dishId))
                        .thenReturn(ingredients);
        Mockito.when(tagService.getRatingUpdateInfoForDishIds(Collections.singletonList(dishId)))
                        .thenReturn(updateInfo);

        DishDTO result = dishService.getDishForV2Display(userId, dishId);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getIngredients());
        Assertions.assertTrue(result.getTags().isEmpty());
        Assertions.assertNotNull(result.getRatings());

        Map<Long,DishItemDTO> ingredientMap = result.getIngredients().stream()
                .collect(Collectors.toMap( r -> r.getDishItemId(),
                        r -> r));
        Assertions.assertNotNull(ingredientMap);
        // check fraction display
        assertFractionDisplay(ingredientMap.get(ingredientId1), "7/8");
        assertFractionDisplay(ingredientMap.get(ingredientId2), "1/2");
        assertFractionDisplay(ingredientMap.get(ingredientId3), null);
        /*
        DishEntity dishResult = dishCaptor.getValue();
        DishItemEntity ingredientResult = itemCaptor.getValue();
        Assert.assertNotNull(ingredientResult);
        // check dish result
        Assert.assertEquals(1, dishResult.getItems().size());
        Assert.assertEquals(dishResult.getItems().get(0).getTag(), tag);
        Assert.assertEquals(1.125, dishResult.getItems().get(0).getQuantity(), 0.001);
        // check ingredient
        Assert.assertNotNull(ingredientResult.getMarker());
        Assert.assertNotNull(ingredientResult.getUnitSize());
        Assert.assertNotNull(ingredientResult.getRawModifiers());
        Assert.assertNotNull(dishResult.getItems().get(0).getTag().getConversionId());
        Assert.assertNotNull(ingredientResult.getUnitId());
        Assert.assertNotNull(ingredientResult.getFractionalQuantity());
        Assert.assertEquals(conversionId, dishResult.getItems().get(0).getTag().getConversionId());
        Assert.assertEquals(ingredientResult.getWholeQuantity(), (Integer) 1);
        Assert.assertEquals(FractionType.OneEighth, ingredientResult.getFractionalQuantity());
        Assert.assertEquals(marker, ingredientResult.getMarker());
        Assert.assertEquals(unitSize, ingredientResult.getUnitSize());
        Assert.assertEquals(rawModifiers, ingredientResult.getRawModifiers());
        Assert.assertEquals(1.125, ingredientResult.getQuantity(), 0.001);
        Assert.assertEquals(unitId, ingredientResult.getUnitId());
        Assert.assertTrue(ingredientResult.getModifiersProcessed());
        */

    }

    private void assertFractionDisplay(DishItemDTO dishItemDTO, String value) {
        if (value == null) {
            Assertions.assertNull(dishItemDTO.getFractionDisplay());
            return;
        }
        Assertions.assertEquals(dishItemDTO.getFractionDisplay(), value);
    }

    private DishItemDTO buildIngredient(Long ingredientId, Long ingredientTagId1, Integer wholeAmount1, FractionType fractionType, Long unitId1) {
        DishItemDTO dishItemDTO = new DishItemDTO();
        dishItemDTO.setDishItemId(ingredientId);
        dishItemDTO.setTagId(ingredientTagId1);
        dishItemDTO.setTagDisplay(String.valueOf(ingredientTagId1));
        dishItemDTO.setWholeQuantity(wholeAmount1);
        dishItemDTO.setFractionalQuantity(fractionType);
        dishItemDTO.setUnitId(unitId1);
        return dishItemDTO;
    }


}
