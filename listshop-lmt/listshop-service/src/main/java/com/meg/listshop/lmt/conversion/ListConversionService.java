package com.meg.listshop.lmt.conversion;

import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.data.entity.DishItemEntity;
import com.meg.listshop.lmt.data.entity.ListItemDetailEntity;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.list.state.ItemStateContext;
import jakarta.validation.constraints.NotNull;

/**
 * Created by margaretmartin on 30/10/2017.
 */

public interface ListConversionService {

    ConvertibleAmount convertDishItemForList(DishItemEntity dishItem, ListItemDetailEntity existing, ListItemEntity item) throws ConversionPathException, ConversionFactorException;

    void sumItemDetails(ListItemEntity item, @NotNull ItemStateContext context) throws ItemProcessingException;


    QuantityElements splitQuantityIntoElements(Double amount);

    ConvertibleAmount convertListItemDetailForList(ListItemDetailEntity detailToAdd, ListItemDetailEntity existingDetail, ListItemEntity parentItem) throws ConversionPathException, ConversionFactorException;
}
