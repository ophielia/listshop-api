package com.meg.atable.lmt.service;

import com.meg.atable.lmt.service.tag.AutoTagSubject;

/**
 * Created by margaretmartin on 08/12/2017.
 */
public interface Instruction {

    Long getAssignTagId();

    Long assignTag(AutoTagSubject subject);
}
