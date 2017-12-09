package com.meg.atable.service;

import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.ShadowTags;

import java.util.List;

/**
 * Created by margaretmartin on 08/12/2017.
 */
public interface Instruction {


    Long getTagIdToAssign(AutoTagSubject subject);
}
