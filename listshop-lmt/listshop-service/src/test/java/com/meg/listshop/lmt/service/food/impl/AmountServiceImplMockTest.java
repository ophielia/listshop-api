package com.meg.listshop.lmt.service.food.impl;

import com.meg.listshop.auth.service.UserPropertyService;
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
    @MockBean
    UserPropertyService userPropertyService;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        this.amountService = new AmountServiceImpl(
                amountRepository,
                userPropertyService
        );

        // set generic ids
        Set<String> toCombine = Set.of("extra", "xtra", "firmly", "loosely");
        Field genericField = this.amountService.getClass()
                .getDeclaredField("TAKES_NEXT_TOKEN");
        genericField.setAccessible(true);
        genericField.set(this.amountService, toCombine);
    }



}