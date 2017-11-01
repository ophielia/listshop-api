package com.meg.atable.api;

import com.meg.atable.Application;
import com.meg.atable.api.model.*;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.JwtUser;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.ItemEntity;
import com.meg.atable.data.entity.ShoppingListEntity;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.service.ShoppingListService;
import com.meg.atable.service.TagService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
    private String userName = "testname";

    private static UserAccountEntity userAccount;
    private static UserDetails userDetails;
    private static TagEntity tag2;
    private static TagEntity tag1;
    private static ShoppingListEntity baseShoppingList;
    private static ShoppingListEntity toDeletePickup;
    private static Long toDeleteItemId;
    private static boolean setupComplete = false;


    @Before
    @WithMockUser
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        if (this.setupComplete) {
            return;
        }
        this.userAccount = userService.save(new UserAccountEntity(userName, "password"));
        this.userDetails = new JwtUser(this.userAccount.getId(),
                userName,
                null,
                null,
                null,
                true,
                null);

        // Tags to make Items
        this.tag1 = new TagEntity();
        this.tag1.setTagType(TagType.Ingredient);
        this.tag1.setName("tag1");
        this.tag1 = tagService.createTag(null, this.tag1);
        this.tag2 = new TagEntity();
        this.tag2.setTagType(TagType.Ingredient);
        this.tag2.setName("tag1");
        this.tag2 = tagService.createTag(null, this.tag2);
        // Base Shopping List for retrieve with one item
        this.baseShoppingList = new ShoppingListEntity();
        this.baseShoppingList.setListLayoutType(ListLayoutType.All);
        this.baseShoppingList.setListType(ListType.BaseList);
        this.baseShoppingList = shoppingListService.createList(userName, this.baseShoppingList);
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setItemSource(ItemSourceType.Manual);
        itemEntity.setTag(tag2);
        shoppingListService.addItemToList(userName, baseShoppingList.getId(), itemEntity);
        // now - find the item id of the added item (so it can be deleted
        this.baseShoppingList = shoppingListService.getListById(userName, baseShoppingList.getId());
        this.toDeleteItemId = this.baseShoppingList.getItems().get(0).getId();

        // Pick up list which will be deleted
        this.toDeletePickup = new ShoppingListEntity();
        this.toDeletePickup.setListLayoutType(ListLayoutType.All);
        this.toDeletePickup.setListType(ListType.PickUpList);
        this.toDeletePickup = shoppingListService.createList(userName, this.toDeletePickup);
        this.userAccount = userService.save(new UserAccountEntity("updateUser", "password"));
/*        this.userDetailsBad = new JwtUser(this.userAccount.getId(),
                "updateUser",
                null,
                null,
                null,
                true,
                null);*/
        this.setupComplete = true;
    }


    @Test
    @WithMockUser
    public void testRetrieveLists() throws Exception {
        Long testId = this.baseShoppingList.getId();
        Long testId2 = this.toDeletePickup.getId();
        mockMvc.perform(get("/shoppinglist")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.shoppingListResourceList", hasSize(2)))
                .andExpect(jsonPath("$._embedded.shoppingListResourceList[0].shopping_list.list_id").value(testId))
                .andExpect(jsonPath("$._embedded.shoppingListResourceList[0].shopping_list.list_type", is("BaseList")))
                .andExpect(jsonPath("$._embedded.shoppingListResourceList[1].shopping_list.list_id").value(testId2))
                .andExpect(jsonPath("$._embedded.shoppingListResourceList[1].shopping_list.list_type", is("PickUpList")));

    }


    @Test
    @WithMockUser
    public void testCreateList() throws Exception {
        JwtUser createUserDetails = new JwtUser(userAccount.getId(),
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
        Long testId = this.baseShoppingList.getId();

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
        Long testId = this.baseShoppingList.getId();

        mockMvc.perform(get("/shoppinglist/" + this.baseShoppingList.getId())
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.shopping_list.list_id", Matchers.isA(Number.class)))
                .andExpect(jsonPath("$.shopping_list.list_id").value(testId));
    }


    @Test
    @WithMockUser
    public void testDeleteList() throws Exception {
        Long testId = this.toDeletePickup.getId();

        mockMvc.perform(delete("/shoppinglist/" + testId)
                .with(user(userDetails)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void testAddItemToList() throws Exception {
        String url = "/shoppinglist/" + this.baseShoppingList.getId()
                + "/item";

        Item item = new Item()
                .tagId(tag1.getTag_id().toString())
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
        Long listId = this.baseShoppingList.getId();
        String url = "/shoppinglist/" + listId + "/item/" + this.toDeleteItemId;
        mockMvc.perform(delete(url)
                .with(user(userDetails)))
                .andExpect(status().isNoContent());
    }


    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
