package api;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class Specification2 {
    public static RequestSpecification getHeaderSpec() {
        return new RequestSpecBuilder()
                .addHeader("api_key", "special-key")
                .build();
    }
}
