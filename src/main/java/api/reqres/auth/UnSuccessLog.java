package api.reqres.auth;

public class UnSuccessLog {
    private String error;

    public UnSuccessLog(String error) {
        this.error = error;
    }

    public UnSuccessLog() {
    }

    public String getError() {
        return error;
    }
}
