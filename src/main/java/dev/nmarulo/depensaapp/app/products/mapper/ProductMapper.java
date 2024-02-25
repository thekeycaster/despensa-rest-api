package dev.nmarulo.depensaapp.app.products.mapper;

import dev.nmarulo.depensaapp.app.products.Product;
import dev.nmarulo.depensaapp.app.products.classes.ProductReq;
import dev.nmarulo.depensaapp.app.products.classes.ProductRes;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {
    ProductRes toProductRes(Product request);
    List<ProductRes> toProductResCollection(List<Product> request);
    Product toProduct(ProductReq request);
}
