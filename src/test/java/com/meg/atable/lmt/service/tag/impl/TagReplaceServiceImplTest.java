package com.meg.atable.lmt.service.tag.impl;

import com.meg.atable.Application;
import com.meg.atable.lmt.service.tag.TagReplaceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Sql(value = {"/sql/com/meg/atable/lmt/service/MergeItemCollectorTest-rollback.sql",
        "/sql/com/meg/atable/lmt/service/MergeItemCollectorTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/com/meg/atable/lmt/service/MergeItemCollectorTest-rollback.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class TagReplaceServiceImplTest {

    @Autowired
    private TagReplaceService tagReplaceService;


    @Test
    public void testReplaceTag() {
        // call to test
        tagReplaceService.replaceTag(999L, 13L);
        // verifications afterwards
    }

}