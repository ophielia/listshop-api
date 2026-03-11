/*
 * The List Shop
 *
 * Copyright (c) 2026.
 */

package com.meg.listshop.lmt.list;

import com.meg.listshop.common.DateUtils;
import com.meg.listshop.common.StringTools;
import com.meg.listshop.lmt.api.exception.ActionInvalidException;
import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.api.exception.ObjectNotFoundException;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.ItemChangeRepository;
import com.meg.listshop.lmt.data.entity.*;
import com.meg.listshop.lmt.data.pojos.ItemMappingDTO;
import com.meg.listshop.lmt.data.pojos.LongTagIdPairDTO;
import com.meg.listshop.lmt.data.pojos.ShoppingListDTO;
import com.meg.listshop.lmt.data.repository.ItemRepository;
import com.meg.listshop.lmt.data.repository.ShoppingListRepository;
import com.meg.listshop.lmt.dish.DishService;
import com.meg.listshop.lmt.list.state.ItemStateContext;
import com.meg.listshop.lmt.list.state.ListItemEvent;
import com.meg.listshop.lmt.list.state.ListItemStateMachine;
import com.meg.listshop.lmt.service.*;
import com.meg.listshop.lmt.service.tag.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
@Transactional(rollbackFor = ItemProcessingException.class)
public class BaseShoppingListService  {

}
