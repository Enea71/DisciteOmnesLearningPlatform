package API;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @POST("register")
    Call<UserAPI.RegisterResponse> register(@Body UserAPI.RegisterRequest req);

    @POST("login")
    Call<UserAPI.LoginResponse> login(@Body UserAPI.LoginRequest req);

    @GET("users/{uid}")
    Call<UserAPI.UserProfile> getProfile(
            @Header("Authorization") String bearer,
            @Path("uid") String uid
    );

    @PUT("users/{uid}")
    Call<Void> updateProfile(
            @Header("Authorization") String bearer,
            @Path("uid") String uid,
            @Body UserAPI.UserProfile profile
    );
}
