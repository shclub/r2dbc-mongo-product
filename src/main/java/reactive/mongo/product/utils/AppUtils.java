package reactive.mongo.product.utils;

import org.springframework.beans.BeanUtils;
import reactive.mongo.product.dto.ProductDto;
import reactive.mongo.product.entity.Product;

public class AppUtils {
    //Product => ProductDto
    public static ProductDto entityToDto(Product product){
        ProductDto productDto = new ProductDto();
        BeanUtils.copyProperties(product, productDto);
        return productDto;
    }

    //ProductDto => Product
    public static Product dtoToEntity(ProductDto productDto) {
        Product product = new Product();
        BeanUtils.copyProperties(productDto, product);
        return product;
    }
}