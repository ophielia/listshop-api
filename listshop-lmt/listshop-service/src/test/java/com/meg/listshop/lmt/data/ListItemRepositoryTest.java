package com.meg.listshop.lmt.data;

import com.meg.listshop.Application;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.data.entity.ListItemDetailEntity;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.repository.ListItemDetailRepository;
import com.meg.listshop.lmt.data.repository.ListItemRepository;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class ListItemRepositoryTest {

    @ClassRule
    public static ListShopPostgresqlContainer postgresSQLContainer = ListShopPostgresqlContainer.getInstance();

    @Autowired
    private ListItemRepository repository;

    @Autowired
    private ListItemDetailRepository detailRepository;

    @Test
    public void blowUpTest() {
        int origCount = repository.findAll().size();
        ListItemEntity newItem = new ListItemEntity();

        ListItemEntity savedItem = repository.save(newItem);
        List<ListItemEntity> items = repository.findAll();
        Assert.assertEquals("one more item should be found", origCount + 1, items.size());

        ListItemEntity retrievedItem = repository.findWithDetailsById(savedItem.getId()).get();
        Assert.assertNotNull(retrievedItem);
        Assert.assertNotNull(retrievedItem.getDetails());
        Assert.assertTrue(retrievedItem.getDetails().isEmpty());


    }

    @Test
    public void testDriveItemWithDetails() {
        ListItemEntity newItem = repository.save(new ListItemEntity());
        ListItemDetailEntity detail = testDetailEntity(newItem);
        ListItemDetailEntity detailToSave = detailRepository.save(detail);
        newItem.addDetailToItem(detailToSave);
        repository.save(newItem);

        ListItemEntity retrievedItem = repository.findWithDetailsById(newItem.getId()).get();
        Assert.assertNotNull("item should not be null", retrievedItem);
        Assert.assertNotNull("details should not be null", retrievedItem.getDetails());
        Assert.assertFalse("details should not be empty", retrievedItem.getDetails().isEmpty());
        Assert.assertEquals("there should be one detail", 1,retrievedItem.getDetails().size());


    }

    private ListItemDetailEntity testDetailEntity(ListItemEntity item) {
        ListItemDetailEntity detail = new ListItemDetailEntity();
        detail.setItem(item);
        return detail;
    }

}
