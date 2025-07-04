package API;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @POST("users/register")
    Call<UserAPI.RegisterResponse> register(@Body UserAPI.RegisterRequest req);

    @POST("users/login")
    Call<UserAPI.LoginResponse> login(@Body UserAPI.LoginRequest req);

    @GET("users/{uid}")
    Call<UserAPI.UserProfile> getProfile(
            @Header("Authorization") String bearer,
            @Path("uid") String uid
    );

    @GET("users/getuid/{username}")
    Call<UserAPI.UidResponse> getUidByUsername(
            @Path("username") String username
    );
    @GET("users/{uid}/getusername")
    Call<UserAPI.UsernameResponse> getUsernameByUid(
            @Path("uid") String uid
    );
    @PUT("groups/{id}")
    Call<Group> updateGroup(
            @Header("Authorization") String auth,
            @Path("id") String groupId,
            @Body Group group
    );
    @GET("groups/{id}/tasks")
    Call<List<Task>> getTasksForGroup(
            @Header("Authorization") String bearer,
            @Path("id") String groupId
    );

    @POST("groups/{id}/tasks")
    Call<Task> createTask(
            @Header("Authorization") String bearer,
            @Path("id") String groupId,
            @Body Task task
    );
    @PUT("users/{uid}")
    Call<Void> updateProfile(
            @Header("Authorization") String bearer,
            @Path("uid") String uid,
            @Body UserAPI.UserProfile profile
    );
    @POST("users/{uid}/password")
    Call<Void> changePassword(
            @Header("Authorization") String bearer,
            @Path("uid") String uid,
            @Body UserAPI.ChangePasswordRequest body
    );
    @PUT("users/{uid}/username")
    Call<Void> changeUsername(
            @Header("Authorization") String bearer,
            @Path("uid") String uid,
            @Body UserAPI.ChangeUsernameRequest body
    );
    @DELETE("users/{uid}/removeUser")
    Call<Void> deleteUser(
            @Header("Authorization") String bearer,
            @Path("uid") String uid
    );
    @POST("groups/create")
    Call<GroupAPI.GroupResponse> createGroup(
            @Header("Authorization") String bearer,
            @Body   GroupAPI.CreateGroupRequest req
    );

    @GET("groups/member")
    Call<GroupAPI.GroupsResponse> getMemberGroups(
            @Header("Authorization") String bearer
    );
    @GET("groups/{gid}")
    Call<Group> getGroup(
            @Header("Authorization") String bearer,
            @Path("gid") String gid);

    @GET("groups/owner")
    Call<GroupAPI.GroupsResponse> getOwnerGroups(
            @Header("Authorization") String bearer
    );
    @DELETE("groups/{gid}")
    Call<Void>deleteGroup(
        @Header("Authorization") String bearer,
        @Path("gid") String gid
    );
}
