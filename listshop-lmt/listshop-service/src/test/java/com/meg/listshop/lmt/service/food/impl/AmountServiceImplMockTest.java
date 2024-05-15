package com.meg.listshop.lmt.service.food.impl;

import com.meg.listshop.lmt.data.repository.AmountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class AmountServiceImplMockTest {
    private AmountServiceImpl amountService;

    @MockBean
    AmountRepository amountRepository;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        this.amountService = new AmountServiceImpl(
                amountRepository
        );

        // set generic ids
        Set<String> toCombine = Set.of("extra", "xtra", "firmly", "loosely");
        Field genericField = this.amountService.getClass()
                .getDeclaredField("TAKES_NEXT_TOKEN");
        genericField.setAccessible(true);
        genericField.set(this.amountService, toCombine);
    }

    @Test
    void testPullModifierTokens_HappyPath() {
        String modifierTokens = "extra large chopped";

        List<String> results = amountService.pullModifierTokens(modifierTokens);

        Assertions.assertNotNull(results);
        Assertions.assertFalse(results.isEmpty());
        Assertions.assertEquals(2, results.size());
    }

    @Test
    void testPullModifierTokens_Spaces() {
        String modifierTokens = "extra       large      chopped        ";

        List<String> results = amountService.pullModifierTokens(modifierTokens);

        Assertions.assertNotNull(results);
        Assertions.assertFalse(results.isEmpty());
        Assertions.assertEquals(2, results.size());
    }


    @Test
    void testPullModifierTokens_Commas() {
        String modifierTokens = "extra   ,,    large,      chopped,        ";

        List<String> results = amountService.pullModifierTokens(modifierTokens);

        Assertions.assertNotNull(results);
        Assertions.assertFalse(results.isEmpty());
        Assertions.assertEquals(2, results.size());
    }

    @Test
    void testPullModifierTokens_PrefixAtEnd() {
        String modifierTokens = "chopped large extra";

        List<String> results = amountService.pullModifierTokens(modifierTokens);

        Assertions.assertNotNull(results);
        Assertions.assertFalse(results.isEmpty());
        Assertions.assertEquals(3, results.size());
    }

    @Test
    void testPullModifierTokens_Null() {
        String modifierTokens = null;

        List<String> results = amountService.pullModifierTokens(modifierTokens);

        Assertions.assertNotNull(results);
        Assertions.assertTrue(results.isEmpty());
    }


    @Test
    void testPullModifierTokens_Empty() {
        String modifierTokens = "";

        List<String> results = amountService.pullModifierTokens(modifierTokens);

        Assertions.assertNotNull(results);
        Assertions.assertTrue(results.isEmpty());
    }

}