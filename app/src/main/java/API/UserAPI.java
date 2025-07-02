package API;

import java.util.Date;

public class UserAPI {

    public static class RegisterRequest {
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
        private String username;
        private String email;
        public LoginResponse(String idToken, String uid, String username, String email){
            this.idToken = idToken;
            this.uid = uid;
            this.username = username;
            this.email = email;
        }

        public String getIdToken()     { return idToken; }
        public String getRefreshToken(){ return refreshToken; }
        public String getUid()         { return uid; }
        public String getExpiresIn()   { return expiresIn; }
        public String getUsername()    {return username;}
        public String getEmail()       {return email;}
    }

    public static class RegisterResponse {
        private String uid;
        private String email;
        private String username;
        private String idToken;

        public String getUid()      { return uid; }
        public String getEmail()    { return email; }
        public String getUsername() { return username; }
        public String getIdToken()  { return idToken; }
    }
    public static class UserProfile {
        private String username;
        private CreatedAt createdAt;
        private String email;

        public String getEmail()    { return email; }
        public String getUsername() { return username; }
        public Date getCreatedAtDate() {
            // convert Firestore Date into java Date format
            return new Date(createdAt.seconds * 1000L + createdAt.nanos   / 1_000_000L);
        }
        public static class CreatedAt {
            long seconds;
            int  nanos;
        }
    }

}
