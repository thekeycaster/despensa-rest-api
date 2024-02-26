package dev.nmarulo.depensaapp.app.products;

import dev.nmarulo.depensaapp.app.products.classes.*;
import dev.nmarulo.depensaapp.app.productshoppinglist.ProductHasShoppingList;
import dev.nmarulo.depensaapp.app.productshoppinglist.ProductHasShoppingListPK;
import dev.nmarulo.depensaapp.app.productshoppinglist.ProductHasShoppingListRepository;
import dev.nmarulo.depensaapp.app.shoppinglist.ShoppingList;
import dev.nmarulo.depensaapp.app.shoppinglist.ShoppingListRepository;
import dev.nmarulo.depensaapp.app.unitytypes.UnitType;
import dev.nmarulo.depensaapp.app.unitytypes.UnitTypeRepository;
import dev.nmarulo.depensaapp.commons.exception.BadRequestException;
import dev.nmarulo.depensaapp.commons.exception.NotFoundException;
import dev.nmarulo.depensaapp.commons.service.CrudServiceImp;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Getter
public class ProductService extends CrudServiceImp<ProductReq, ProductRes, Product, Integer> {
    
    private final ProductRepository repository;
    
    private final ShoppingListRepository shoppingListRepository;
    
    private final UnitTypeRepository unitTypeRepository;
    
    private final ProductHasShoppingListRepository productHasShoppingListRepository;
    
    public IndexProductRes index(Integer excludeShoppingListId) {
        var response = new IndexProductRes();
        //Obtener todos los productos que no estén en la lista de compra actual.
        var pageFindAll = this.repository.findAllByIdNotInShoppingList(excludeShoppingListId, getDataRequestScope().getPageable());
        
        var products = pageFindAll.stream()
                                  .map(this::mapperTo)
                                  .toList();
        
        response.setContent(products);
        response.setCurrentPage(pageFindAll.getNumber());
        response.setPageSize(pageFindAll.getNumberOfElements());
        response.setTotalPages(pageFindAll.getTotalPages());
        response.setTotal(pageFindAll.getTotalElements());
        
        return response;
    }
    
    private IndexProductRes.Product mapperTo(Product product) {
        var response = new IndexProductRes.Product();
        
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        
        return response;
    }
    
    public IndexShoppingListProductRes indexShoppingList(IndexShoppingListProductReq request) {
        var productOptional = this.repository.findById(request.getProductId());
        var shoppingListOptional = this.shoppingListRepository.findById(request.getShoppingListId());
        var unityTipeOptional = this.unitTypeRepository.findById(request.getUnitTypeId());
        var isEmptyShoppingList = shoppingListOptional.isEmpty();
        var isEmptyProduct = productOptional.isEmpty();
        var isEmptyUnitType = unityTipeOptional.isEmpty();
        
        if (isEmptyProduct || isEmptyShoppingList || isEmptyUnitType) {
            throw new NotFoundException(getLocalMessage().getMessage("error.record-not-exist"));
        }
        
        var productHasShoppingList = getEntity(request, productOptional.get());
        
        //TODO: Como el "save" no me retorna las FK estoy consultando previamente cada uno de los campos.
        var productHasShoppingListSave = this.productHasShoppingListRepository.save(productHasShoppingList);
        
        return mapperTo(productOptional.get(), shoppingListOptional.get(), unityTipeOptional.get(), productHasShoppingListSave);
    }
    
    private IndexShoppingListProductRes mapperTo(Product product, ShoppingList shoppingList, UnitType unitType, ProductHasShoppingList productHasShoppingListSave) {
        var response = new IndexShoppingListProductRes();
        
        var shoppingListRes = new IndexShoppingListProductRes.ShoppingList(shoppingList.getId(), shoppingList.getName());
        var unitTypeRes = new IndexShoppingListProductRes.UnitType(unitType.getId(), unitType.getName());
        var productRes = new IndexShoppingListProductRes.Product(product.getId(), product.getName(), product.getPrice());
        
        response.setShoppingList(shoppingListRes);
        response.setProduct(productRes);
        response.setUnitType(unitTypeRes);
        response.setTotalPrice(productHasShoppingListSave.getTotalPrice());
        response.setUnitsPerProduct(productHasShoppingListSave.getUnitsPerProduct());
        
        return response;
    }
    
    private ProductHasShoppingList getEntity(IndexShoppingListProductReq request, Product product) {
        var productHasShoppingListPK = new ProductHasShoppingListPK();
        var productHasShoppingList = new ProductHasShoppingList();
        
        productHasShoppingListPK.setProductId(request.getProductId());
        productHasShoppingListPK.setShoppingListId(request.getShoppingListId());
        productHasShoppingListPK.setUnitTypeId(request.getUnitTypeId());
        
        throwIsExisteProductInShoppingList(productHasShoppingListPK);
        
        var totalPrice = multiply(product.getPrice(), request.getUnitsPerProduct());
        var totalCalories = multiply(product.getCalories(), request.getUnitsPerProduct());
        
        productHasShoppingList.setProductHasShoppingListPK(productHasShoppingListPK);
        productHasShoppingList.setUnitsPerProduct(request.getUnitsPerProduct());
        productHasShoppingList.setTotalCalories(totalCalories);
        productHasShoppingList.setTotalPrice(totalPrice);
        productHasShoppingList.setSelected(false);
        
        return productHasShoppingList;
    }
    
    private BigDecimal multiply(BigDecimal valueBigDecimal, Integer valueInteger) {
        return valueBigDecimal.multiply(new BigDecimal(valueInteger));
    }
    
    private void throwIsExisteProductInShoppingList(ProductHasShoppingListPK productHasShoppingListPK) {
        boolean isExistsProductShoppingList = this.productHasShoppingListRepository.existsById(productHasShoppingListPK);
        
        if (isExistsProductShoppingList) {
            throw new BadRequestException(getLocalMessage().getMessage("error.record-already-exist"));
        }
    }
}
