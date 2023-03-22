package tests;

import api.reqres.colors.ColorsData;
import api.reqres.info.TestData;
import api.reqres.registration.Register;
import api.reqres.registration.SuccessReg;
import api.reqres.registration.UnSuccessReg;
import api.reqres.users.UserData;
import api.reqres.users.UserTime;
import api.reqres.users.UserTimeResponse;
import api.spec.Specifications;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Test;

import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ReqresPojoTest {
    private static final TestData testData = new TestData();
    private final static String URL = testData.getUrl();

    @Test
    public void checkAvatarAndIdTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        List<UserData> users = given()
                .contentType(ContentType.JSON)
                .when()
                .get("api/users?page=2")
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserData.class);
        users.forEach(x -> assertTrue(x.getAvatar().contains(x.getId().toString())));
        List<String> avatars = users.stream().map(UserData::getAvatar).collect(Collectors.toList());
        List<String> ids = users.stream().map(x -> x.getId().toString()).collect(Collectors.toList());
        assertTrue(users.stream().allMatch(x -> x.getEmail().endsWith(testData.getDomainEmail())));
        for (int i = 0; i < avatars.size(); i++) {
            assertTrue(avatars.get(i).contains(ids.get(i)));
        }
    }

    @Test
    public void successRegTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Integer id = 4;
        String token = testData.getToken();
        Register user = new Register(testData.getValidEmail(), testData.getRegPass());
        SuccessReg successReg = given()
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
    public void unSuccessRegTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecError400());
        Register user = new Register(testData.getInvalidRegEmail(), "");
        UnSuccessReg unSuccessReg = given()
                .body(user.toString())
                .post("api/register")
                .then().log().all()
                .extract().as(UnSuccessReg.class);
        assertEquals(testData.getErrorMessage(), unSuccessReg.getError());
    }

    @Test
    public void sortedYearsTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        List<ColorsData> colors = given()
                .when()
                .get("api/unknown")
                .then().log().all()
                .extract().body().jsonPath().getList("data", ColorsData.class);
        List<Integer> years = colors.stream().map(ColorsData::getYear).collect(Collectors.toList());
        List<Integer> sortedYears = years.stream().sorted().collect(Collectors.toList());
        assertEquals(sortedYears, years);
    }

    @Test
    public void checkDeleteUserTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecUnique(204));
        Response response = given()
                .when()
                .delete("api/users/2")
                .then().log().all().extract().response();
        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void checkTimeUpdatedTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        UserTime user = new UserTime(testData.getNameTest(), testData.getJobUpdateTest());
        UserTimeResponse response = given()
                .body(user)
                .when()
                .put("api/users/2")
                .then().log().all()
                .extract().as(UserTimeResponse.class);
        String regex = "(.{6})$";
        String currentTime = Clock.systemUTC().instant().toString().replaceAll(regex, "");
        assertEquals(currentTime, response.getUpdatedAt().replaceAll(regex, ""));
    }
}
