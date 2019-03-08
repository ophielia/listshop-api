package com.meg.atable.lmt.service.tag;

/**
 * Created by margaretmartin on 08/12/2017.
 */
public interface AutoTagProcessor {

    AutoTagSubject autoTagSubject(AutoTagSubject subject);

   Long getProcessIdentifier();

    class Type {
        public static final Long TAG = 3L;
        public static final Long TEXT = 5L;
        public static final Long RATING = 7L;
        private Type() {
            throw new IllegalStateException("Utility class");
        }
    }
}
