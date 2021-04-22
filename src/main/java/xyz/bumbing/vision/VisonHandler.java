package xyz.bumbing.vision;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.vision.CloudVisionTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.google.api.client.util.Base64;

import reactor.core.publisher.Mono;

@RestController
public class VisonHandler {
    private static final Logger log = LoggerFactory.getLogger(VisonHandler.class);

    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private CloudVisionTemplate cloudVisionTemplate;

    Mono<ServerResponse> textDetection(ServerRequest request) {
	return request.bodyToMono(VisionRequest.class)
		.switchIfEmpty(Mono.error(new IllegalStateException("json required"))).flatMap(vision -> {
		    String retVal = "";
		    try {
			retVal = this.cloudVisionTemplate
				.extractTextFromImage(resourceLoader.getResource(vision.getImageSource()));
		    } catch (Exception e) {
			log.error("detect error", e);
		    }

		    return ServerResponse.ok().bodyValue(retVal);
		}).onErrorResume(err -> {
		    err.printStackTrace();

		    return ServerResponse.badRequest().build();
		});
    }

    Mono<ServerResponse> textDetectionBase(ServerRequest request) {
	return request.bodyToMono(VisionRequest.class)
		.switchIfEmpty(Mono.error(new IllegalStateException("json required"))).flatMap(vision -> {
		    String resp = "";
		    try {
			String imageSource = vision.getImageSource();
			if (imageSource.contains("base64")) {
			    imageSource = imageSource.split(",")[1];
			    resp = this.cloudVisionTemplate
				    .extractTextFromImage(new ByteArrayResource((Base64.decodeBase64(imageSource))));
			}

		    } catch (Exception e) {
			log.error("detect error", e);
		    }

		    return ServerResponse.ok().bodyValue(resp);
		}).onErrorResume(err -> {
		    err.printStackTrace();

		    return ServerResponse.badRequest().build();
		});
    }

    Pattern pattern = Pattern.compile("[A-Z]+");

    Mono<ServerResponse> textDetectionCap(ServerRequest request) {
	return request.bodyToMono(VisionRequest.class)
		.switchIfEmpty(Mono.error(new IllegalStateException("json required"))).flatMap(vision -> {
		    String resp = "";
		    try {
			String imageSource = vision.getImageSource();
			if (imageSource.contains("base64")) {
			    imageSource = imageSource.split(",")[1];
			    String extract = this.cloudVisionTemplate
				    .extractTextFromImage(new ByteArrayResource((Base64.decodeBase64(imageSource))));
			    Matcher matcher = pattern.matcher(extract);
			    while (matcher.find()) {
				resp += matcher.group();
			    }
			}

		    } catch (Exception e) {
			log.error("detect error", e);
		    }

		    return ServerResponse.ok().bodyValue(resp);
		}).onErrorResume(err -> {
		    err.printStackTrace();

		    return ServerResponse.badRequest().build();
		});
    }

}
