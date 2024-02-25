package dev.nmarulo.depensaapp.app.shoppinglist.mapper;

import dev.nmarulo.depensaapp.app.shoppinglist.ShoppingList;
import dev.nmarulo.depensaapp.app.shoppinglist.classes.ShoppingListReq;
import dev.nmarulo.depensaapp.app.shoppinglist.classes.ShoppingListRes;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ShoppingListMapper {
    
    ShoppingListRes mapperTo(ShoppingList request);
    List<ShoppingListRes> mapperTo(List<ShoppingList> request);
    ShoppingList mapperTo(ShoppingListReq request);
    
}
