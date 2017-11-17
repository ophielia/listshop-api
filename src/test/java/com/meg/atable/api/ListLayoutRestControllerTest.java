package com.meg.atable.api;

import com.meg.atable.Application;
import com.meg.atable.api.model.*;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.JwtUser;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.ListLayoutCategoryEntity;
import com.meg.atable.data.entity.ListLayoutEntity;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.data.repository.ListLayoutCategoryRepository;
import com.meg.atable.data.repository.ListLayoutRepository;
import com.meg.atable.data.repository.TagRepository;
import com.meg.atable.service.DishService;
import com.meg.atable.service.ListLayoutService;
import com.meg.atable.service.impl.ServiceTestUtils;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
public class ListLayoutRestControllerTest {


    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;
    @Autowired
    private ListLayoutRepository listLayoutRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private ListLayoutCategoryRepository layoutCategoryRepository;

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
    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    private MockMvc mockMvc;
    private static UserAccountEntity userAccount;
    private String userName = "testname";

    private static List<ListLayoutEntity> listLayoutList = new ArrayList<>();
    private static UserDetails userDetails;
    private static boolean setup = false;
    private static ListLayoutEntity deleteList;
    private static ListLayoutEntity deleteCategoryList;
    private static TagEntity tag1;
    private static TagEntity tag2;
    private static TagEntity tag3;
    private static TagEntity tag4;
    private static TagEntity tag5;

    @Before
    @WithMockUser
    public void setup() throws Exception {

        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        if (setup) {
            return;
        }
        this.userAccount = userService.save(new UserAccountEntity(userName, "password"));
        userDetails = new JwtUser(this.userAccount.getId(),
                userName,
                null,
                null,
                null,
                true,
                null);


        ListLayoutEntity retrieve = new ListLayoutEntity();
        retrieve.setLayoutType(ListLayoutType.All);
        retrieve = listLayoutRepository.save(retrieve);

        ListLayoutEntity toManipulate = new ListLayoutEntity();
        toManipulate.setLayoutType(ListLayoutType.All);
        toManipulate = listLayoutRepository.save(toManipulate);


        tag1 = ServiceTestUtils.buildTag("tag1", TagType.Ingredient);
        tag2 = ServiceTestUtils.buildTag("tag2", TagType.Ingredient);
        tag3 = ServiceTestUtils.buildTag("tag3", TagType.Ingredient);
        tag4 = ServiceTestUtils.buildTag("tag4", TagType.Ingredient);
        tag5 = ServiceTestUtils.buildTag("tag5", TagType.Ingredient);
        tag1 = tagRepository.save(tag1);
        tag2 = tagRepository.save(tag2);
        tag3 = tagRepository.save(tag3);
        tag4 = tagRepository.save(tag4);
        tag5 = tagRepository.save(tag5);

        ListLayoutCategoryEntity layoutCategoryEntity = new ListLayoutCategoryEntity();
        layoutCategoryEntity.setName("addTagsCategoryId");
        List<TagEntity> assignedTags = Arrays.asList(tag1, tag2);
        layoutCategoryEntity.setTags(assignedTags);
        layoutCategoryEntity.setLayoutId(retrieve.getId());
        ListLayoutCategoryEntity addCategories = layoutCategoryRepository.save(layoutCategoryEntity);

        layoutCategoryEntity = new ListLayoutCategoryEntity();
        layoutCategoryEntity.setName("deleteTagsCategory");
        assignedTags = Arrays.asList(tag1, tag2);
        layoutCategoryEntity.setTags(assignedTags);
        layoutCategoryEntity.setLayoutId(retrieve.getId());
        ListLayoutCategoryEntity deleteCategory = layoutCategoryRepository.save(layoutCategoryEntity);

        ListLayoutCategoryEntity retrieveCategory = new ListLayoutCategoryEntity();
        layoutCategoryEntity.setName("retrieveCategory");
        assignedTags = Arrays.asList(tag3, tag4);
        retrieveCategory.setTags(assignedTags);
        retrieveCategory.setLayoutId(retrieve.getId());
        ListLayoutCategoryEntity retrieveCat = layoutCategoryRepository.save(retrieveCategory);


        List<ListLayoutCategoryEntity> newCategories = Arrays.asList(retrieveCat, deleteCategory, addCategories);
        retrieve.setCategories(newCategories);
        retrieve.setName("listLayoutOne");
        retrieve = listLayoutRepository.save(retrieve);


        ListLayoutCategoryEntity uncatCat = new ListLayoutCategoryEntity();
        uncatCat.setName("uncatCat");
        assignedTags = Arrays.asList(tag3, tag4);
        uncatCat.setTags(assignedTags);
        uncatCat.setLayoutId(toManipulate.getId());
        ListLayoutCategoryEntity uncatCategory = layoutCategoryRepository.save(uncatCat);

        toManipulate.setCategories(Arrays.asList(uncatCategory));
        toManipulate.setName("listLayoutTwo");
        listLayoutRepository.save(toManipulate);

        ListLayoutEntity layout = new ListLayoutEntity();
        layout.setLayoutType(ListLayoutType.All);
        layout = listLayoutRepository.save(layout);
        ListLayoutCategoryEntity three = new ListLayoutCategoryEntity();
        three.setName("uncatCat");
        assignedTags = Arrays.asList(tag3, tag4);
        three.setTags(assignedTags);
        three.setLayoutId(toManipulate.getId());
        three = layoutCategoryRepository.save(three);

        layout.setCategories(Arrays.asList(three));
        layout.setName("delete me");
        deleteList = listLayoutRepository.save(layout);

        layout = new ListLayoutEntity();
        layout.setLayoutType(ListLayoutType.All);
        layout = listLayoutRepository.save(layout);
        three = new ListLayoutCategoryEntity();
        three.setName("uncatCat");
        assignedTags = Arrays.asList(tag3, tag4);
        three.setTags(assignedTags);
        three.setLayoutId(toManipulate.getId());
        three = layoutCategoryRepository.save(three);

        layout.setCategories(Arrays.asList(three));
        layout.setName("delete me");
        deleteCategoryList = listLayoutRepository.save(layout);

        this.listLayoutList = Arrays.asList(toManipulate, retrieve);
        setup = true;
    }


