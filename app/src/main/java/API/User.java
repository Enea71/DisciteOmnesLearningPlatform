package API;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private String uid;

    @SerializedName("displayName")
    private String displayName;

    @SerializedName("email")
    private String email;

    @SerializedName("photoUrl")
    private String photoUrl;

    // No-arg constructor for Gson
    public User() {}

    // Getters
    public String getUid()          { return uid; }
    public String getDisplayName()  { return displayName; }
    public String getEmail()        { return email; }
    public String getPhotoUrl()     { return photoUrl; }
}

