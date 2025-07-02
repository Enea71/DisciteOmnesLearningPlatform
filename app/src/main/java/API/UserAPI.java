package API;

public class UserAPI {

    public class RegisterRequest {
        private String email;
        private String password;
        private String username;

        // Required no-arg constructor for Gson
        public RegisterRequest() {}

        public RegisterRequest(String email, String password, String username) {
            this.email    = email;
            this.password = password;
            this.username = username;
        }

        // Getters (and setters if you need them)
        public String getEmail()    { return email; }
        public String getPassword() { return password; }
        public String getUsername() { return username; }
    }
    public static class LoginRequest {
        private String email;
        private String password;

        public LoginRequest() {}
        public LoginRequest(String email, String password) {
            this.email    = email;
            this.password = password;
        }

        public String getEmail()    { return email; }
        public String getPassword() { return password; }
    }

    public static class LoginResponse {
        private String idToken;
        private String refreshToken;
        private String uid;
        private String expiresIn;

        public String getIdToken()     { return idToken; }
        public String getRefreshToken(){ return refreshToken; }
        public String getUid()         { return uid; }
        public String getExpiresIn()   { return expiresIn; }
    }

    public class RegisterResponse {
        private String uid;
        private String email;
        private String username;

        public String getUid()      { return uid; }
        public String getEmail()    { return email; }
        public String getUsername() { return username; }
    }
    public static class UserProfile {
        private String username;
        private long   createdAt;  // or String, depending on your server

        public String getUsername() { return username; }
        public long   getCreatedAt(){ return createdAt; }
    }

}
