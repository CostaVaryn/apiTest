package tests;

import api.spec.Specifications;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReqresNoPojoTest {
    private final static String URL = "https://reqres.in/";

    @Test
    @DisplayName("Check avatars")
    public void checkAvatarsNoPojoTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Response response = given()
                .when()
                .get("api/users?page=2")
                .then().log().all()
                .body("page", equalTo(2))
                .body("data.id", notNullValue())
                .body("data.email", notNullValue())
                .body("data.first_name", notNullValue())
                .body("data.last_name", notNullValue())
                .body("data.avatar", notNullValue())
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        List<String> emails = jsonPath.get("data.email");
        List<Integer> ids = jsonPath.get("data.id");
        List<String> avatars = jsonPath.get("data.avatar");
        assertAll(
                () -> assertNotNull(emails),
                () -> assertNotNull(ids),
                () -> assertNotNull(avatars),
                () -> assertTrue(emails.stream().allMatch(x -> x.endsWith("@reqres.in")))
        );
        for (int i = 0; i < avatars.size(); i++) {
            assertTrue(avatars.get(i).contains(ids.get(i).toString()));
        }
    }

    @Test
    @DisplayName("Check single user")
    public void checkSingleUserTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Response response = given()
                .when()
                .get("api/users/2")
                .then().log().all()
                .body("data.id", notNullValue())
                .body("data.email", notNullValue())
                .body("data.first_name", notNullValue())
                .body("data.last_name", notNullValue())
                .body("data.avatar", notNullValue())
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        int id = jsonPath.getInt("data.id");
        String email = jsonPath.getString("data.email");
        String firstName = jsonPath.getString("data.first_name");
        String lastName = jsonPath.getString("data.last_name");
        assertAll(
                () -> assertEquals(2, id),
                () -> assertEquals("janet.weaver@reqres.in", email),
                () -> assertEquals("Janet", firstName),
                () -> assertEquals("Weaver", lastName)
        );
    }

    @Test
    @DisplayName("Check single user not found")
    public void checkSingleUserNotFoundTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecUnique(404));
        Response response = given()
                .when()
                .get("api/users/23")
                .then().log().all()
                .statusCode(404)
                .extract().response();
        assertEquals(404, response.getStatusCode());
    }

    @Test
    @DisplayName("Check list resources")
    public void checkListResourcesTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Response response = given()
                .when()
                .get("api/unknown")
                .then().log().all()
                .body("page", equalTo(1))
                .body("per_page", equalTo(6))
                .body("total", equalTo(12))
                .body("total_pages", equalTo(2))
                .body("data.id", notNullValue())
                .body("data.name", notNullValue())
                .body("data.year", notNullValue())
                .body("data.color", notNullValue())
                .body("data.pantone_value", notNullValue())
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        List<Objects> objects = jsonPath.get("data");
        assertAll(
                () -> assertNotNull(objects),
                () -> assertEquals(6, objects.size())
        );
    }

    @Test
    @DisplayName("Check single resource")
    public void checkSingleResourceTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Response response = given()
                .when()
                .get("api/unknown/2")
                .then().log().all()
                .body("data.id", notNullValue())
                .body("data.name", notNullValue())
                .body("data.year", notNullValue())
                .body("data.color", notNullValue())
                .body("data.pantone_value", notNullValue())
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        int id = jsonPath.getInt("data.id");
        String name = jsonPath.getString("data.name");
        int year = jsonPath.getInt("data.year");
        assertAll(
                () -> assertEquals(2, id),
                () -> assertEquals("fuchsia rose", name),
                () -> assertEquals(2001, year)
        );
    }

    @Test
    @DisplayName("Check single resource not found")
    public void checkSingleResourceNotFoundTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecUnique(404));
        Response response = given()
                .when()
                .get("api/unknown/23")
                .then().log().all()
                .statusCode(404)
                .extract().response();
        assertEquals(404, response.getStatusCode());
    }

    @Test
    @DisplayName("Successful registration")
    public void successfulUserRegTestNoPojo() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Map<String, String> user = new HashMap<>();
        user.put("email", "eve.holt@reqres.in");
        user.put("password", "pistol");
        Response response = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .body("id", equalTo(4))
                .body("token", equalTo("QpwL5tke4Pnpja7X4"))
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        int id = jsonPath.get("id");
        String token = jsonPath.get("token");
        assertAll(
                () -> Assert.assertEquals(4, id),
                () -> Assert.assertEquals("QpwL5tke4Pnpja7X4", token)
        );
    }

    @Test
    @DisplayName("Unsuccessful registration")
    public void unsuccessfulUserRegNoPojo() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecError400());
        Map<String, String> user = new HashMap<>();
        user.put("email", "sydney@fife");
        Response response = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        String error = jsonPath.get("error");
        Assert.assertEquals("Missing password", error);
    }
}
