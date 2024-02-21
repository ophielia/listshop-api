package com.meg.listshop.lmt.data.repository.impl;

import com.meg.listshop.lmt.api.model.TagType;
import com.meg.listshop.lmt.data.pojos.IncludeType;
import com.meg.listshop.lmt.data.pojos.TagInfoDTO;
import com.meg.listshop.lmt.data.pojos.TagSearchCriteria;
import com.meg.listshop.lmt.data.repository.CustomTagInfoRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test-jpa")
public class CustomTagRepositoryImplTest {



    @Autowired
    private CustomTagInfoRepository repository;
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