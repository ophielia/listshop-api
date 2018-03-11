package com.meg.atable.api;

import com.meg.atable.Application;
import com.meg.atable.api.model.Item;
import com.meg.atable.api.model.ShoppingList;
import com.meg.atable.api.model.TagType;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.JwtUser;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.*;
import com.meg.atable.data.repository.DishRepository;
import com.meg.atable.data.repository.MealPlanRepository;
import com.meg.atable.data.repository.SlotRepository;
import com.meg.atable.data.repository.TagRepository;
import com.meg.atable.service.ShoppingListService;
import com.meg.atable.service.impl.ServiceTestUtils;
import com.meg.atable.service.tag.TagService;
import com.meg.atable.test.TestConstants;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class ShoppingListRestControllerTest {


    @Autowired
    private ShoppingListService shoppingListService;

    @Autowired
    private TagService tagService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private MealPlanRepository mealPlanRepository;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.stream(converters)

                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null");
    }

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    private final String userName = "testname";

    private static UserAccountEntity userAccount;
    private static UserDetails userDetails;
    private static TagEntity tag1;
    private static ShoppingListEntity baseShoppingList;
    private static ShoppingListEntity toDeletePickup;
    private static Long toDeleteItemId;
    private static boolean setupComplete = false;
    private static MealPlanEntity finalMealPlan;


    @Before
    @WithMockUser
    public void setup() throws Exception {

        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        if (setupComplete) {
            return;
        }
        userDetails = new JwtUser(TestConstants.USER_1_ID,
                TestConstants.USER_1_NAME,
                null,
                null,
                null,
                true,
                null);
        setupComplete = true;
/*
        if (setupComplete) {
            return;
        }
        userAccount = userService.save(new UserAccountEntity(userName, "password"));
        userDetails = new JwtUser(userAccount.getId(),
                userName,
                null,
                null,
                null,
                true,
                null);

        // Tags to make Items
        tag1 = new TagEntity();
        tag1.setTagType(TagType.Ingredient);
        tag1.setName("tag1");
        tag1 = tagService.createTag(null, tag1);
        TagEntity tag2 = new TagEntity();
        tag2.setTagType(TagType.Ingredient);
        tag2.setName("tag1");
        tag2 = tagService.createTag(null, tag2);
        // Base Shopping List for retrieve with one item
        baseShoppingList = new ShoppingListEntity();
        baseShoppingList.setListLayoutType(ListLayoutType.All);
        baseShoppingList.setListType(ListType.BaseList);
        baseShoppingList = shoppingListService.createList(userName, baseShoppingList);
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.addItemSource(ItemSourceType.Manual);
        itemEntity.setTag(tag2);
        shoppingListService.addItemToList(userName, TestConstants.LIST_1_ID, itemEntity);
        // now - find the item id of the added item (so it can be deleted
        baseShoppingList = shoppingListService.getListById(userName, TestConstants.LIST_1_ID);
        toDeleteItemId = baseShoppingList.getItems().get(0).getId();

        // Pick up list which will be deleted
        toDeletePickup = new ShoppingListEntity();
        toDeletePickup.setListLayoutType(ListLayoutType.All);
        toDeletePickup.setListType(ListType.PickUpList);
        toDeletePickup = shoppingListService.createList(userName, toDeletePickup);
        userAccount = userService.save(new UserAccountEntity("updateUser", "password"));

        finalMealPlan = createTestMealPlan();
        setupComplete = true;

 */
    }


    @Test
    @WithMockUser
    public void testRetrieveLists() throws Exception {
        JwtUser thisuserDetails = new JwtUser(TestConstants.USER_1_ID,
                TestConstants.USER_1_NAME,
                null,
                null,
                null,
                true,
                null);

        mockMvc.perform(get("/shoppinglist")
                .with(user(thisuserDetails)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.shoppingListResourceList", hasSize(3)));
    }


    @Test
    @WithMockUser
    public void testCreateList() throws Exception {
        JwtUser createUserDetails = new JwtUser(TestConstants.USER_1_ID,
                userName,
                null,
                null,
                null,
                true,
                null);

        ShoppingList shoppingList = new ShoppingList()
                .listType("ActiveList")
                .layoutType("All");
        String shoppingListJson = json(shoppingList);

        this.mockMvc.perform(post("/shoppinglist")
                .with(user(createUserDetails))
                .contentType(contentType)
                .content(shoppingListJson))
                .andExpect(status().isCreated());

    }

    @Test
    @WithMockUser
    public void testRetrieveListByType() throws Exception {
        Long testId = TestConstants.LIST_1_ID;

        mockMvc.perform(get("/shoppinglist/type/BaseList")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.shopping_list.list_id", Matchers.isA(Number.class)))
                .andExpect(jsonPath("$.shopping_list.list_id").value(testId));
    }


    @Test
    @WithMockUser
    public void testRetrieveListById() throws Exception {
        Long testId = TestConstants.LIST_1_ID;

        mockMvc.perform(get("/shoppinglist/" + TestConstants.LIST_1_ID)
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.shopping_list.list_id", Matchers.isA(Number.class)))
                .andExpect(jsonPath("$.shopping_list.list_id").value(testId));
    }


    @Test
    @WithMockUser
    public void testDeleteList() throws Exception {
        Long testId = TestConstants.LIST_3_ID;

        mockMvc.perform(delete("/shoppinglist/" + testId)
                .with(user(userDetails)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void testAddItemToList() throws Exception {
        String url = "/shoppinglist/" + TestConstants.LIST_1_ID
                + "/item";

        Item item = new Item()
                .tagId(tag1.getId().toString())
                .itemSource("Manual");

        String itemJson = json(item);
        this.mockMvc.perform(post(url)
                .with(user(userDetails))
                .contentType(contentType)
                .content(itemJson))
                .andExpect(status().isNoContent());
    }


    @Test
    @WithMockUser
    public void testDeleteItemFromList() throws Exception {
        Long listId = TestConstants.LIST_1_ID;
        String url = "/shoppinglist/" + listId + "/item/" + toDeleteItemId;
        mockMvc.perform(delete(url)
                .with(user(userDetails)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void testGenerateFromMealPlan() throws Exception {
        Long mealPlanId = TestConstants.MEAL_PLAN_1_ID;
        String url = "/shoppinglist/mealplan/" + mealPlanId;

        this.mockMvc.perform(post(url)
                .with(user(userDetails))
                .contentType(contentType))
                .andExpect(status().isNoContent());
    }

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    private MealPlanEntity createTestMealPlan() {
        TagEntity tag1 = ServiceTestUtils.buildTag("tag1", TagType.TagType);
        TagEntity tag2 = ServiceTestUtils.buildTag("tag2", TagType.TagType);
        TagEntity tag3 = ServiceTestUtils.buildTag("tag3", TagType.TagType);
        TagEntity tag4 = ServiceTestUtils.buildTag("tag4", TagType.TagType);
        TagEntity tag5 = ServiceTestUtils.buildTag("tag5", TagType.TagType);
        List<TagEntity> tags = Arrays.asList(tag1, tag2, tag3, tag4, tag5);
        List<TagEntity> savedTags = tagRepository.save(tags);

        DishEntity dish1 = ServiceTestUtils.buildDish(userAccount.getId(), "dish1", savedTags.subList(0, 2));
        DishEntity dish2 = ServiceTestUtils.buildDish(userAccount.getId(), "dish2", savedTags.subList(2, 3));
        DishEntity dish3 = ServiceTestUtils.buildDish(userAccount.getId(), "dish3", savedTags.subList(3, 5));
        List<DishEntity> dishes = Arrays.asList(dish1, dish2, dish3);
        List<DishEntity> savedDishes = dishRepository.save(dishes);

        MealPlanEntity mealPlanEntity = ServiceTestUtils.buildMealPlan("testMealPlan", userAccount.getId());
        MealPlanEntity savedMealPlan = mealPlanRepository.save(mealPlanEntity);

        SlotEntity slot1 = ServiceTestUtils.buildDishSlot(savedMealPlan, savedDishes.get(0));
        SlotEntity slot2 = ServiceTestUtils.buildDishSlot(savedMealPlan, savedDishes.get(1));
        SlotEntity slot3 = ServiceTestUtils.buildDishSlot(savedMealPlan, savedDishes.get(2));
        List<SlotEntity> slots = Arrays.asList(slot1, slot2, slot3);
        List<SlotEntity> savedSlots = slotRepository.save(slots);

        savedMealPlan.setSlots(savedSlots);
        return mealPlanRepository.save(savedMealPlan);
    }
}
