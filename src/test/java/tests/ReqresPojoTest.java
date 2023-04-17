package tests;

import api.reqres.auth.Login;
import api.reqres.auth.SuccessLog;
import api.reqres.auth.UnSuccessLog;
import api.reqres.colors.ColorsData;
import api.reqres.info.TestData;
import api.reqres.registration.Register;
import api.reqres.registration.SuccessReg;
import api.reqres.registration.UnSuccessReg;
import api.reqres.resource.ResourceData;
import api.reqres.users.UserData;
import api.reqres.users.UserTime;
import api.reqres.users.UserTimeResponse;
import api.spec.Specifications;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReqresPojoTest {
    private static final TestData testData = new TestData();
    private final static String URL = testData.getUrl();

    @ParameterizedTest(name = "{index} - Check {0} list users")
    @DisplayName("GET: LIST USERS")
    @ValueSource(ints = {1, 2})
    public void checkAvatarAndIdTest(Integer page) {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        List<UserData> users = given()
                .contentType(ContentType.JSON)
                .when()
                .get("api/users?page=" + page)
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserData.class);
        List<String> avatars = users.stream().map(UserData::getAvatar).collect(Collectors.toList());
        List<String> ids = users.stream().map(x -> x.getId().toString()).collect(Collectors.toList());
        assertAll(
                () -> users.forEach(x -> assertTrue(x.getAvatar().contains(x.getId().toString()))),
                () -> {
                    for (int i = 0; i < avatars.size(); i++) {
                        assertTrue(avatars.get(i).contains(ids.get(i)));
                    }
                },
                () -> assertTrue(users.stream().allMatch(x -> x.getEmail().endsWith(testData.getDomainEmail())))
        );
    }

    @ParameterizedTest(name = "{index} - Check single resource ID whose equals {0}")
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12})
    @DisplayName("GET: SINGLE <USER>")
    public void checkSingleUserTest(Integer userId) {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        UserData userData = given()
                .contentType(ContentType.JSON)
                .when()
                .get("api/users/" + userId)
                .then().log().all()
                .extract().body().jsonPath().getObject("data", UserData.class);
        assertAll(
                () -> assertNotNull(userData),
                () -> assertEquals(userId, userData.getId()),
                () -> assertTrue(userData.getEmail().endsWith(testData.getDomainEmail())),
                () -> assertTrue(userData.getAvatar().endsWith(userId + "-image.jpg"))
        );
    }

    @Test
    @DisplayName("POST: REGISTER - SUCCESSFUL")
    public void successRegTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Integer id = 4;
        String token = testData.getToken();
        Register user = new Register(testData.getValidEmail(), testData.getRegPass());
        SuccessReg successReg = given()
                .contentType(ContentType.JSON)
                .body(user.toString())
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(SuccessReg.class);
        assertAll(
                () -> assertNotNull(successReg.getId()),
                () -> assertNotNull(successReg.getToken()),
                () -> assertEquals(id, successReg.getId()),
                () -> assertEquals(token, successReg.getToken())
        );
    }

    @Test
    @DisplayName("POST: REGISTER - UNSUCCESSFUL")
    public void unSuccessRegTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecError400());
        Register user = new Register(testData.getInvalidRegEmail(), "");
        UnSuccessReg unSuccessReg = given()
                .contentType(ContentType.JSON)
                .body(user.toString())
                .post("api/register")
                .then().log().all()
                .extract().as(UnSuccessReg.class);
        assertEquals(testData.getErrorMessage(), unSuccessReg.getError());
    }

    @Test
    @DisplayName("POST: LOGIN - UNSUCCESSFUL")
    public void unSuccessLogTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecError400());
        Login user = new Login(testData.getInvalidLogEmail(), "");
        UnSuccessLog unSuccessLog = given()
                .contentType(ContentType.JSON)
                .body(user.toString())
                .when()
                .post("api/login")
                .then().log().all()
                .extract().as(UnSuccessLog.class);
        assertEquals(testData.getErrorMessage(), unSuccessLog.getError());
    }

    @Test
    @DisplayName("POST: LOGIN - SUCCESSFUL")
    public void successLogTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Login user = new Login(testData.getValidEmail(), testData.getLogPass());
        SuccessLog successLog = given()
                .contentType(ContentType.JSON)
                .body(user.toString())
                .when()
                .post("api/login")
                .then().log().all()
                .extract().as(SuccessLog.class);
        assertAll(
                () -> assertNotNull(successLog.getToken()),
                () -> assertEquals(testData.getToken(), successLog.getToken())
        );
    }

    @Test
    @DisplayName("POST: CREATE")
    public void checkCreateTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecUnique(201));
        UserTime user = new UserTime(testData.getNameTest(), testData.getJobTest());
        UserTimeResponse response = given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("api/users")
                .then().log().all()
                .extract().as(UserTimeResponse.class);
        String regex = "(.{6})$";
        String currentTime = Clock.systemUTC().instant().toString().replaceAll(regex, "");
        assertAll(
                () -> assertEquals(testData.getNameTest(), response.getName()),
                () -> assertEquals(testData.getJobTest(), response.getJob()),
                () -> assertNotNull(response.getId()),
                () -> assertEquals(currentTime, response.getCreatedAt().replaceAll(regex, ""))
        );
    }

    @Test
    @DisplayName("GET: LIST <RESOURCE>")
    public void sortedYearsTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        List<ColorsData> colors = given()
                .contentType(ContentType.JSON)
                .when()
                .get("api/unknown")
                .then().log().all()
                .extract().body().jsonPath().getList("data", ColorsData.class);
        List<Integer> years = colors.stream().map(ColorsData::getYear).collect(Collectors.toList());
        List<Integer> sortedYears = years.stream().sorted().collect(Collectors.toList());
        assertEquals(sortedYears, years);
    }

    @ParameterizedTest(name = "{index} - Check single resource ID whose equals {0}")
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12})
    @DisplayName("GET: SINGLE <RESOURCE>")
    public void checkSingleResourceTest(Integer resId) {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        ResourceData resData = given()
                .contentType(ContentType.JSON)
                .when()
                .get("api/unknown/" + resId)
                .then().log().all()
                .extract().body().jsonPath().getObject("data", ResourceData.class);
        assertAll(
                () -> assertNotNull(resData),
                () -> assertEquals(resId, resData.getId())
        );
    }

    @Test
    @DisplayName("GET: SINGLE <RESOURCE> NOT FOUND")
    public void checkSingleResourceNotFoundTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecUnique(404));
        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("api/unknown/23")
                .then().log().all()
                .extract().response();
        assertEquals(404, response.getStatusCode());
    }

    @Test
    @DisplayName("GET: SINGLE USER NOT FOUND")
    public void checkSingleUserNotFoundTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecUnique(404));
        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/users/23")
                .then().log().all()
                .extract().response();
        assertEquals(404, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE: DELETE")
    public void checkDeleteUserTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecUnique(204));
        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .delete("api/users/2")
                .then().log().all().extract().response();
        assertEquals(204, response.getStatusCode());
    }

    @Test
    @DisplayName("PUT: UPDATE")
    public void checkTimeUpdatedUsingPutMethodTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        UserTime user = new UserTime(testData.getNameTest(), testData.getJobUpdateTest());
        UserTimeResponse response = given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .put("api/users/2")
                .then().log().all()
                .extract().as(UserTimeResponse.class);
        String regex = "(.{6})$";
        String currentTime = Clock.systemUTC().instant().toString().replaceAll(regex, "");
        assertEquals(currentTime, response.getUpdatedAt().replaceAll(regex, ""));
    }

    @Test
    @DisplayName("PATCH: UPDATE")
    public void checkTimeUpdatedUsingPatchMethodTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        UserTime user = new UserTime(testData.getNameTest(), testData.getJobUpdateTest());
        UserTimeResponse response = given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .patch("api/users/2")
                .then().log().all()
                .extract().as(UserTimeResponse.class);
        String regex = "(.{6})$";
        String currentTime = Clock.systemUTC().instant().toString().replaceAll(regex, "");
        assertEquals(currentTime, response.getUpdatedAt().replaceAll(regex, ""));
    }

    @Test
    @DisplayName("GET: DELAYED RESPONSE")
    public void checkListUsersWithDelayTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        List<UserData> users = given()
                .contentType(ContentType.JSON)
                .when()
                .get("api/users?page=2")
                .then().log().all()
                .body("per_page", equalTo(6))
                .extract().body().jsonPath().getList("data", UserData.class);
        List<String> avatars = users.stream().map(UserData::getAvatar).collect(Collectors.toList());
        List<String> ids = users.stream().map(x -> x.getId().toString()).collect(Collectors.toList());
        assertAll(
                () -> users.forEach(x -> assertTrue(x.getAvatar().contains(x.getId().toString()))),
                () -> {
                    for (int i = 0; i < avatars.size(); i++) {
                        assertTrue(avatars.get(i).contains(ids.get(i)));
                    }
                },
                () -> assertTrue(users.stream().allMatch(x -> x.getEmail().endsWith(testData.getDomainEmail())))
        );
    }
}
