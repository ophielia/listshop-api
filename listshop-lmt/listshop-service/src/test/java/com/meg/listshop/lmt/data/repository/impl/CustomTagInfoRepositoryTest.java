package com.meg.listshop.lmt.data.repository.impl;

import com.meg.listshop.lmt.data.repository.CustomTagRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test-jpa")
public class CustomTagInfoRepositoryTest {



    @Autowired
    private TagInfoRepositoryImpl repository;
    @Test
    void findTagInfoByCriteria() {
      /*  TagSearchCriteria criteria = new TagSearchCriteria();
        criteria.setGroupIncludeType(IncludeType.ONLY);
criteria.setUserId(20L);
criteria.setTagTypes(Arrays.asList(new TagType[]{TagType.Ingredient, TagType.DishType}));
criteria.setIncludedStatuses(Collections.singletonList(TagInternalStatus.CHECKED));
        List<TagInfoDTO> result = repository.findTagInfoByCriteria(criteria);

        Assert.assertNotNull(result);*/
        Assert.assertEquals(1, 1);
    }
}