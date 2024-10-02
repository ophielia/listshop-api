package com.meg.listshop.lmt.service.food.impl;

import com.meg.listshop.common.UnitType;
import com.meg.listshop.common.data.entity.UnitEntity;
import com.meg.listshop.conversion.data.pojo.ConversionSampleDTO;
import com.meg.listshop.conversion.data.pojo.SimpleAmount;
import com.meg.listshop.common.data.repository.UnitRepository;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.service.ConversionService;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.data.pojos.TagInfoDTO;
import com.meg.listshop.lmt.data.pojos.TagSearchCriteria;
import com.meg.listshop.lmt.data.repository.FoodCategoryMappingRepository;
import com.meg.listshop.lmt.data.repository.FoodCategoryRepository;
import com.meg.listshop.lmt.data.repository.FoodConversionRepository;
import com.meg.listshop.lmt.data.repository.FoodRepository;
import com.meg.listshop.lmt.service.food.FoodService;
import com.meg.listshop.lmt.service.tag.TagService;
import com.meg.listshop.lmt.service.tag.TagStructureService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class FoodServiceImplMockTest {
    private FoodService foodService;

    @MockBean
    FoodCategoryMappingRepository foodCategoryMappingRepo;
    @MockBean
    FoodRepository foodRepository;

    @MockBean
    FoodCategoryRepository foodCategoryRepository;
    @MockBean
    TagService tagService;
    @MockBean
    TagStructureService tagStructureService;

    @MockBean
    FoodConversionRepository foodConversionRepository;

    @MockBean
    ConversionService conversionService;

    @MockBean
    UnitRepository unitRepository;

    List<FoodCategoryMappingEntity> allMappedCategories;
    List<FoodCategoryEntity> allCategories;

    List<TagInfoDTO> allSearchTags;

    Map<Long, UnitEntity> testUnitLookups = new HashMap<>();
    private Long GRAM_ID = 1013L;
    private Long SINGLE_UNIT_ID = 1011L;
    private Long CUP_ID = 1000L;
    private Long TABLESPOON_ID = 1001L;
    private Long TEASPOON_ID = 1002L;


    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        this.foodService = new FoodServiceImpl(
                foodCategoryMappingRepo, foodRepository, foodCategoryRepository,
                tagService, tagStructureService,
                foodConversionRepository, conversionService,
                unitRepository
        );

        // set single unit id
        Field nameField = this.foodService.getClass()
                .getDeclaredField("SINGLE_UNIT_ID");
        nameField.setAccessible(true);
        nameField.set(this.foodService, SINGLE_UNIT_ID);

        // set gram unit id
        Field gramField = this.foodService.getClass()
                .getDeclaredField("GRAM_UNIT_ID");
        gramField.setAccessible(true);
        gramField.set(this.foodService, GRAM_ID);

        // set generic ids
        Set<Long> genericIds = new HashSet<>();
        genericIds.add(1000L);
        genericIds.add(1001L);
        genericIds.add(1002L);
        Field genericField = this.foodService.getClass()
                .getDeclaredField("GENERIC_IDS");
        genericField.setAccessible(true);
        genericField.set(this.foodService, genericIds);

        allSearchTags = new ArrayList<>();
        allSearchTags.add(buildTagInfoDTO(1L, "searchTag1", 2L));
        allSearchTags.add(buildTagInfoDTO(2L, "searchTag2", 3L));
        allSearchTags.add(buildTagInfoDTO(3L, "searchTag3", 4L));
        allSearchTags.add(buildTagInfoDTO(4L, "searchTag4", null));

        allMappedCategories = new ArrayList<>();
        allMappedCategories.add(buildMapping(2L, 22L));
        allMappedCategories.add(buildMapping(3L, 33L));
        allMappedCategories.add(buildMapping(4L, 44L));

        allCategories = new ArrayList<>();
        allCategories.add(buildCategory(22L, "category2"));
        allCategories.add(buildCategory(33L, "category3"));
        allCategories.add(buildCategory(44L, "category4"));

        testUnitLookups.put(CUP_ID, buildUnit(CUP_ID, "cup"));
        testUnitLookups.put(TABLESPOON_ID, buildUnit(TABLESPOON_ID, "tablespoon"));
        testUnitLookups.put(TEASPOON_ID, buildUnit(TEASPOON_ID, "teaspoon"));
        testUnitLookups.put(GRAM_ID, buildUnit(GRAM_ID, "gram"));
        UnitEntity singleUnit = buildUnit(SINGLE_UNIT_ID, "unit");
        singleUnit.setType(UnitType.UNIT);
        testUnitLookups.put(SINGLE_UNIT_ID, singleUnit);
    }


    @Test
    void testCategoryMatchForTag() {
        Long tagId = 1L;
        Long categoryId = 22L;
        FoodCategoryEntity expectedCategory = allCategories.stream().filter(c -> c.getId().equals(categoryId)).findFirst().orElse(null);
        List<Long> tagIds = allSearchTags.stream().map(TagInfoDTO::getTagId).collect(Collectors.toList());

        Mockito.when(foodCategoryMappingRepo.findFoodCategoryMappingEntityByTagIdIn(tagIds)).thenReturn(allMappedCategories);
        Mockito.when(foodCategoryRepository.findById(categoryId)).thenReturn(Optional.of(expectedCategory));

        FoodCategoryEntity result = foodService.getCategoryMatchForTag(tagId, allSearchTags);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(22L, result.getId(), "category should be 22");

    }

    @Test
    void testCategoryMatchForTagSomeMappings() {
        Long tagId = 1L;
        Long categoryId = 44L;
        FoodCategoryEntity expectedCategory = allCategories.stream().filter(c -> c.getId().equals(categoryId)).findFirst().orElse(null);
        List<Long> tagIds = allSearchTags.stream().map(TagInfoDTO::getTagId).collect(Collectors.toList());

        List<FoodCategoryMappingEntity> onlyTop = allMappedCategories.stream().filter(m -> m.getTagId().equals(4L))
                .collect(Collectors.toList());
        Mockito.when(foodCategoryMappingRepo.findFoodCategoryMappingEntityByTagIdIn(tagIds)).thenReturn(onlyTop);
        Mockito.when(foodCategoryRepository.findById(categoryId)).thenReturn(Optional.of(expectedCategory));
        FoodCategoryEntity result = foodService.getCategoryMatchForTag(tagId, allSearchTags);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(44L, result.getId(), "category should be 44");
    }

    @Test
    void testCategoryMatchNoMappings() {
        Long tagId = 1L;
        List<Long> tagIds = allSearchTags.stream().map(TagInfoDTO::getTagId).collect(Collectors.toList());

        Mockito.when(foodCategoryMappingRepo.findFoodCategoryMappingEntityByTagIdIn(tagIds)).thenReturn(new ArrayList<>());
        FoodCategoryEntity result = foodService.getCategoryMatchForTag(tagId, allSearchTags);
        Assertions.assertNull(result);

    }

    @Test
    void testGetSuggestedFoods() {
        Long tagId = 1L;
        Long categoryId = 99L;
        Long otherCategoryId = 999L;
        Long replacementTagId = 100L;
        TagEntity tag = new TagEntity();
        tag.setId(tagId);
        tag.setName("tag1");
        tag.setReplacementTagId(replacementTagId);

        List<TagEntity> ascendantTags = new ArrayList<>();
        ascendantTags.add(buildTagEntity(2L, "tag2"));
        ascendantTags.add(buildTagEntity(3L, "tag3"));
        ascendantTags.add(buildTagEntity(4L, "tag4"));

        TagSearchCriteria searchCriteria = new TagSearchCriteria();
        searchCriteria.setTagIds(ascendantTags.stream().map(TagEntity::getId).collect(Collectors.toList()));

        List<TagInfoDTO> tagInfoList = new ArrayList<>();
        tagInfoList.add(buildTagInfoDTO(1L, "tag1", 2L));
        tagInfoList.add(buildTagInfoDTO(2L, "tag2", 3L));
        tagInfoList.add(buildTagInfoDTO(3L, "tag3", 4L));
        tagInfoList.add(buildTagInfoDTO(4L, "tag4", null));

        FoodCategoryEntity foundCategory = new FoodCategoryEntity();
        foundCategory.setId(categoryId);


        List<FoodEntity> categoryFoods = new ArrayList<>();
        categoryFoods.add(buildFood(1L, "food1", otherCategoryId));
        categoryFoods.add(buildFood(2L, "food2", otherCategoryId));
        categoryFoods.add(buildFood(33L, "food33", otherCategoryId));
        categoryFoods.add(buildFood(44L, "food44", categoryId));
        categoryFoods.add(buildFood(55L, "food55", categoryId));

        TagEntity replaceTag = new TagEntity();
        replaceTag.setId(replacementTagId);

        Mockito.when(tagService.getTagById(tagId))
                .thenReturn(tag);

        Mockito.when(tagStructureService.getAscendantTags(tag))
                .thenReturn(ascendantTags);

        Mockito.when(tagService.getTagInfoList(any(TagSearchCriteria.class)))
                .thenReturn(tagInfoList);

        Mockito.when(foodCategoryMappingRepo.findFoodCategoryMappingEntityByTagIdIn(any(List.class)))
                .thenReturn(allMappedCategories);

        List<FoodCategoryMappingEntity> partialMappings = Collections.singletonList(buildMapping(4L, categoryId));
        Mockito.when(foodCategoryMappingRepo.findFoodCategoryMappingEntityByTagIdIn(any(List.class)))
                .thenReturn(partialMappings);


        Mockito.when(foodCategoryRepository.findById(categoryId))
                .thenReturn(Optional.of(foundCategory));
        Mockito.when(foodRepository.findFoodMatches("%" + tag.getName().toLowerCase() + "%"))
                .thenReturn(categoryFoods);


        // call to be tested
        List<FoodEntity> result = foodService.getSuggestedFoods(tagId, null);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(5, result.size(), "expect 5 results");
        for (int i = 0; i < 2; i++) {
            Assertions.assertEquals(categoryId, result.get(i).getCategoryId(), "first 2 should be the right category id");
        }
        for (int i = 2; i < 5; i++) {
            Assertions.assertEquals(otherCategoryId, result.get(i).getCategoryId(), "first 2 should be the right category id");
        }
    }

    @Test
    void testAddOrUpdateFoodForTag() {
        Long tagId = 99L;
        Long foodId = 999L;
        Long conversionId = 9999L;
        Long referenceId = 99999L;
        double conversionAmount = 3.0;
        Long conversionUnit = 3333L;
        double conversionGramWeight = 150.0;

        TagEntity tagNoFood = new TagEntity(tagId);
        tagNoFood.setConversionId(null);

        FoodConversionEntity conversionEntity = new FoodConversionEntity();
        conversionEntity.setAmount(conversionAmount);
        conversionEntity.setUnitId(conversionUnit);
        conversionEntity.setGramWeight(conversionGramWeight);
        conversionEntity.setId(referenceId);

        FoodEntity foodEntity = new FoodEntity();
        foodEntity.setFoodId(foodId);
        foodEntity.setConversionId(conversionId);

        ArgumentCaptor<TagEntity> tagCaptor = ArgumentCaptor.forClass(TagEntity.class);
        Mockito.when(tagService.getTagById(tagId)).thenReturn(tagNoFood);
        Mockito.when(foodRepository.findById(foodId)).thenReturn(Optional.of(foodEntity));
        Mockito.when(foodConversionRepository.findAllByConversionId(conversionId))
                .thenReturn(Collections.singletonList(conversionEntity));
        Mockito.doNothing().when(conversionService).saveConversionFactors(conversionId, Collections.singletonList(conversionEntity));
        Mockito.when(tagService.updateTag(Mockito.eq(tagId), tagCaptor.capture())).thenReturn(tagNoFood);

        // call under test
        foodService.addOrUpdateFoodForTag(tagId, foodId, true);

        Mockito.verify(conversionService).saveConversionFactors(conversionId, Collections.singletonList(conversionEntity));

        TagEntity saved = tagCaptor.getValue();
        Assertions.assertNotNull(saved, "should have captured value");
        Assertions.assertEquals(conversionId, saved.getConversionId(), "conversion id should match tag conversion id");
        Assertions.assertEquals(true, (saved.getInternalStatus() % 7) == 0, "should have correct internal status");
        Assertions.assertNull(saved.getMarker(), "marker was not set");
    }


    @Test
    void testAddOrUpdateFoodForTagWithMarker() {
        Long tagId = 99L;
        Long foodId = 999L;
        Long conversionId = 9999L;
        Long referenceId = 99999L;
        String marker = "marker";
        double conversionAmount = 3.0;
        Long conversionUnit = 3333L;
        double conversionGramWeight = 150.0;

        TagEntity tagNoFood = new TagEntity(tagId);
        tagNoFood.setConversionId(null);

        FoodConversionEntity conversionEntity = new FoodConversionEntity();
        conversionEntity.setAmount(conversionAmount);
        conversionEntity.setUnitId(conversionUnit);
        conversionEntity.setGramWeight(conversionGramWeight);
        conversionEntity.setId(referenceId);

        FoodEntity foodEntity = new FoodEntity();
        foodEntity.setFoodId(foodId);
        foodEntity.setConversionId(conversionId);
        foodEntity.setMarker(marker);

        ArgumentCaptor<TagEntity> tagCaptor = ArgumentCaptor.forClass(TagEntity.class);
        Mockito.when(tagService.getTagById(tagId)).thenReturn(tagNoFood);
        Mockito.when(foodRepository.findById(foodId)).thenReturn(Optional.of(foodEntity));
        Mockito.when(foodConversionRepository.findAllByConversionId(conversionId))
                .thenReturn(Collections.singletonList(conversionEntity));
        Mockito.doNothing().when(conversionService).saveConversionFactors(conversionId, Collections.singletonList(conversionEntity));
        Mockito.when(tagService.updateTag(Mockito.eq(tagId), tagCaptor.capture())).thenReturn(tagNoFood);

        // call under test
        foodService.addOrUpdateFoodForTag(tagId, foodId, true);

        Mockito.verify(conversionService).saveConversionFactors(conversionId, Collections.singletonList(conversionEntity));

        TagEntity saved = tagCaptor.getValue();
        Assertions.assertNotNull(saved, "should have captured value");
        Assertions.assertEquals(conversionId, saved.getConversionId(), "conversion id should match tag conversion id");
        Assertions.assertEquals(true, (saved.getInternalStatus() % 7) == 0, "should have correct internal status");
        Assertions.assertNotNull(saved.getMarker(), "marker was not set");
        Assertions.assertEquals(marker, saved.getMarker(), "marker was not set");
    }

    @Test
    void testSamplesForConversionIdSimple() throws ConversionPathException, ConversionFactorException {
        Long conversionId = 12345L;
        Boolean isLiquid = false;
        Long fromUnitId = TABLESPOON_ID;

        FoodConversionEntity teaspoonFactor = buildFoodConversionFactor(conversionId, fromUnitId, null, null);
        Set<Long> unitIdsSearch = new HashSet<>();
        unitIdsSearch.add(TABLESPOON_ID);
        unitIdsSearch.add(TEASPOON_ID);
        unitIdsSearch.add(CUP_ID);
        unitIdsSearch.add(SINGLE_UNIT_ID);
        List<UnitEntity> foundUnits = unitIdsSearch.stream()
                .map(s -> testUnitLookups.get(s))
                .collect(Collectors.toList());

        SimpleAmount toConvertTeaspoon = new SimpleAmount(1.0, testUnitLookups.get(TEASPOON_ID), conversionId, false, null);
        SimpleAmount toConvertTablespoon = new SimpleAmount(1.0, testUnitLookups.get(TABLESPOON_ID), conversionId, false, null);
        SimpleAmount toConvertCup = new SimpleAmount(1.0, testUnitLookups.get(CUP_ID), conversionId, false, null);


        Mockito.when(foodConversionRepository.findAllByConversionId(conversionId))
                .thenReturn(Collections.singletonList(teaspoonFactor));
        HashSet<Long> fromFactorUnitIds = new HashSet<>();
        fromFactorUnitIds.add(TEASPOON_ID);
        Mockito.when(unitRepository.findIntegralUnits(fromFactorUnitIds))
                .thenReturn(new HashSet<Long>());
        Mockito.when(unitRepository.findAllById(unitIdsSearch))
                .thenReturn(foundUnits);
        Mockito.when(unitRepository.findById(GRAM_ID))
                .thenReturn(Optional.of(testUnitLookups.get(GRAM_ID)));

        Mockito.when(conversionService.convertToUnit(toConvertTeaspoon, testUnitLookups.get(GRAM_ID), null))
                .thenReturn(dummyConvert(testUnitLookups.get(GRAM_ID), null));
        Mockito.when(conversionService.convertToUnit(toConvertTablespoon, testUnitLookups.get(GRAM_ID), null))
                .thenReturn(dummyConvert(testUnitLookups.get(GRAM_ID), null));
        Mockito.when(conversionService.convertToUnit(toConvertCup, testUnitLookups.get(GRAM_ID), null))
                .thenReturn(dummyConvert(testUnitLookups.get(GRAM_ID), null));


        List<ConversionSampleDTO> result = foodService.samplesForConversionId(conversionId, isLiquid);

        Assertions.assertNotNull(result, "should have captured value");
        Assertions.assertEquals(3, result.size(), "should be 3 results");
    }

    @Test
    void testSamplesForConversionIdSingleUnit() throws ConversionPathException, ConversionFactorException {
        Long conversionId = 12345L;
        Boolean isLiquid = false;
        Long fromUnitId = SINGLE_UNIT_ID;

        FoodConversionEntity singleUnitFactor = buildFoodConversionFactor(conversionId, fromUnitId, null, null);
        Set<Long> unitIdsSearch = new HashSet<>();
        unitIdsSearch.add(SINGLE_UNIT_ID);
        List<UnitEntity> foundUnits = unitIdsSearch.stream()
                .map(s -> testUnitLookups.get(s))
                .collect(Collectors.toList());

        SimpleAmount toConvertSingleUnit = new SimpleAmount(1.0, testUnitLookups.get(SINGLE_UNIT_ID), conversionId, false, null);

        Mockito.when(foodConversionRepository.findAllByConversionId(conversionId))
                .thenReturn(Collections.singletonList(singleUnitFactor));
        Mockito.when(unitRepository.findById(SINGLE_UNIT_ID))
                .thenReturn(Optional.of(testUnitLookups.get(SINGLE_UNIT_ID)));
        Mockito.when(unitRepository.findById(GRAM_ID))
                .thenReturn(Optional.of(testUnitLookups.get(GRAM_ID)));
        Mockito.when(unitRepository.findAllById(unitIdsSearch))
                .thenReturn(foundUnits);

        Mockito.when(conversionService.convertToUnit(toConvertSingleUnit, testUnitLookups.get(GRAM_ID), null))
                .thenReturn(dummyConvert(testUnitLookups.get(GRAM_ID), null));

        List<ConversionSampleDTO> result = foodService.samplesForConversionId(conversionId, isLiquid);

        Assertions.assertNotNull(result, "should have captured value");
        Assertions.assertEquals(1, result.size(), "should contain 1 result");
    }

    @Test
    void testSamplesForConversionIdMultiUnitsAndMarkers() throws ConversionPathException, ConversionFactorException {
        Long conversionId = 12345L;
        Boolean isLiquid = false;

        FoodConversionEntity dicedTeaspoonFactor = buildFoodConversionFactor(conversionId, TABLESPOON_ID, "diced", null);
        FoodConversionEntity slicedCupFactor = buildFoodConversionFactor(conversionId, CUP_ID, "sliced", null);
        FoodConversionEntity noMarkerCupFactor = buildFoodConversionFactor(conversionId, CUP_ID, null, null);
        FoodConversionEntity largeUnitFactor = buildFoodConversionFactor(conversionId, SINGLE_UNIT_ID,null , "large");
        FoodConversionEntity mediumUnitFactor = buildFoodConversionFactor(conversionId, SINGLE_UNIT_ID, null, "medium");

        Set<Long> unitIdsSearch = new HashSet<>();
        unitIdsSearch.add(TABLESPOON_ID);
        unitIdsSearch.add(TEASPOON_ID);
        unitIdsSearch.add(CUP_ID);
        unitIdsSearch.add(SINGLE_UNIT_ID);
        List<UnitEntity> foundUnits = unitIdsSearch.stream()
                .map(s -> testUnitLookups.get(s))
                .collect(Collectors.toList());

        List<FoodConversionEntity> allFactors = Arrays.asList(dicedTeaspoonFactor, slicedCupFactor, noMarkerCupFactor, largeUnitFactor, mediumUnitFactor);
        Set<Long> singleUnitId = new HashSet<>();
        singleUnitId.add(SINGLE_UNIT_ID);

        Mockito.when(foodConversionRepository.findAllByConversionId(conversionId))
                .thenReturn(Arrays.asList(dicedTeaspoonFactor, slicedCupFactor, noMarkerCupFactor, largeUnitFactor, mediumUnitFactor));
        Mockito.when(unitRepository.findIntegralUnits(allFactors.stream().map(FoodConversionEntity::getFromUnitId).collect(Collectors.toSet())))
                .thenReturn(singleUnitId);
        Mockito.when(unitRepository.findById(GRAM_ID))
                .thenReturn(Optional.of(testUnitLookups.get(GRAM_ID)));
        Mockito.when(unitRepository.findAllById(unitIdsSearch))
                .thenReturn(foundUnits);

        Mockito.when(conversionService.convertToUnit(Mockito.any(ConvertibleAmount.class), Mockito.any(UnitEntity.class), Mockito.eq("large")))
                .thenReturn(dummyConvert(testUnitLookups.get(SINGLE_UNIT_ID), null));
        Mockito.when(conversionService.convertToUnit(Mockito.any(ConvertibleAmount.class), Mockito.any(UnitEntity.class),Mockito.eq("medium")))
                .thenReturn(dummyConvert(testUnitLookups.get(SINGLE_UNIT_ID), null));
        Mockito.when(conversionService.convertToUnit(Mockito.any(ConvertibleAmount.class), Mockito.any(UnitEntity.class), Mockito.eq(null)))
                .thenReturn(dummyConvert(testUnitLookups.get(SINGLE_UNIT_ID), null));

        List<ConversionSampleDTO> result = foodService.samplesForConversionId(conversionId, isLiquid);

        Assertions.assertNotNull(result, "should have captured value");
        Assertions.assertEquals(18, result.size(), "should be 18 results");
    }


    private ConvertibleAmount dummyConvert(UnitEntity targetUnit, String marker) {
        return new SimpleAmount(2.0, targetUnit, null, false, marker);
    }

    private FoodConversionEntity buildFoodConversionFactor(Long conversionId, Long fromUnitId, String marker, String unitSize) {
        FoodConversionEntity conversionEntity = new FoodConversionEntity();
        conversionEntity.setUnitId(fromUnitId);
        conversionEntity.setConversionId(conversionId);
        conversionEntity.setMarker(marker);
        conversionEntity.setUnitSize(unitSize);
        return conversionEntity;
    }

    private FoodCategoryMappingEntity buildMapping(Long tagId, Long categoryId) {
        FoodCategoryMappingEntity entity = new FoodCategoryMappingEntity();
        entity.setTagId(tagId);
        entity.setCategoryId(categoryId);
        return entity;
    }

    private TagEntity buildTagEntity(Long tagId, String tagName) {
        TagEntity tag = new TagEntity();
        tag.setId(tagId);
        tag.setName(tagName);

        return tag;
    }


    private FoodCategoryEntity buildCategory(Long categoryId, String categoryName) {
        FoodCategoryEntity category = new FoodCategoryEntity();
        category.setId(categoryId);
        category.setName(categoryName);
        return category;
    }


    private TagInfoDTO buildTagInfoDTO(Long tagId, String tagName,
                                       Long parentId) {
        return new TagInfoDTO(tagId, tagName, "", 0.0, 0L, TagType.Rating, false, parentId, false);

    }

    private FoodEntity buildFood(Long foodId, String foodName, Long categoryId) {
        FoodEntity food = new FoodEntity();
        food.setFoodId(foodId);
        food.setName(foodName);
        food.setCategoryId(categoryId);
        return food;
    }

    private UnitEntity buildUnit(Long unitId, String unitName) {
        UnitEntity unit = new UnitEntity();
        unit.setId(unitId);
        unit.setType(UnitType.HYBRID);
        unit.setName(unitName);
        return unit;
    }

}