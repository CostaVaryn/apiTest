package api.reqres.registration;

public class Register {
    private String email;
    private String password;

    public Register(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String toString() {
        return password.equals("") ? "{\n    " + "\"email\": " + "\"" + email +"\"" + "\n" + "}" :
                "{\n    " + "\"email\": " + "\"" + email +"\"," + "\n" +
                "    " + "\"password\": " + "\"" + password + "\"\n" + "}";
    }
}
