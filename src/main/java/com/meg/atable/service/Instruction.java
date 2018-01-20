package com.meg.atable.service;

import com.meg.atable.service.tag.AutoTagSubject;

/**
 * Created by margaretmartin on 08/12/2017.
 */
public interface Instruction {


    Long getTagIdToAssign(AutoTagSubject subject);
}
