package reactive.mongo.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactive.mongo.product.dto.ProductDto;
import reactive.mongo.product.service.ProductService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;

    @GetMapping
    public Flux<ProductDto> getAllProduct() {
        return service.selectAllProduct();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProductDto>> getProduct(@PathVariable String id) {
        return service.selectProduct(id);
    }

    @PostMapping
    public Mono<ResponseEntity<ProductDto>> saveProduct(@RequestBody Mono<ProductDto> productDtoMono) {
        return service.insertProduct(productDtoMono).log();
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ProductDto>> modifyProduct(@PathVariable String id, @RequestBody Mono<ProductDto> productDtoMono) {
        return service.updateProduct(id, productDtoMono);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> removeProduct(@PathVariable String id){
        return service.deleteProduct(id);
    }

    @GetMapping("/name")
    public Flux<ProductDto> getProductByName(@RequestParam String name){
        return service.selectProductByName(name);
    }

    @GetMapping("/product-range")
    public Flux<ProductDto> getProductInRange(@RequestParam double min, @RequestParam double max){
        return service.selectProductByPriceRange(min, max);
    }

}
