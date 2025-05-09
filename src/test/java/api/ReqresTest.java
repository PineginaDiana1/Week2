package api;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class ReqresTest {
    private final static String URL = "https://reqres.in/";

    @Test
     public void checkAvatarAndIdTest(){

        Spec.installSpec(Spec.requestSpecification(URL), Spec.responseSpecification200());

        List<UserData> users = given()
                .when()
                .get("api/users?page=2")
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserData.class);

        users.forEach(x-> Assert.assertTrue(x.getAvatar().contains(x.getId().toString())));

        Assert.assertTrue(users.stream().allMatch(x->x.getEmail().endsWith("@reqres.in")));

        List<String> avatars = users.stream().map(UserData::getAvatar).collect(Collectors.toList());

        List<String> ids = users.stream().map(x->x.getId().toString()).collect(Collectors.toList());
        for (int i = 0; i < avatars.size(); i++){
            Assert.assertTrue(avatars.get(i).contains(ids.get(i)));
        }
    }

    @Test

    public void successRegTest(){
        Spec.installSpec(Spec.requestSpecification(URL), Spec.responseSpecification200());

        Integer id = 4;
        String token = "QpwL5tke4Pnpja7X4";
        Reg user = new Reg("eve.holt@reqres.in", "pistol");

        SuccessReg sucreg = given()
                .header("x-api-key", "reqres-free-v1")
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(SuccessReg.class);

        Assert.assertNotNull(sucreg.getId());
        Assert.assertNotNull(sucreg.getToken());
        Assert.assertEquals(id, sucreg.getId());
        Assert.assertEquals(token, sucreg.getToken());

    }

    @Test

    public void unSuccessRegTest(){
        Spec.installSpec(Spec.requestSpecification(URL), Spec.responseSpecification400());

        Reg user = new Reg("sydney@fife", "");

        UnSuccessReg unSuccessReg = given()
                .header("x-api-key", "reqres-free-v1")
                .body(user)
                .post("api/register")
                .then().log().all()
                .extract().as(UnSuccessReg.class);

        Assert.assertEquals("Missing password", unSuccessReg.getError());


    }

    @Test

    public void sortedYearsTest(){
        Spec.installSpec(Spec.requestSpecification(URL), Spec.responseSpecification200());

        List<Colors> colors = given()
                .when()
                .get("api/unknown")
                .then().log().all()
                .extract().body().jsonPath().getList("data", Colors.class);

        List<Integer> years = colors.stream().map(Colors::getYear).collect(Collectors.toList());
        List<Integer> sortedYears = years.stream().sorted().collect(Collectors.toList());
        Assert.assertEquals(sortedYears, years);

        System.out.println(years);
        System.out.println(sortedYears);
    }

    @Test
    public void delete(){
        Spec.installSpec(Spec.requestSpecification(URL), Spec.responseSpecification204());

        given()
                .header("x-api-key", "reqres-free-v1")
                .when()
                .delete("api/users/2")
                .then().log().all();
    }

//
    @Test
    public void timeTest() {
        Spec.installSpec(Spec.requestSpecification(URL), Spec.responseSpecification200());

        // Создаем объект с тестовыми данными
        UserTime user = new UserTime("morpheus", "zion resident");

        // Фиксируем время ДО отправки запроса
        Instant startTime = Instant.now();

        UserTimeResponse response = given()
                .header("x-api-key", "reqres-free-v1")
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .put("api/users/2")
                .then()
                .log().all()
                .extract()
                .as(UserTimeResponse.class);

        // Фиксируем время ПОСЛЕ получения ответа
        Instant endTime = Instant.now();

        // Парсим время из ответа сервера
        Instant updatedAt = Instant.parse(response.getUpdatedAt());

        // Проверяем, что время в ответе находится между startTime и endTime
        Assert.assertTrue(
                !updatedAt.isBefore(startTime) && !updatedAt.isAfter(endTime),
                "Время в ответе (" + updatedAt + ") должно быть между " +
                        startTime + " и " + endTime
        );
    }

}
