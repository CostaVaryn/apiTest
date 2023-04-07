package tests;

import api.reqres.info.PersonInfo;
import api.reqres.info.ResourceInfo;
import api.reqres.info.TestData;
import api.spec.Specifications;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Clock;
import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReqresNoPojoTest {
    private static final TestData testData = new TestData();
    private final static String URL = testData.getUrl();
    List<PersonInfo> usersList = new ArrayList<>(testData.getUsersList());
    List<ResourceInfo> resList = new ArrayList<>(testData.getResList());

    @ParameterizedTest
    @DisplayName("GET: Check list users")
    @ValueSource(ints = {1, 2})
    public void checkListUsersNoPojoTest(Integer page) {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Response response = given()
                .when()
                .get("api/users?page=" + page)
                .then().log().all()
                .body("page", equalTo(page))
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
                () -> assertTrue(emails.stream().allMatch(x -> x.endsWith(testData.getDomainEmail()))),
                () -> {
                    for (int i = 0; i < avatars.size(); i++) {
                        assertTrue(avatars.get(i).contains(ids.get(i).toString()));
                    }
                }
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12})
    @DisplayName("GET: Check single user")
    public void checkSingleUserTest(Integer userId) {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Response response = given()
                .when()
                .get("api/users/" + userId)
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
                () -> assertEquals(userId, id),
                () -> assertEquals(usersList.get(userId - 1).getEmail(), email),
                () -> assertEquals(usersList.get(userId - 1).getFirstName(), firstName),
                () -> assertEquals(usersList.get(userId - 1).getLastName(), lastName)
        );
    }

    @Test
    @DisplayName("GET: Check single user not found")
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
    @DisplayName("GET: Check list resources")
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

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12})
    @DisplayName("GET: Check single resource")
    public void checkSingleResourceTest(Integer resId) {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Response response = given()
                .when()
                .get("api/unknown/" + resId)
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
                () -> assertEquals(resId, id),
                () -> assertEquals(resList.get(resId - 1).getResName(), name),
                () -> assertEquals(resList.get(resId - 1).getResYear(), year)
        );
    }

    @Test
    @DisplayName("GET: Check single resource not found")
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
    @DisplayName("GET: Delayed response")
    public void checkDelayedResponseTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Response response = given()
                .when()
                .get("/api/users?delay=3")
                .then()
                .log().all()
                .body("page", equalTo(1))
                .body("data.id", notNullValue())
                .body("data.email", notNullValue())
                .body("data.first_name", notNullValue())
                .body("data.last_name", notNullValue())
                .body("data.avatar", notNullValue())
                .statusCode(200)
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        List<String> emails = jsonPath.get("data.email");
        List<Integer> ids = jsonPath.get("data.id");
        List<String> avatars = jsonPath.get("data.avatar");
        assertAll(
                () -> assertEquals(200, response.getStatusCode()),
                () -> assertNotNull(emails),
                () -> assertNotNull(ids),
                () -> assertNotNull(avatars),
                () -> assertTrue(emails.stream().allMatch(x -> x.endsWith(testData.getDomainEmail())))
        );
    }

    @Test
    @DisplayName("POST: Successful registration")
    public void successfulUserRegNoPojoTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Map<String, String> user = new HashMap<>();
        user.put("email", testData.getValidEmail());
        user.put("password", testData.getRegPass());
        Response response = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .body("id", equalTo(4))
                .body("token", equalTo(testData.getToken()))
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        int id = jsonPath.get("id");
        String token = jsonPath.get("token");
        assertAll(
                () -> assertEquals(4, id),
                () -> assertEquals(testData.getToken(), token)
        );
    }

    @Test
    @DisplayName("POST: Successful authorization")
    public void successfulUserAuthNoPojoTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Map<String, String> user = new HashMap<>();
        user.put("email", testData.getValidEmail());
        user.put("password", testData.getLogPass());
        Response response = given()
                .body(user)
                .when()
                .post("api/login")
                .then().log().all()
                .body("token", equalTo(testData.getToken()))
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        String token = jsonPath.get("token");
        assertAll(
                () -> assertNotNull(token),
                () -> assertEquals(testData.getToken(), token)
        );
    }

    @Test
    @DisplayName("POST: Unsuccessful authorization")
    public void unsuccessfulUserAuthNoPojoTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecError400());
        Map<String, String> user = new HashMap<>();
        user.put("email", testData.getInvalidLogEmail());
        Response response = given()
                .body(user)
                .when()
                .post("api/login")
                .then().log().all()
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        String errorMessage = jsonPath.get("error");
        assertEquals(testData.getErrorMessage(), errorMessage);
    }

    @Test
    @DisplayName("POST: Unsuccessful registration")
    public void unsuccessfulUserRegNoPojoTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecError400());
        Map<String, String> user = new HashMap<>();
        user.put("email", testData.getInvalidRegEmail());
        Response response = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        String errorMessage = jsonPath.get("error");
        assertEquals(testData.getErrorMessage(), errorMessage);
    }

    @Test
    @DisplayName("POST: Create resource")
    public void checkCreateResourceNoPojoTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecUnique(201));
        Map<String, String> jsonBody = new HashMap<>();
        jsonBody.put("name", testData.getNameTest());
        jsonBody.put("job", testData.getJobTest());
        Response response = given()
                .body(jsonBody)
                .when()
                .post("api/users")
                .then().log().all()
                .body("name", equalTo(testData.getNameTest()))
                .body("job", equalTo(testData.getJobTest()))
                .body("id", notNullValue())
                .body("createdAt", notNullValue())
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        String name = jsonPath.getString("name");
        String job = jsonPath.getString("job");
        int id = Integer.parseInt(jsonPath.getString("id"));
        String regex = "(.{6})$";
        String createdAt = jsonPath.getString("createdAt").replaceAll(regex, "");
        String currentTime = Clock.systemUTC().instant().toString().replaceAll(regex, "");
        assertAll(
                () -> assertEquals(testData.getNameTest(), name),
                () -> assertEquals(testData.getJobTest(), job),
                () -> assertTrue(id > 0),
                () -> assertEquals(currentTime, createdAt)
        );
    }

    @Test
    @DisplayName("PUT: Update resource")
    public void checkPutUpdateResourceNoPojoTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Map<String, String> jsonBody = new HashMap<>();
        jsonBody.put("name", testData.getNameTest());
        jsonBody.put("job", testData.getJobUpdateTest());
        Response response = given()
                .body(jsonBody)
                .when()
                .put("api/users/1")
                .then().log().all()
                .body("name", equalTo(testData.getNameTest()))
                .body("job", equalTo(testData.getJobUpdateTest()))
                .body("updatedAt", notNullValue())
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        String name = jsonPath.getString("name");
        String job = jsonPath.getString("job");
        String regex = "(.{6})$";
        String updatedAt = jsonPath.getString("updatedAt").replaceAll(regex, "");
        String currentTime = Clock.systemUTC().instant().toString().replaceAll(regex, "");
        assertAll(
                () -> assertEquals(testData.getNameTest(), name),
                () -> assertEquals(testData.getJobUpdateTest(), job),
                () -> assertEquals(currentTime, updatedAt)
        );
    }

    @Test
    @DisplayName("PATCH: Update resource")
    public void checkPatchUpdateResourceNoPojoTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Map<String, String> jsonBody = new HashMap<>();
        jsonBody.put("name", testData.getNameTest());
        jsonBody.put("job", testData.getJobUpdateTest());
        Response response = given()
                .body(jsonBody)
                .when()
                .patch("api/users/2")
                .then().log().all()
                .body("name", equalTo(testData.getNameTest()))
                .body("job", equalTo(testData.getJobUpdateTest()))
                .body("updatedAt", notNullValue())
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        String name = jsonPath.getString("name");
        String job = jsonPath.getString("job");
        String regex = "(.{6})$";
        String updatedAt = jsonPath.getString("updatedAt").replaceAll(regex, "");
        String currentTime = Clock.systemUTC().instant().toString().replaceAll(regex, "");
        assertAll(
                () -> assertEquals(testData.getNameTest(), name),
                () -> assertEquals(testData.getJobUpdateTest(), job),
                () -> assertEquals(currentTime, updatedAt)
        );
    }

    @Test
    @DisplayName("DELETE: Delete user")
    public void checkDeleteUserNoPojoTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecUnique(204));
        Response response = given()
                .when()
                .delete("api/users/2")
                .then().log().all()
                .statusCode(204)
                .extract().response();
        assertEquals(204, response.getStatusCode());
    }
}
