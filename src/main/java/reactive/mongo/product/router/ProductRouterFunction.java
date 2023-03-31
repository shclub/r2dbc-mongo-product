package reactive.mongo.product.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactive.mongo.product.Handler.ProductHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ProductRouterFunction {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(ProductHandler productHandler) {
        return RouterFunctions.route(GET("/router/products"), productHandler::selectAllProduct)
                .andRoute(GET("/router/products/{id}"), productHandler::selectProduct)
                .andRoute(GET("/router/products/product-range"), productHandler::selectProductInRange)
                .andRoute(POST("/router/products/{id}"), productHandler::insertProduct)
                .andRoute(PUT("/router/products/{id}"), productHandler::updateProduct)
                .andRoute(DELETE("/router/products/{id}"), productHandler::deleteProduct);
    }
}