    @Test
    @WithMockUser
    public void testReadListLayout() throws Exception {
        Long testId = this.listLayoutList.get(0).getId();
        mockMvc.perform(get("/listlayout/"
                + this.listLayoutList.get(0).getId())
                .with(user(this.userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.list_layout.layout_id", Matchers.isA(Number.class)))
                .andExpect(jsonPath("$.list_layout.layout_id").value(testId));

    }

    @Test
    @WithMockUser
    public void testAddCategoryToListLayout() throws Exception {
        JwtUser createUserDetails = new JwtUser(userAccount.getId(),
                userName,
                null,
                null,
                null,
                true,
                null);
        ListLayoutEntity test = this.listLayoutList.get(0);
        ListLayoutCategoryEntity testcategory = new ListLayoutCategoryEntity();
        testcategory.setName("new category");
        ListLayoutCategory categoryModel = ModelMapper.toModel(testcategory);
        String url = "/listlayout/"
                + this.listLayoutList.get(0).getId() + "/category";
        String listLayoutJson = json(categoryModel);
        this.mockMvc.perform(post(url)
                .with(user(createUserDetails))
                .contentType(contentType)
                .content(listLayoutJson))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void testAddTagsToCategory() throws Exception {
        List<String> idList = Arrays.asList(this.tag1, this.tag2, this.tag3, this.tag5)
                .stream()
                .map(TagEntity::getId)
                .map(String::valueOf)
                .collect(Collectors.toList());
        String taglist = "?tags=" + String.join(",", idList);
        ListLayoutEntity test = this.listLayoutList.get(0);
        ListLayoutCategoryEntity testcategory = test.getCategories().get(0);
        mockMvc.perform(post("/listlayout/"
                + this.listLayoutList.get(0).getId() + "/category/"
                + testcategory.getId() + "/tag" + taglist)
                .with(user(userDetails)))
                .andExpect(status().isNoContent());


    }

    @Test
    @WithMockUser
    public void testCreateListLayout() throws Exception {
        JwtUser createUserDetails = new JwtUser(userAccount.getId(),
                userName,
                null,
                null,
                null,
                true,
                null);
        ListLayoutEntity test = new ListLayoutEntity();
        test.setName("new list layout");
        test.setLayoutType(ListLayoutType.All);
        ListLayout model = ModelMapper.toModel(test);
        String url = "/listlayout";
        String listLayoutJson = json(model);
        this.mockMvc.perform(post(url)
                .with(user(createUserDetails))
                .contentType(contentType)
                .content(listLayoutJson))
                .andExpect(status().isCreated());

    }


    @Test
    @WithMockUser
    public void testGetTagsForCategory() throws Exception {
        ListLayoutEntity test = this.listLayoutList.get(0);
        ListLayoutCategoryEntity testcategory = test.getCategories().get(0);
        mockMvc.perform(get("/listlayout/"
                + this.listLayoutList.get(0).getId() + "/category/"
                + testcategory.getId() + "/tag")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));
    }


    @Test
    @WithMockUser
    public void testGetUncategorizedTags() throws Exception {
        mockMvc.perform(get("/listlayout/"
                + this.listLayoutList.get(0).getId() + "/tag")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));
    }

    @Test
    @WithMockUser
    public void testUpdateCategoryFromListLayout() throws Exception {
        JwtUser createUserDetails = new JwtUser(userAccount.getId(),
                userName,
                null,
                null,
                null,
                true,
                null);
        ListLayoutEntity test = this.listLayoutList.get(0);
        ListLayoutCategoryEntity testcategory = test.getCategories().get(0);
        testcategory.setName("wokkawokka");
        ListLayoutCategory categoryModel = ModelMapper.toModel(testcategory);
        String url = "/listlayout/"
                + this.listLayoutList.get(0).getId() + "/category/"
                + test.getId();
        String listLayoutJson = json(categoryModel);

        this.mockMvc.perform(put(url)
                .with(user(createUserDetails))
                .contentType(contentType)
                .content(listLayoutJson))
                .andExpect(status().isNoContent());

    }

    @Test
    @WithMockUser
    public void testRetrieveListLayouts() throws Exception {
        Long testId = this.listLayoutList.get(0).getId();
        Long testId2 = this.listLayoutList.get(1).getId();
        mockMvc.perform(get("/listlayout")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.listLayoutResourceList", hasSize(4)))
                .andExpect(jsonPath("$._embedded.listLayoutResourceList[1].list_layout.layout_id").value(testId))
                .andExpect(jsonPath("$._embedded.listLayoutResourceList[1].list_layout.name", is("listLayoutTwo")))
                .andExpect(jsonPath("$._embedded.listLayoutResourceList[0].list_layout.layout_id").value(testId2))
                .andExpect(jsonPath("$._embedded.listLayoutResourceList[0].list_layout.name", is("listLayoutOne")));

    }

    @Test
    @WithMockUser
    public void testDeleteListLayout() throws Exception {
        Long testId = deleteList.getId();
        mockMvc.perform(delete("/listlayout/"
                + testId)
                .with(user(userDetails)))
                .andExpect(status().isNoContent());

    }


    @Test
    @WithMockUser
    public void testDeleteCategoryFromListLayout() throws Exception {
        ListLayoutEntity list = deleteCategoryList;
        ListLayoutCategoryEntity category = list.getCategories().get(0);
        Long testId = category.getId();
        mockMvc.perform(delete("/listlayout/" + category.getLayoutId()
                + "/category/" +
                +testId)
                .with(user(userDetails)))
                .andExpect(status().isNoContent());


    }

    @Test
    @WithMockUser
    public void testDeleteTagsFromCategory() throws Exception {
        List<String> idList = Arrays.asList(this.tag1, this.tag2, this.tag3, this.tag5)
                .stream()
                .map(TagEntity::getId)
                .map(String::valueOf)
                .collect(Collectors.toList());
        String taglist = "?tags=" + String.join(",", idList);
        ListLayoutEntity test = this.listLayoutList.get(0);
        ListLayoutCategoryEntity testcategory = test.getCategories().get(0);
        mockMvc.perform(delete("/listlayout/"
                + this.listLayoutList.get(0).getId() + "/category/"
                + testcategory.getId() + "/tag" + taglist)
                .with(user(userDetails)))
                .andExpect(status().isNoContent());

    }

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }


}
