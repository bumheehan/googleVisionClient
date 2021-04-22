package xyz.bumbing.vision;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

@Configuration
public class VisonRouter {
    @Bean
    RouterFunction<?> routes(VisonHandler vh) {

	return RouterFunctions
		.route(RequestPredicates.POST("/detecttext/uri")
			.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), vh::textDetection)
		.andRoute(RequestPredicates.POST("/detecttext/base")
			.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), vh::textDetectionBase)
		.andRoute(RequestPredicates.POST("/detecttext/cap")
			.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), vh::textDetectionCap);

    }
}
