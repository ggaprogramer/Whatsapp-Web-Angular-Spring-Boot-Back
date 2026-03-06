package whatsapp.web.authentication.dto;

public record LoginDTO (String email, String password, Boolean rememberPassword) {
    public LoginDTO(String email, String password, Boolean rememberPassword){
        this.email = email;
        this.password = password;
        this.rememberPassword = rememberPassword != null && rememberPassword;
    }
}
