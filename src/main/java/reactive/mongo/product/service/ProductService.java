package reactive.mongo.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactive.mongo.product.dto.ProductDto;
import reactive.mongo.product.entity.Product;
import reactive.mongo.product.repository.ProductRepository;
import reactive.mongo.product.utils.AppUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repository;

    public Flux<ProductDto> selectAllProduct() {
        return repository.findAll()//Flux<Product>
                .map(AppUtils::entityToDto);
                //.map(product -> AppUtils.entityToDto(product));
    }

    public Mono<ResponseEntity<ProductDto>> selectProduct(String id) {
        return repository.findById(id) //Mono<Product>
                .map(product -> ResponseEntity.ok(AppUtils.entityToDto(product)))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public Mono<ResponseEntity<ProductDto>> insertProduct(Mono<ProductDto> productDtoMono) {
        return productDtoMono.map(AppUtils::dtoToEntity) //Mono<ProductDto> -> Mono<Product>
                .flatMap(repository::insert) //Mono<Product>
                .map(insProduct -> ResponseEntity.ok(AppUtils.entityToDto(insProduct))) //Mono<ResponseEntity<ProductDto>>
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()); //406
    }

    public Mono<ResponseEntity<ProductDto>> updateProduct(String id, Mono<ProductDto> productDtoMono){
        Mono<Product> unUpdatedProductMono = productDtoMono.map(AppUtils::dtoToEntity);
        Mono<ProductDto> updatedProductDtoMono = unUpdatedProductMono.flatMap(product ->
                repository.findById(id).flatMap(existProduct -> {
                    existProduct.setName(product.getName());
                    if (product.getQty() != 0) {
                        existProduct.setQty(product.getQty());
                    }
                    if (product.getPrice() != 0.0) {
                        existProduct.setPrice(product.getPrice());
                    }
                    return repository.save(existProduct).map(AppUtils::entityToDto);
                })
        );
        return updatedProductDtoMono.map(productDto -> ResponseEntity.ok(productDto))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    public Mono<ResponseEntity<Void>> deleteProduct(String id) {
        return repository.findById(id)
                .flatMap(existProduct -> repository.delete(existProduct)
                        .then(Mono.just(ResponseEntity.ok().<Void>build()))
                ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    public Flux<ProductDto> selectProductByName(String name){
        return repository.findByName(name).map(AppUtils::entityToDto);
    }

    public Flux<ProductDto> selectProductByPriceRange(double min, double max) {
        return repository.findByPriceBetween(Range.closed(min, max)).map(AppUtils::entityToDto);
    }
}
