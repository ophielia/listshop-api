package com.meg.listshop.lmt.api.model;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

public interface ListShopResource {

    ListShopModel fillLinks(HttpServletRequest request, ListShopModel model);

    URI selfLink(HttpServletRequest request, ListShopModel model);
}
