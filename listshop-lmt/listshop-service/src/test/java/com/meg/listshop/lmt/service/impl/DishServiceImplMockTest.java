package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.auth.data.repository.UserRepository;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.model.FractionType;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.DishItemEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.pojos.DishItemDTO;
import com.meg.listshop.lmt.data.repository.DishItemRepository;
import com.meg.listshop.lmt.data.repository.DishRepository;
import com.meg.listshop.lmt.service.DishService;
import com.meg.listshop.lmt.service.food.AmountService;
import com.meg.listshop.lmt.service.tag.AutoTagService;
import com.meg.listshop.lmt.service.tag.TagService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class DishServiceImplMockTest {

    private DishService dishService;


    @MockBean
    private DishRepository dishRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AutoTagService autoTagService;
    @MockBean
    private TagService tagService;
    @MockBean
    private DishItemRepository dishItemRepository;
    @MockBean
    private AmountService amountService;


    @Before
    public void setUp() {

        dishService = new DishServiceImpl(dishRepository,
                userRepository,
                autoTagService,
                tagService,
                dishItemRepository,
                amountService
        );
    }


    @Test
    public void testCreateDish() {
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
        Assert.assertEquals(testDishName, dishSaved.getDishName());
        Assert.assertEquals(Long.valueOf(99L), dishSaved.getUserId());
        Assert.assertEquals("reference", dishSaved.getReference());
    }

    @Test
    public void testCreateDish_ExistingName() {
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
        Assert.assertEquals(testDishName + " " + 4, dishSaved.getDishName());
        Assert.assertEquals(Long.valueOf(99L), dishSaved.getUserId());

    }

    @Test
    public void testGetDishesToAutotag() {
        Long statusFlag = 105L;
        int dishLimit = 15;

        Pageable expectedPageable = PageRequest.of(0, dishLimit);

        dishService.getDishesToAutotag(statusFlag, dishLimit);

        Mockito.verify(dishRepository, Mockito.times(1)).findDishesToAutotag(statusFlag, expectedPageable);


    }

    @Test
    public void testAddIngredientToDish_Simple() {
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
        Assert.assertNotNull(ingredientResult);
        Assert.assertNotNull(dishResult);
        // check dish result
        Assert.assertEquals(1, dishResult.getItems().size());
        Assert.assertEquals(dishResult.getItems().get(0).getTag(), tag);
        Assert.assertEquals(1.5, dishResult.getItems().get(0).getQuantity(), 0.001);
        // check ingredient
        Assert.assertNull(ingredientResult.getMarker());
        Assert.assertNull(ingredientResult.getUnitSize());
        Assert.assertNull(ingredientResult.getRawModifiers());
        Assert.assertEquals(ingredientResult.getWholeQuantity(), (Integer) 1);
        Assert.assertEquals(FractionType.OneHalf, ingredientResult.getFractionalQuantity());
        Assert.assertEquals(1.5, ingredientResult.getQuantity(), 0.001);
        Assert.assertEquals(unitId, ingredientResult.getUnitId());
        Assert.assertNotNull(ingredientResult.getUnitId());
        Assert.assertFalse(ingredientResult.getModifiersProcessed());
        Assert.assertNull(dishResult.getItems().get(0).getTag().getConversionId());
    }

    @Test
    public void testAddIngredientToDish_ConversionIdNoMarkers() {
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
        Assert.assertNotNull(ingredientResult);
        Assert.assertNotNull(dishResult);
        // check dish result
        Assert.assertEquals(1, dishResult.getItems().size());
        Assert.assertEquals(dishResult.getItems().get(0).getTag(), tag);
        Assert.assertEquals(1.0, dishResult.getItems().get(0).getQuantity(), 0.001);
        // check ingredient
        Assert.assertNull(ingredientResult.getMarker());
        Assert.assertNull(ingredientResult.getUnitSize());
        Assert.assertNull(ingredientResult.getRawModifiers());
        Assert.assertNotNull(dishResult.getItems().get(0).getTag().getConversionId());
        Assert.assertNotNull(ingredientResult.getUnitId());
        Assert.assertEquals(conversionId, dishResult.getItems().get(0).getTag().getConversionId());
        Assert.assertEquals(ingredientResult.getWholeQuantity(), (Integer) 1);
        Assert.assertNull(ingredientResult.getFractionalQuantity());
        Assert.assertEquals(1, ingredientResult.getQuantity(), 0.001);
        Assert.assertEquals(unitId, ingredientResult.getUnitId());
        Assert.assertTrue(ingredientResult.getModifiersProcessed());
    }

    @Test
    public void testAddIngredientToDish_ConversionMarkers() {
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
        dishItemDto.setRawModifiers(rawModifiers);

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
        Mockito.when(amountService.pullModifierTokens(rawModifiers))
                .thenReturn(modifierTokens);
        Mockito.when(amountService.pullMarkersForModifers(modifierTokens, conversionId))
                .thenReturn(List.of(marker));
        Mockito.when(amountService.pullUnitSizesForModifiers(modifierTokens, conversionId))
                .thenReturn(List.of(unitSize));

        dishService.addIngredientToDish(userId, dishId, dishItemDto);


        DishEntity dishResult = dishCaptor.getValue();
        DishItemEntity ingredientResult = itemCaptor.getValue();
        Assert.assertNotNull(ingredientResult);
        Assert.assertNotNull(dishResult);
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
    }

    @Test
    public void testAddIngredientToDish_ConversionMarkersMultiple() {
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
        dishItemDto.setRawModifiers(rawModifiers);

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
        Mockito.when(amountService.pullModifierTokens(rawModifiers))
                .thenReturn(modifierTokens);
        Mockito.when(amountService.pullMarkersForModifers(modifierTokens, conversionId))
                .thenReturn(List.of(marker, "diced"));
        Mockito.when(amountService.pullUnitSizesForModifiers(modifierTokens, conversionId))
                .thenReturn(List.of(unitSize));

        dishService.addIngredientToDish(userId, dishId, dishItemDto);


        DishEntity dishResult = dishCaptor.getValue();
        DishItemEntity ingredientResult = itemCaptor.getValue();
        Assert.assertNotNull(ingredientResult);
        Assert.assertNotNull(dishResult);
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
    }


    @Test(expected = ObjectNotFoundException.class)
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

        ArgumentCaptor<DishItemEntity> itemCaptor = ArgumentCaptor.forClass(DishItemEntity.class);
        ArgumentCaptor<DishEntity> dishCaptor = ArgumentCaptor.forClass(DishEntity.class);

        Mockito.when(dishRepository.findByDishIdForUser(userId, dishId))
                .thenReturn(Optional.of(dishEntity));
        Mockito.when(tagService.getTagById(tagId))
                .thenReturn(tag);

        dishService.updateIngredientInDish(userId, dishId, dishItemDto);
    }

    @Test(expected = ObjectNotFoundException.class)
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
        Mockito.when(tagService.getTagById(tagId))
                .thenReturn(tag);

        dishService.updateIngredientInDish(userId, dishId, dishItemDto);
    }

    @Test(expected = ObjectNotFoundException.class)
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
    }

    @Test
    public void testUpdateIngredientInDish_Simple() {
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
        Assert.assertNotNull(ingredientResult);
        Assert.assertNotNull(dishResult);
        // check dish result
        Assert.assertEquals(1, dishResult.getItems().size());
        Assert.assertEquals(dishResult.getItems().get(0).getTag(), tag);
        Assert.assertEquals(1.0, dishResult.getItems().get(0).getQuantity(), 0.001);
        // check ingredient
        Assert.assertNull(ingredientResult.getMarker());
        Assert.assertNull(ingredientResult.getUnitSize());
        Assert.assertNull(ingredientResult.getRawModifiers());
        Assert.assertNotNull(dishResult.getItems().get(0).getTag().getConversionId());
        Assert.assertNotNull(ingredientResult.getUnitId());
        Assert.assertEquals(conversionId, dishResult.getItems().get(0).getTag().getConversionId());
        Assert.assertEquals(ingredientResult.getWholeQuantity(), (Integer) 1);
        Assert.assertNull(ingredientResult.getFractionalQuantity());
        Assert.assertEquals(1, ingredientResult.getQuantity(), 0.001);
        Assert.assertEquals(unitId, ingredientResult.getUnitId());
        Assert.assertTrue(ingredientResult.getModifiersProcessed());
    }


    @Test
    public void testUpdateIngredientInDish_ConversionIdNoMarkers() {
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
        Assert.assertNotNull(ingredientResult);
        Assert.assertNotNull(dishResult);
        // check dish result
        Assert.assertEquals(1, dishResult.getItems().size());
        Assert.assertEquals(dishResult.getItems().get(0).getTag(), tag);
        Assert.assertEquals(1.0, dishResult.getItems().get(0).getQuantity(), 0.001);
        // check ingredient
        Assert.assertNull(ingredientResult.getMarker());
        Assert.assertNull(ingredientResult.getUnitSize());
        Assert.assertNull(ingredientResult.getRawModifiers());
        Assert.assertNotNull(dishResult.getItems().get(0).getTag().getConversionId());
        Assert.assertNotNull(ingredientResult.getUnitId());
        Assert.assertEquals(conversionId, dishResult.getItems().get(0).getTag().getConversionId());
        Assert.assertEquals(ingredientResult.getWholeQuantity(), (Integer) 1);
        Assert.assertNull(ingredientResult.getFractionalQuantity());
        Assert.assertEquals(1, ingredientResult.getQuantity(), 0.001);
        Assert.assertEquals(unitId, ingredientResult.getUnitId());
        Assert.assertTrue(ingredientResult.getModifiersProcessed());
    }
    @Test
    public void testUpdateIngredientInDish_ConversionNewMarkers() {
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
        dishItemDto.setRawModifiers(rawModifiers);

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
        Mockito.when(amountService.pullModifierTokens(rawModifiers))
                .thenReturn(modifierTokens);
        Mockito.when(amountService.pullMarkersForModifers(modifierTokens, conversionId))
                .thenReturn(List.of(marker));
        Mockito.when(amountService.pullUnitSizesForModifiers(modifierTokens, conversionId))
                .thenReturn(List.of(unitSize));

        dishService.updateIngredientInDish(userId, dishId, dishItemDto);


        DishEntity dishResult = dishCaptor.getValue();
        DishItemEntity ingredientResult = itemCaptor.getValue();
        Assert.assertNotNull(ingredientResult);
        Assert.assertNotNull(dishResult);
        // check dish result
        Assert.assertEquals(1, dishResult.getItems().size());
        Assert.assertEquals(dishResult.getItems().get(0).getTag(), tag);
        Assert.assertEquals(1.0, dishResult.getItems().get(0).getQuantity(), 0.001);
        // check ingredient
        Assert.assertNotNull(ingredientResult.getMarker());
        Assert.assertNotNull(ingredientResult.getUnitSize());
        Assert.assertNotNull(ingredientResult.getRawModifiers());
        Assert.assertNotNull(dishResult.getItems().get(0).getTag().getConversionId());
        Assert.assertNotNull(ingredientResult.getUnitId());
        Assert.assertNull(ingredientResult.getFractionalQuantity());
        Assert.assertEquals(conversionId, dishResult.getItems().get(0).getTag().getConversionId());
        Assert.assertEquals(ingredientResult.getWholeQuantity(), (Integer) 1);
        Assert.assertEquals(marker, ingredientResult.getMarker());
        Assert.assertEquals(unitSize, ingredientResult.getUnitSize());
        Assert.assertEquals(rawModifiers, ingredientResult.getRawModifiers());
        Assert.assertEquals(1.0, ingredientResult.getQuantity(), 0.001);
        Assert.assertEquals(unitId, ingredientResult.getUnitId());
        Assert.assertTrue(ingredientResult.getModifiersProcessed());    }

    @Test
    public void testUpdateIngredientInDish_ConversionMarkers() {
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
        dishItemDto.setRawModifiers(rawModifiers);

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
        Mockito.when(amountService.pullModifierTokens(rawModifiers))
                .thenReturn(modifierTokens);
        Mockito.when(amountService.pullMarkersForModifers(modifierTokens, conversionId))
                .thenReturn(List.of(marker));
        Mockito.when(amountService.pullUnitSizesForModifiers(modifierTokens, conversionId))
                .thenReturn(List.of(unitSize));

        dishService.addIngredientToDish(userId, dishId, dishItemDto);


        DishEntity dishResult = dishCaptor.getValue();
        DishItemEntity ingredientResult = itemCaptor.getValue();
        Assert.assertNotNull(ingredientResult);
        Assert.assertNotNull(dishResult);
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
    }

    @Test
    public void testUpdateIngredientInDish_ConversionMarkersMultiple() {
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
        dishItemDto.setRawModifiers(rawModifiers);

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
        Mockito.when(amountService.pullModifierTokens(rawModifiers))
                .thenReturn(modifierTokens);
        Mockito.when(amountService.pullMarkersForModifers(modifierTokens, conversionId))
                .thenReturn(List.of(marker, "diced"));
        Mockito.when(amountService.pullUnitSizesForModifiers(modifierTokens, conversionId))
                .thenReturn(List.of(unitSize));

        dishService.addIngredientToDish(userId, dishId, dishItemDto);


        DishEntity dishResult = dishCaptor.getValue();
        DishItemEntity ingredientResult = itemCaptor.getValue();
        Assert.assertNotNull(ingredientResult);
        Assert.assertNotNull(dishResult);
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
    }

}