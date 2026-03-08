/*
 * The List Shop
 *
 * Copyright (c) 2022-2026.
 */

package com.meg.listshop.lmt.api.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meg.listshop.Application;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.api.model.v2.Tag;
import com.meg.listshop.lmt.api.model.v2.TagList;
import com.meg.listshop.test.TestConstants;
import com.meg.listshop.test.TestUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.annotation.PostConstruct;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;

import static io.restassured.RestAssured.given;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest(classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class V2TagRestControllerTest {

    @Container
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();
    private final String token = TestConstants.USER_1_TOKEN;
    private final String tokenUserWithTags = TestConstants.USER_3_TOKEN;
    @LocalServerPort
    public int serverPort;
    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void initRestAssured() {
        RestAssured.port = serverPort;
        RestAssured.urlEncodingEnabled = false;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void readSingleTag() {
        Long testId = 128L;
        String url = "/v2/tag/" + testId;
        String json = given()
                .header(TestUtils.authToken(token))
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("tag_id", Matchers.isA(String.class))
                .body("tag_id", Matchers.equalTo(testId.toString()))
                .extract().asString();
        Assertions.assertNotNull(json);
    }

    @Test
    void readSingleTagForUser() {
        String url = "/v2/tag/333333";
        given()
                .header(TestUtils.authToken(tokenUserWithTags))
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("tag_id", Matchers.isA(String.class))
                .body("tag_id", Matchers.equalTo("333333"));
    }

    @Test
    void readTagsForUser() {
        String url = "/v2/tag";
        TagList tagList = given()
                .header(TestUtils.authToken(tokenUserWithTags))
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("tag_list.user_id", Matchers.hasItems("0", "20"))
                .extract().as(TagList.class);
        Assertions.assertNotNull(tagList);
        Assertions.assertTrue( tagList.getTagList().size() >= 478);
    }


    @Test
    void readStandardTags() {
        String url = "/v2/tag";
        TagList tagList = given()
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("tag_list.user_id", Matchers.hasItems("0"))
                .body("tag_list.user_id", Matchers.not(Matchers.hasItems("20")))
                .extract().as(TagList.class);
        Assertions.assertNotNull(tagList);
        Assertions.assertTrue( tagList.getTagList().size() >= 477);
    }

    @Test
    void addAsChild() throws Exception {
        String url = "/v2/tag/" + TestConstants.PARENT_TAG_ID_2 + "/child";

        Tag tag = new Tag().withName("testTag")
                .withTagType(TagType.Rating.name());
        String tagString = json(tag);

        String locationValue = given()
                .header(TestUtils.authToken(token))
                .contentType(ContentType.JSON)
                .body(tagString)
                .when()
                .post(url)
                .then()
                .statusCode(201)
                .extract()
                .header("Location");

        // affirm that newly created tag has user_id matching user_details
        // and parent id matching parent_tag_id_2
        // get newly created id
        String idString = locationValue.substring(locationValue.lastIndexOf("/") + 1);
        Long newId = Long.valueOf(idString);

        String fetchUrl = "/tag/" + newId;
        given()
                .header(TestUtils.authToken(token))
                .when()
                .get(fetchUrl)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("tag.user_id", Matchers.equalTo(TestConstants.USER_1_ID.toString()));

        // now, check that parent id is correct"
        TagList afterList = given()
                .header(TestUtils.authToken(token))
                .when()
                .get("/v2/tag")
                .then()
                .statusCode(200)
                .extract()
                .as(TagList.class);

        Tag resultTag = afterList.getTagList().stream()
                .filter(t -> t.getTagId().equals(idString))
                .findFirst()
                .orElse(null);
        Assertions.assertEquals(String.valueOf(TestConstants.PARENT_TAG_ID_2), resultTag.getParentId(), "parent tag id should equal that given for create call");

    }

    @Test
    void addAsChild_Exists() throws Exception {
        Long parentId = 88L; // prepared meats
        String url = "/v2/tag/" + parentId + "/child";

        Tag tag = new Tag().withName("Meaty Ingredient")
                .withTagType(TagType.Ingredient.name());
        String tagString = json(tag);

        String locationValue = given()
                .header(TestUtils.authToken(token))
                .contentType(ContentType.JSON)
                .body(tagString)
                .when()
                .post(url)
                .then()
                .statusCode(201)
                .extract()
                .header("Location");

        // get newly created id
        String idString = locationValue.substring(locationValue.lastIndexOf("/") + 1);
        Long newId = Long.valueOf(idString);

        // now, try to recreate the same tag
        String recreateLocationValue = given()
                .header(TestUtils.authToken(token))
                .contentType(ContentType.JSON)
                .body(tagString)
                .when()
                .post(url)
                .then()
                .statusCode(201)
                .extract()
                .header("Location");

        // get the resulting id
        String recreateIdString = recreateLocationValue.substring(recreateLocationValue.lastIndexOf("/") + 1);
        Long recreatedId = Long.valueOf(recreateIdString);

        Assertions.assertEquals(newId, recreatedId);
    }

    @Test
    void addAsChildStandard() throws Exception {
        String url = "/v2/tag/" + TestConstants.PARENT_TAG_ID_2 + "/child?asStandard=true";

        Tag tag = new Tag().withName("testTag-standard")
                .withTagType(TagType.Rating.name());
        String tagString = json(tag);

        String locationValue = given()
                .header(TestUtils.authToken(token))
                .contentType(ContentType.JSON)
                .body(tagString)
                .when()
                .post(url)
                .then()
                .statusCode(201)
                .extract()
                .header("Location");

        // affirm that newly created tag has user_id matching user_details
        // and parent id matching parent_tag_id_2
        // get newly created id
        String idString = locationValue.substring(locationValue.lastIndexOf("/") + 1);

        // now, check that parent id is correct"
        TagList tagList = given()
                .header(TestUtils.authToken(token))
                .when()
                .get("/v2/tag")
                .then()
                .statusCode(200)
                .extract()
                .as(TagList.class);

        Tag afterTag = tagList.getTagList().stream()
                .filter(t -> t.getTagId().equals(idString))
                .findFirst()
                .orElse(null);
        Assertions.assertEquals(String.valueOf(TestConstants.PARENT_TAG_ID_2), afterTag.getParentId(), "parent tag id should equal that given for create call");
        Assertions.assertEquals(String.valueOf(0L), afterTag.getUserId(), "user id should be 0");

    }

    @Test
    void addAsChildStandardExists() throws Exception {
        Long parentId = 88L;
        String url = "/v2/tag/" + parentId + "/child?asStandard=true";

        Tag tag = new Tag().withName("mystery MEAT")
                .withTagType(TagType.Ingredient.name());
        String tagString = json(tag);

        String locationValue = given()
                .header(TestUtils.authToken(token))
                .contentType(ContentType.JSON)
                .body(tagString)
                .when()
                .post(url)
                .then()
                .statusCode(201)
                .extract()
                .header("Location");

        // get newly created id
        String idString = locationValue.substring(locationValue.lastIndexOf("/") + 1);

        // recreate same tag
        Tag recreateTag = new Tag().withName("mystery MEAT")
                .withTagType(TagType.Ingredient.name());
        String recreateTagString = json(recreateTag);

        String recreateLocationValue = given()
                .header(TestUtils.authToken(token))
                .contentType(ContentType.JSON)
                .body(recreateTagString)
                .when()
                .post(url)
                .then()
                .statusCode(201)
                .extract()
                .header("Location");
        String recreateId = recreateLocationValue.substring(recreateLocationValue.lastIndexOf("/") + 1);

        Assertions.assertEquals(idString, recreateId);
    }


    private String json(Object o) throws IOException {
        return objectMapper.writeValueAsString(o);
    }

}
