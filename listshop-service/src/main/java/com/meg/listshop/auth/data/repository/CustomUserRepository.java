package com.meg.listshop.auth.data.repository;

import java.time.LocalDate;
import java.util.Map;

public interface CustomUserRepository {

    void deleteUser(Long userId);

    Map<String, Integer> getLoggedInUserCountByClient(LocalDate dateLimit);

    Integer getLoggedInUserCount(LocalDate dateLimit);

    Integer getActiveUserCount(LocalDate dateLimit);

    Integer getTotalUserCount();
}
