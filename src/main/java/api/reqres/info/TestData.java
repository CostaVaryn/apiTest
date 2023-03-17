package api.reqres.info;

import java.util.ArrayList;
import java.util.List;

public class TestData {

    private final String url, token, validEmail, invalidLogEmail, regPass, logPass,
            invalidRegEmail, errorMessage, nameTest, jobTest, jobUpdateTest, domainEmail;

    public TestData() {
        url = "https://reqres.in/";
        token = "QpwL5tke4Pnpja7X4";
        validEmail = "eve.holt@reqres.in";
        invalidLogEmail = "peter@klaven";
        regPass = "pistol";
        logPass = "cityslicka";
        invalidRegEmail = "sydney@fife";
        errorMessage = "Missing password";
        nameTest = "morpheus";
        jobTest = "leader";
        jobUpdateTest = "zion resident";
        domainEmail = "@reqres.in";
    }

    public List getUsersList() {
        List<PersonData> usersList = new ArrayList<>();
        usersList.add(new PersonData("george.bluth@reqres.in", "George", "Bluth"));
        usersList.add(new PersonData("janet.weaver@reqres.in", "Janet", "Weaver"));
        usersList.add(new PersonData("emma.wong@reqres.in", "Emma", "Wong"));
        usersList.add(new PersonData("eve.holt@reqres.in", "Eve", "Holt"));
        usersList.add(new PersonData("charles.morris@reqres.in", "Charles", "Morris"));
        usersList.add(new PersonData("tracey.ramos@reqres.in", "Tracey", "Ramos"));
        usersList.add(new PersonData("michael.lawson@reqres.in", "Michael", "Lawson"));
        usersList.add(new PersonData("lindsay.ferguson@reqres.in", "Lindsay", "Ferguson"));
        usersList.add(new PersonData("tobias.funke@reqres.in", "Tobias", "Funke"));
        usersList.add(new PersonData("byron.fields@reqres.in", "Byron", "Fields"));
        usersList.add(new PersonData("george.edwards@reqres.in", "George", "Edwards"));
        usersList.add(new PersonData("rachel.howell@reqres.in", "Rachel", "Howell"));
        return usersList;
    }

    public List getResList() {
        List<ResourceData> resList = new ArrayList<>();
        resList.add(new ResourceData("cerulean", 2000));
        resList.add(new ResourceData("fuchsia rose", 2001));
        resList.add(new ResourceData("true red", 2002));
        resList.add(new ResourceData("aqua sky", 2003));
        resList.add(new ResourceData("tigerlily", 2004));
        resList.add(new ResourceData("blue turquoise", 2005));
        resList.add(new ResourceData("sand dollar", 2006));
        resList.add(new ResourceData("chili pepper", 2007));
        resList.add(new ResourceData("blue iris", 2008));
        resList.add(new ResourceData("mimosa", 2009));
        resList.add(new ResourceData("turquoise", 2010));
        resList.add(new ResourceData("honeysuckle", 2011));
        return resList;
    }

    public String getUrl() {
        return url;
    }

    public String getToken() {
        return token;
    }

    public String getValidEmail() {
        return validEmail;
    }

    public String getInvalidLogEmail() {
        return invalidLogEmail;
    }

    public String getRegPass() {
        return regPass;
    }

    public String getLogPass() {
        return logPass;
    }

    public String getInvalidRegEmail() {
        return invalidRegEmail;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getNameTest() {
        return nameTest;
    }

    public String getJobTest() {
        return jobTest;
    }

    public String getJobUpdateTest() {
        return jobUpdateTest;
    }

    public String getDomainEmail() {
        return domainEmail;
    }
}
