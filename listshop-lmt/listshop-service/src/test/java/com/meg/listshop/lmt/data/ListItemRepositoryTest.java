package com.meg.listshop.lmt.data;

import com.meg.listshop.Application;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.data.entity.ListItemDetailEntity;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.repository.ListItemDetailRepository;
import com.meg.listshop.lmt.data.repository.ListItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;


@Testcontainers
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class ListItemRepositoryTest {

    @Container
    public static ListShopPostgresqlContainer postgresSQLContainer = ListShopPostgresqlContainer.getInstance();

    @Autowired
    private ListItemRepository repository;

    @Autowired
    private ListItemDetailRepository detailRepository;

    @Test
    void blowUpTest() {
        int origCount = repository.findAll().size();
        ListItemEntity newItem = new ListItemEntity();

        ListItemEntity savedItem = repository.save(newItem);
        List<ListItemEntity> items = repository.findAll();
        Assertions.assertEquals(origCount + 1, items.size(), "one more item should be found");

        ListItemEntity retrievedItem = repository.findWithDetailsById(savedItem.getId()).get();
        Assertions.assertNotNull(retrievedItem);
        Assertions.assertNotNull(retrievedItem.getDetails());
        Assertions.assertTrue(retrievedItem.getDetails().isEmpty());


    }

    @Test
    void testDriveItemWithDetails() {
        ListItemEntity newItem = repository.save(new ListItemEntity());
        ListItemDetailEntity detail = testDetailEntity(newItem);
        ListItemDetailEntity detailToSave = detailRepository.save(detail);
        newItem.addDetailToItem(detailToSave);
        repository.save(newItem);

        ListItemEntity retrievedItem = repository.findWithDetailsById(newItem.getId()).get();
        Assertions.assertNotNull(retrievedItem, "item should not be null");
        Assertions.assertNotNull(retrievedItem.getDetails(), "details should not be null");
        Assertions.assertFalse(retrievedItem.getDetails().isEmpty(), "details should not be empty");
        Assertions.assertEquals(1, retrievedItem.getDetails().size(), "there should be one detail");


    }

    private ListItemDetailEntity testDetailEntity(ListItemEntity item) {
        ListItemDetailEntity detail = new ListItemDetailEntity();
        detail.setItem(item);
        return detail;
    }

}
