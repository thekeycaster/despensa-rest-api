package dev.nmarulo.depensaapp.app.shoppinglist;

import dev.nmarulo.depensaapp.app.productshoppinglist.ProductHasShoppingList;
import dev.nmarulo.depensaapp.app.shoppinglist.classes.IndexByIdShoppingListRes;
import dev.nmarulo.depensaapp.app.shoppinglist.classes.IndexShoppingListRes;
import dev.nmarulo.depensaapp.app.shoppinglist.classes.ShoppingListReq;
import dev.nmarulo.depensaapp.app.shoppinglist.classes.ShoppingListRes;
import dev.nmarulo.depensaapp.commons.exception.NotFoundException;
import dev.nmarulo.depensaapp.commons.service.CrudServiceImp;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
public class ShoppingListService extends CrudServiceImp<ShoppingListReq, ShoppingListRes, ShoppingList, Integer> {
    
    private final ShoppingListRepository repository;
    
    public IndexShoppingListRes index() {
        var response = new IndexShoppingListRes();
        var pageFindAll = this.repository.findAll(getDataRequestScope().getPageable());
        getModelMapper().map(pageFindAll, response);
        return response;
    }
    
    public IndexByIdShoppingListRes indexById(Integer id) {
        var findById = this.repository.findById(id);
        
        if (findById.isEmpty()) {
            throw new NotFoundException(getLocalMessage().getMessage("error.record-not-exist"));
        }
        
        var response = new IndexByIdShoppingListRes();
        var shoppingList = findById.get();
        var productsRes = shoppingList.getProductHasShoppingList()
                                      .stream()
                                      .map(this::mapperTo)
                                      .toList();
        
        response.setId(shoppingList.getId());
        response.setName(shoppingList.getName());
        response.setItems(productsRes);
        
        return response;
    }
    
    private IndexByIdShoppingListRes.Item mapperTo(ProductHasShoppingList productHasShoppingList) {
        var response = new IndexByIdShoppingListRes.Item();
        
        var productRes = getModelMapper().map(productHasShoppingList, IndexByIdShoppingListRes.Item.Product.class);
        var unitTypeRes = getModelMapper().map(productHasShoppingList, IndexByIdShoppingListRes.Item.UnitType.class);
        
        getModelMapper().map(productRes, response);
        getModelMapper().map(unitTypeRes, response);
        getModelMapper().map(productHasShoppingList, response);
        
        return response;
    }
}
