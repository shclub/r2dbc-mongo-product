package reactive.mongo.product.Handler;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactive.mongo.product.dto.ProductDto;
import reactive.mongo.product.entity.Product;
import reactive.mongo.product.repository.ProductRepository;
import reactive.mongo.product.utils.AppUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductHandler {
    private final ProductRepository repository;

    private Mono<ServerResponse> response404 = ServerResponse.notFound().build();
    private Mono<ServerResponse> response406 = ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).build();

    public Mono<ServerResponse> selectAllProduct(ServerRequest request) {
        //Flux<Product> => Flux<ProductDto>
        Flux<ProductDto> productDtoFlux = repository.findAll().map(AppUtils::entityToDto);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(productDtoFlux, ProductDto.class);
    }

    public Mono<ServerResponse> selectProduct(ServerRequest request) {
        String id = request.pathVariable("id");
        return repository.findById(id) //Mono<Product>
                .map(AppUtils::entityToDto) //Mono<ProductDto>
                .flatMap(productDto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(productDto)))//Mono<ServerResponse>
                .switchIfEmpty(response404);
    }

    public Mono<ServerResponse> selectProductInRange(ServerRequest request) {
        //Optional<String> request.queryParam("min")
        double min = Double.parseDouble(request.queryParam("min").orElseGet(() -> Double.toString(Double.MIN_VALUE)));
        System.out.println("min = " + min);
        double max = Double.parseDouble(request.queryParam("max").orElseGet(() -> Double.toString(Double.MAX_VALUE)));
        System.out.println("max = " + max);

        Flux<ProductDto> productDtoFlux = repository.findByPriceBetween(Range.closed(min, max)).map(AppUtils::entityToDto);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(productDtoFlux, ProductDto.class)
                .switchIfEmpty(response404);
    }

    public Mono<ServerResponse> insertProduct(ServerRequest request) {
        //Flux<ProductDto> => Flux<Product>
        Mono<Product> unSavedProduct = request.bodyToMono(ProductDto.class).map(AppUtils::dtoToEntity);
        return unSavedProduct.flatMap(product ->
                    repository.insert(product).map(AppUtils::entityToDto) //Flux<Product> => Flux<ProductDto>
                            .flatMap(savedProductDto -> ServerResponse.accepted()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(savedProductDto)
                            )
                ).switchIfEmpty(response406);
    }

    public Mono<ServerResponse> updateProduct(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Product> unUpdatedProductMono = request.bodyToMono(ProductDto.class).map(AppUtils::dtoToEntity);

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
        return updatedProductDtoMono.flatMap(productDto ->
                ServerResponse.accepted().contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(productDto)
                ).switchIfEmpty(response404);
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        String id = request.pathVariable("id");
        return repository.findById(id)
                .flatMap(existProduct ->
                        ServerResponse.ok()
                                .build(repository.delete(existProduct))
                ).switchIfEmpty(response404);
    }

}
