package com.meg.listshop.lmt.service;

import com.meg.listshop.lmt.service.tag.AutoTagSubject;

/**
 * Created by margaretmartin on 08/12/2017.
 */
public interface Instruction {

    Long getAssignTagId();

    Long assignTag(AutoTagSubject subject);
}
