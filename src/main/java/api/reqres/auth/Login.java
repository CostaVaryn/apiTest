package api.reqres.auth;

public class Login {
    private String email;
    private String password;

    public Login(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String toString() {
        return password.equals("") ? "{\n    " + "\"email\": " + "\"" + email + "\"" + "\n" + "}" :
                "{\n    " + "\"email\": " + "\"" + email + "\"," + "\n" +
                        "    " + "\"password\": " + "\"" + password + "\"\n" + "}";
    }
}
