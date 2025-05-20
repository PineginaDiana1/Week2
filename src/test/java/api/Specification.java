package api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class Specification {

        public static RequestSpecification requestSpecification(String url){
            return new RequestSpecBuilder()
                    .setBaseUri(url)
                    .setContentType(ContentType.JSON)
                    .build();
        }


        public static ResponseSpecification responseSpecification200(){
            return new ResponseSpecBuilder()
                    .expectStatusCode(200)
                    .expectContentType(ContentType.JSON)
                    .build();
        }

        public static ResponseSpecification responseSpecification404(){
            return new ResponseSpecBuilder()
                    .expectStatusCode(404)
                    .expectContentType(ContentType.JSON)
                    .build();
        }


        public static void installSpec(ResponseSpecification response
        ){
            RestAssured.responseSpecification = response;
        }
    }
