package com.meg.atable.service;

import java.util.List;

/**
 * Created by margaretmartin on 08/12/2017.
 */
public interface AutoTagProcessor {

    AutoTagSubject autoTagSubject(AutoTagSubject subject);

    public class Type {
        public static final Long Tag=3L;
        public static final Long Text=5L;

    }
}
