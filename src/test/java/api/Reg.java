package api;

public class Reg {
    private String email;
    private String password;

    public Reg(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Reg() {
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
