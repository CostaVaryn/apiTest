package api.reqres.auth;

public class SuccessLog {
    private String token;

    public SuccessLog (String token) {
        this.token = token;
    }

    public SuccessLog() {
    }

    public String getToken() {
        return token;
    }
}
