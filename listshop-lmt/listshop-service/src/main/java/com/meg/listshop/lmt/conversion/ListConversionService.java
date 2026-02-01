package com.meg.listshop.lmt.conversion;

import com.meg.listshop.conversion.data.pojo.DomainType;
import com.meg.listshop.conversion.exceptions.ConversionAddException;
import com.meg.listshop.conversion.exceptions.ConversionFactorException;
import com.meg.listshop.conversion.exceptions.ConversionPathException;
import com.meg.listshop.conversion.service.ConvertibleAmount;
import com.meg.listshop.lmt.api.exception.ItemProcessingException;
import com.meg.listshop.lmt.data.entity.DishItemEntity;
import com.meg.listshop.lmt.data.entity.ListItemDetailEntity;
import com.meg.listshop.lmt.data.entity.ListItemEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.list.state.ItemStateContext;
import jakarta.validation.constraints.NotNull;

/**
 * Created by margaretmartin on 30/10/2017.
 */

public interface ListConversionService {

    ConvertibleAmount convertDishItemForList(DishItemEntity dishItem, ListItemDetailEntity existing, ListItemEntity item, DomainType userDomain) throws ConversionPathException, ConversionFactorException;

    void sumItemDetails(ListItemEntity item, @NotNull ItemStateContext context) throws ItemProcessingException;

    ConvertibleAmount convertListItemDetailForList(ListItemDetailEntity detailToAdd, ListItemDetailEntity existingDetail,
                                                   ListItemEntity parentItem, DomainType domainType) throws ConversionPathException, ConversionFactorException;

    ConvertibleAmount convertTagForList(TagEntity tag, BasicAmount tagAmount, ListItemDetailEntity existing, ListItemEntity item, DomainType domainType) throws ConversionPathException, ConversionFactorException;

    ConvertibleAmount addToListItemDetail(ConvertibleAmount converted, ListItemDetailEntity existing, @NotNull ItemStateContext context) throws ConversionPathException, ConversionAddException, ConversionFactorException;
}
