package com.meg.listshop.lmt.data.repository.impl;

import com.meg.listshop.Application;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.pojos.IncludeType;
import com.meg.listshop.lmt.data.pojos.TagInfoDTO;
import com.meg.listshop.lmt.data.pojos.TagSearchCriteria;
import com.meg.listshop.lmt.data.pojos.TestTagInfo;
import com.meg.listshop.lmt.data.repository.TagRepository;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test-jpa")
public class CustomTagRepositoryImplTest {



    @Autowired
    private TagRepository repository;
    @Test
    void findTagInfoByCriteria() {
        TagSearchCriteria criteria = new TagSearchCriteria();
        criteria.setGroupIncludeType(IncludeType.ONLY);
criteria.setUserId(20L);
criteria.setTagTypes(Arrays.asList(new TagType[]{TagType.Ingredient, TagType.DishType}));
        List<TagInfoDTO> result = repository.findTagInfoByCriteria(criteria);

        Assert.assertNotNull(result);
    }
}