package api;

import org.testng.annotations.Test;

import java.io.File;
import java.util.List;


import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

public class PetTest {
    private final static String URL = "https://petstore.swagger.io/v2/";

    @Test
    public void unsuccessfulDeletePet() {
        Specification.installSpec(
                Specification.requestSpecification(URL),
                Specification.responseSpecification404());

        given()
                .spec(Specification2.getHeaderSpec())
                .when()
                .delete("pet/9223372036854776000")
                .then().log().all();
    }

    @Test
    public void findPetById() {
        Specification.installSpec(
                Specification.requestSpecification(URL),
                Specification.responseSpecification200());
        Long id = Long.valueOf(3);
        String status = "6000";

        PetData pet = given()
                .spec(Specification2.getHeaderSpec())
                .when()
                .get("pet/3")
                .then().log().all()
                .extract().as(PetData.class);

        assertThat(pet)
                .isNotNull()
                .satisfies(p -> {
                    assertThat(p.getName()).isNotNull();
                    assertThat(p.getId()).isNotNull().isEqualTo(id);
                    assertThat(p.getStatus()).isEqualTo(status);
                });
    }

    @Test
    public void findPetByStatus() {
        Specification.installSpec(
                Specification.requestSpecification(URL),
                Specification.responseSpecification200());
        String status = "available";

        PetData[] pets = given()
                .spec(Specification2.getHeaderSpec())
                .when()
                .get("pet/findByStatus?status=" + status)
                .then().log().all()
                .extract().as(PetData[].class);

        assertThat(pets)
                .isNotEmpty()
                .allSatisfy(pet ->
                        assertThat(pet.getStatus()).isEqualTo(status)
                );
    }

    @Test
    public void addNewPet() {
        Specification.installSpec(
                Specification.requestSpecification(URL),
                Specification.responseSpecification200());

        PetData petRequest = new PetData()
                .setId(0L)
                .setName("doggie")
                .setStatus("available")
                .setCategory(new Category()
                        .setId(0L)
                        .setName("string"))
                .setPhotoUrls(List.of("https://example.com/dog.jpg"))
                .setTags(List.of(new Tag()
                        .setId(0L)
                        .setName("cute")));

        PetData petResponse = given()
                .spec(Specification2.getHeaderSpec())
                .body(petRequest)
                .when()
                .post("pet")
                .then()
                .log().all()
                .extract().as(PetData.class);

        assertThat(petResponse)
                .isNotNull()
                .satisfies(p -> {
                    assertThat(p.getName()).isEqualTo("doggie");
                    assertThat(p.getStatus()).isEqualTo("available");
                    assertThat(p.getId()).isNotNull();
                    assertThat(p.getPhotoUrls()).isNotEmpty();
                });
    }

    @Test
    public void updateExistingPet() {
        Specification.installSpec(
                Specification.requestSpecification(URL),
                Specification.responseSpecification200());

        PetData petRequest = new PetData()
                .setId(1L)
                .setName("test")
                .setStatus("sold")
                .setCategory(new Category()
                        .setId(0L)
                        .setName("string"))
                .setPhotoUrls(List.of("https://example.com/dog.jpg"))
                .setTags(List.of(new Tag()
                        .setId(0L)
                        .setName("cute")));

        PetData petResponse = given()
                .spec(Specification2.getHeaderSpec())
                .body(petRequest)
                .when()
                .put("pet")
                .then()
                .log().all()
                .extract().as(PetData.class);

        assertThat(petResponse)
                .isNotNull()
                .satisfies(p -> {
                    assertThat(p.getName()).isEqualTo("test");
                    assertThat(p.getStatus()).isEqualTo("sold");
                    assertThat(p.getId()).isEqualTo(petRequest.getId());
                    assertThat(p.getPhotoUrls()).isNotEmpty();
                });
    }

    @Test
    public void uploadImageToPet() {
        Specification.installSpec(
                Specification.requestSpecification(URL),
                Specification.responseSpecification200());

        File imageFile = new File("src/test/resources/dog.jpg");

        given()
                .spec(Specification2.getHeaderSpec())
                .contentType("multipart/form-data")
                .multiPart("file", imageFile)
                .when()
                .post("pet/1/uploadImage")
                .then()
                .log().all();
    }

    @Test
    public void updatePetWithFormData() {
        Specification.installSpec(
                Specification.requestSpecification(URL),
                Specification.responseSpecification404());

        given()
                .spec(Specification2.getHeaderSpec())
                .contentType("application/x-www-form-urlencoded")
                .formParam("name", "test")
                .formParam("status", "sold")
                .when()
                .post("pet/999999999999")
                .then()
                .log().all();
    }
}