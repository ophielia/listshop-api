/*
 * The List Shop
 *
 * Copyright (c) 2022-2026.
 */

package com.meg.listshop.lmt.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.meg.listshop.Application;
import com.meg.listshop.auth.service.CustomUserDetails;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.Tag;
import com.meg.listshop.lmt.api.model.TagListResource;
import com.meg.listshop.lmt.api.model.TagResource;
import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.repository.TagRelationRepository;
import com.meg.listshop.lmt.data.repository.TagRepository;
import com.meg.listshop.lmt.service.layout.LayoutService;
import com.meg.listshop.test.TestConstants;
import com.meg.listshop.test.TestUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.annotation.PostConstruct;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureJsonTesters
@ActiveProfiles("test")
class TagRestControllerTest {

    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @LocalServerPort
    public int serverPort;

    private static UserDetails userDetails;
    @Autowired
    private
    ObjectMapper objectMapper;
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());

    @Autowired
    private TagRepository tagRepository;
    @Autowired
    @Qualifier("V2LayoutService")
    private LayoutService listLayoutService;
    @Autowired
    private TagRelationRepository tagRelationRepository;

    @PostConstruct
    public void initRestAssured() {
        RestAssured.port = serverPort;
        RestAssured.urlEncodingEnabled = false;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }


    @BeforeEach
    public void setup() throws Exception {


        userDetails = new CustomUserDetails(TestConstants.USER_1_ID,
                TestConstants.USER_1_EMAIL,
                null,
                null,
                null,
                true,
                null);


    }

    @Test
    void readSingleTag() throws Exception {
        Long testId = TestConstants.TAG_1_ID;
        String url = "/tag/" + testId;
        given()
                .header(TestUtils.authToken(TestConstants.USER_1_TOKEN))
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("tag.tag_id", Matchers.isA(String.class))
                .body("tag.tag_id", Matchers.equalTo(testId.toString()));

    }

    @Test
    @Disabled
        // endpoint is deprecated
    void createTag() throws Exception {
        Tag newtag = new Tag("created tag");
        newtag.tagType(TagType.Rating.name());
        String tagJson = json(newtag);

        given()
                .header(TestUtils.authToken(TestConstants.USER_1_TOKEN))
                .contentType(ContentType.JSON)
                .body(tagJson)
                .when()
                .post("/tag")
                .then()
                .statusCode(201);
    }

    @Test
    @Disabled
        // endpoint is deprecated
    void createTag_checkDefaults() throws Exception {
        Tag newtag = new Tag("created tag with defaults");
        newtag.tagType(TagType.Rating.name());
        String tagJson = json(newtag);

        String location = given()
                .header(TestUtils.authToken(TestConstants.USER_1_TOKEN))
                .contentType(ContentType.JSON)
                .body(tagJson)
                .when()
                .post("/tag")
                .then()
                .statusCode(201)
                .extract()
                .header("Location");

        Assertions.assertNotNull(location);
        String[] locationParts = location.split("/");
        String id = locationParts[locationParts.length - 1];
        Assertions.assertNotNull(id);

        String fetchUrl = "/tag/" + id;
        String resultJson = given()
                .header(TestUtils.authToken(TestConstants.USER_1_TOKEN))
                .when()
                .get(fetchUrl)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("tag.user_id", Matchers.equalTo(TestConstants.USER_1_ID.toString()))
                .extract()
                .asString();

        TagResource resourceResult = objectMapper.readValue(resultJson, TagResource.class);
        Assertions.assertNotNull(resourceResult);

        // verify that tag with found id belongs to default group
        String resultList = given()
                .header(TestUtils.authToken(TestConstants.USER_1_TOKEN))
                .when()
                .get("/tag/user")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .asString();

        TagListResource afterList = objectMapper.readValue(resultList, TagListResource.class);
        Map<String, Tag> tagMap = afterList.getEmbeddedList().getTagResourceList().stream()
                .map(TagResource::getTag)
                //.map(Dish::getId)
                .collect(Collectors.toMap(Tag::getId, Function.identity()));
        Tag newTag = tagMap.get(id);
        Assertions.assertNotNull(newTag.getParentId());

    }

    @Test
    @Disabled
        // endpoint is deprecated
    void createTag_asStandard() throws Exception {
        Tag newtag = new Tag("created Ingredient tag with defaults");
        newtag.tagType(TagType.Ingredient.name());
        String tagJson = json(newtag);

        String location = given()
                .header(TestUtils.authToken(TestConstants.USER_1_TOKEN))
                .contentType(ContentType.JSON)
                .body(tagJson)
                .when()
                .post("/tag?asStandard=true")
                .then()
                .statusCode(201)
                .extract()
                .header("Location");

        Assertions.assertNotNull(location);
        String[] locationParts = location.split("/");
        String id = locationParts[locationParts.length - 1];
        Assertions.assertNotNull(id);

        // verify that tag with found id belongs to default group
        String resultList = given()
                .header(TestUtils.authToken(TestConstants.USER_1_TOKEN))
                .when()
                .get("/tag/user?asStandard=true")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .asString();

        TagListResource afterList = objectMapper.readValue(resultList, TagListResource.class);
        Map<String, Tag> tagMap = afterList.getEmbeddedList().getTagResourceList().stream()
                .map(TagResource::getTag)
                //.map(Dish::getId)
                .collect(Collectors.toMap(Tag::getId, Function.identity()));
        Tag newTag = tagMap.get(id);
        Assertions.assertNotNull(newTag.getParentId());
        Assertions.assertEquals("0", newTag.getUserId());

    }


    @Test
    void addAsChild() throws Exception {
        String url = "/tag/" + TestConstants.PARENT_TAG_ID_2 + "/child";

        Tag tag = new Tag("testTag");
        tag = tag.tagType(TagType.Rating.name());
        String tagString = json(tag);

        String locationValue = given()
                .header(TestUtils.authToken(TestConstants.USER_1_TOKEN))
                .contentType(ContentType.JSON)
                .body(tagString)
                .when()
                .post(url)
                .then()
                .statusCode(Matchers.is(Matchers.both(Matchers.greaterThanOrEqualTo(200)).and(Matchers.lessThan(300))))
                .extract()
                .header("Location");

        // affirm that newly created tag has user_id matching user_details
        // and parent id matching parent_tag_id_2
        // get newly created id
        String idString = locationValue.substring(locationValue.lastIndexOf("/") + 1);
        Long newId = Long.valueOf(idString);

        String fetchUrl = "/tag/" + newId;
        given()
                .header(TestUtils.authToken(TestConstants.USER_1_TOKEN))
                .when()
                .get(fetchUrl)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("tag.user_id", Matchers.equalTo(TestConstants.USER_1_ID.toString()));

        // now, check that parent id is correct"
        String resultJson = given()
                .header(TestUtils.authToken(TestConstants.USER_1_TOKEN))
                .when()
                .get("/tag/user")
                .then()
                .statusCode(200)
                .extract()
                .asString();

        TagListResource afterList = objectMapper.readValue(resultJson, TagListResource.class);
        Optional<TagResource> resultTag = afterList.getEmbeddedList().getTagResourceList().stream()
                .filter(t -> t.getTag().getId().equals(idString))
                .findFirst();
        Assertions.assertNotNull(resultTag);
        Assertions.assertEquals(String.valueOf(TestConstants.PARENT_TAG_ID_2), resultTag.get().getTag().getParentId(), "parent tag id should equal that given for create call");

    }

    @Test
    void addAsChild_Exists() throws Exception {
        Long parentId = 88L; // prepared meats
        String url = "/tag/" + parentId + "/child";

        Tag tag = new Tag("Meaty Ingredient");
        tag = tag.tagType(TagType.Ingredient.name());
        String tagString = json(tag);

        String locationValue = given()
                .header(TestUtils.authToken(TestConstants.USER_1_TOKEN))
                .contentType(ContentType.JSON)
                .body(tagString)
                .when()
                .post(url)
                .then()
                .statusCode(Matchers.is(Matchers.both(Matchers.greaterThanOrEqualTo(200)).and(Matchers.lessThan(300))))
                .extract()
                .header("Location");

        // get newly created id
        String idString = locationValue.substring(locationValue.lastIndexOf("/") + 1);
        Long newId = Long.valueOf(idString);

        // now, try to recreate the same tag
        String recreateLocationValue = given()
                .header(TestUtils.authToken(TestConstants.USER_1_TOKEN))
                .contentType(ContentType.JSON)
                .body(tagString)
                .when()
                .post(url)
                .then()
                .statusCode(Matchers.is(Matchers.both(Matchers.greaterThanOrEqualTo(200)).and(Matchers.lessThan(300))))
                .extract()
                .header("Location");

        // get the resulting id
        String recreateIdString = recreateLocationValue.substring(recreateLocationValue.lastIndexOf("/") + 1);
        Long recreatedId = Long.valueOf(recreateIdString);

        Assertions.assertEquals(newId, recreatedId);
    }

    @Test
    void addAsChild_Standard() throws Exception {
        String url = "/tag/" + TestConstants.PARENT_TAG_ID_2 + "/child?asStandard=true";

        Tag tag = new Tag("testTag-standard");
        tag = tag.tagType(TagType.Rating.name());
        String tagString = json(tag);

        String locationValue = given()
                .header(TestUtils.authToken(TestConstants.USER_1_TOKEN))
                .contentType(ContentType.JSON)
                .body(tagString)
                .when()
                .post(url)
                .then()
                .statusCode(Matchers.is(Matchers.both(Matchers.greaterThanOrEqualTo(200)).and(Matchers.lessThan(300))))
                .extract()
                .header("Location");

        // affirm that newly created tag has user_id matching user_details
        // and parent id matching parent_tag_id_2
        // get newly created id
        String idString = locationValue.substring(locationValue.lastIndexOf("/") + 1);

        // now, check that parent id is correct"
        String resultJson = given()
                .header(TestUtils.authToken(TestConstants.USER_1_TOKEN))
                .when()
                .get("/tag/user")
                .then()
                .statusCode(200)
                .extract()
                .asString();

        TagListResource afterList = objectMapper.readValue(resultJson, TagListResource.class);
        Optional<TagResource> resultTag = afterList.getEmbeddedList().getTagResourceList().stream()
                .filter(t -> t.getTag().getId().equals(idString))
                .findFirst();
        Assertions.assertNotNull(resultTag);
        Assertions.assertEquals(String.valueOf(TestConstants.PARENT_TAG_ID_2), resultTag.get().getTag().getParentId(), "parent tag id should equal that given for create call");
        Assertions.assertEquals(String.valueOf(0L), resultTag.get().getTag().getUserId(), "user id should be 0");

    }

    @Test
    void addAsChild_StandardExists() throws Exception {
        Long parentId = 88L;
        String url = "/tag/" + parentId + "/child?asStandard=true";

        Tag tag = new Tag("mystery MEAT");
        tag = tag.tagType(TagType.Ingredient.name());
        String tagString = json(tag);

        String locationValue = given()
                .header(TestUtils.authToken(TestConstants.USER_1_TOKEN))
                .contentType(ContentType.JSON)
                .body(tagString)
                .when()
                .post(url)
                .then()
                .statusCode(Matchers.is(Matchers.both(Matchers.greaterThanOrEqualTo(200)).and(Matchers.lessThan(300))))
                .extract()
                .header("Location");

        // get newly created id
        String idString = locationValue.substring(locationValue.lastIndexOf("/") + 1);

        // recreate same tag
        Tag recreateTag = new Tag("MYSTERY MEAT");
        recreateTag = recreateTag.tagType(TagType.Ingredient.name());
        String recreateTagString = json(recreateTag);

        String recreateLocation = given()
                .header(TestUtils.authToken(TestConstants.USER_1_TOKEN))
                .contentType(ContentType.JSON)
                .body(recreateTagString)
                .when()
                .post(url)
                .then()
                .statusCode(Matchers.is(Matchers.both(Matchers.greaterThanOrEqualTo(200)).and(Matchers.lessThan(300))))
                .extract()
                .header("Location");
        String recreateId = recreateLocation.substring(recreateLocation.lastIndexOf("/") + 1);

        Assertions.assertEquals(idString, recreateId);
    }


    private String json(Object o) throws IOException {
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        return objectMapper.writeValueAsString(o);
    }

}
