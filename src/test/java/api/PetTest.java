package api;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;


import static io.restassured.RestAssured.given;

public class PetTest {
    private final static String URL = "https://petstore.swagger.io/v2/";

    //Тест на получение 404 ошибки после удаления несуществующего объекта
    @Test
    public void unsuccessfulDeletePet(){
        Specification.installSpec(Specification.requestSpecification(URL), Specification.responseSpecification404());

        given()
                .header("api_key", "special-key")
                .when()
                .delete("pet/9223372036854776000")
                .then().log().all();
    }

    //Тест на успешный поиск по id (нестабильный)
    @Test
    public void findPetById(){
        Specification.installSpec(Specification.requestSpecification(URL), Specification.responseSpecification200());
        Long id = Long.valueOf(3);
        String status = "6000";

        PetData pets = given()
                .header("api_key", "special-key")
                .when()
                .get("pet/3")
                .then().log().all()
                .extract().as(PetData.class);

        Assert.assertNotNull(pets.getName());
        Assert.assertNotNull(pets.getId());
        Assert.assertEquals(id, pets.getId());
        Assert.assertEquals(status, pets.getStatus());
    }

    //Тест на успешный поиск по статусу
    @Test
    public void findPetByStatus(){
        Specification.installSpec(Specification.requestSpecification(URL), Specification.responseSpecification200());
        String status = "available";

        PetData[] pets = given()
                .header("api_key", "special-key")
                .when()
                .get("pet/findByStatus?status=" + status)
                .then().log().all()
                .extract().as(PetData[].class);

        for (PetData pet : pets) {
            Assert.assertEquals(status, pet.getStatus());
        }
    }

    //Тест на успешное добавление нового pet
    @Test
    public void addNewPet(){
        Specification.installSpec(Specification.requestSpecification(URL), Specification.responseSpecification200());

        PetData petRequest = new PetData();
        petRequest.setId(0L);
        petRequest.setName("doggie");
        petRequest.setStatus("available");

        PetData.Category category = new PetData.Category();
        category.setId(0L);
        category.setName("string");
        petRequest.setCategory(category);

        petRequest.setPhotoUrls(List.of("https://example.com/dog.jpg"));

        PetData.Tag tag = new PetData.Tag();
        tag.setId(0L);
        tag.setName("cute");
        petRequest.setTags(List.of(tag));

        PetData petResponse = given()
                .header("api_key", "special-key")
                .body(petRequest)
                .when()
                .post("pet")
                .then()
                .log().all()
                .extract().as(PetData.class);

        Assert.assertEquals("doggie", petResponse.getName());
        Assert.assertEquals("available", petResponse.getStatus());
        Assert.assertNotNull(petResponse.getId());
        Assert.assertFalse(petResponse.getPhotoUrls().isEmpty());

    }

    //Тест на успешное обновление данных существующего pet
    @Test
    public void updateExistingPet(){
        Specification.installSpec(Specification.requestSpecification(URL), Specification.responseSpecification200());

        PetData petRequest = new PetData();
        petRequest.setId(1L);
        petRequest.setName("test");
        petRequest.setStatus("sold");

        PetData.Category category = new PetData.Category();
        category.setId(0L);
        category.setName("string");
        petRequest.setCategory(category);

        petRequest.setPhotoUrls(List.of("https://example.com/dog.jpg"));

        PetData.Tag tag = new PetData.Tag();
        tag.setId(0L);
        tag.setName("cute");
        petRequest.setTags(List.of(tag));

        PetData petResponse = given()
                .header("api_key", "special-key")
                .body(petRequest)
                .when()
                .put("pet")
                .then()
                .log().all()
                .extract().as(PetData.class);

        Assert.assertEquals("test", petResponse.getName());
        Assert.assertEquals("sold", petResponse.getStatus());
        Assert.assertEquals(petRequest.getId(), petResponse.getId());
        Assert.assertFalse(petResponse.getPhotoUrls().isEmpty());
    }

    //Тест на успешную загрузку изображения к pet
    @Test
    public void uploadImageToPet(){
        Specification.installSpec(Specification.requestSpecification(URL), Specification.responseSpecification200());

        File imageFile = new File("src/test/resources/dog.jpg");

        given()
                .header("api_key", "special-key")
                .contentType("multipart/form-data")
                .multiPart("file", imageFile)
                .when()
                .post("pet/1/uploadImage")
                .then()
                .log().all();
    }

    //Тест на получение 404 ошибки после обновления несуществующего pet
    @Test
    public void updatePetWithFormData(){
        Specification.installSpec(Specification.requestSpecification(URL), Specification.responseSpecification404());


        String updatedName = "test";
        String updatedStatus = "sold";

        given()
                .header("api_key", "special-key")
                .contentType("application/x-www-form-urlencoded")
                .formParam("name", updatedName)
                .formParam("status", updatedStatus)
                .when()
                .post("pet/999999999999")
                .then()
                .log().all();

    }
}
