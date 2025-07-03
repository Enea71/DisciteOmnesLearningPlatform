package API;


import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;


public class GroupAPI {


    public static class CreateGroupRequest {
        @SerializedName("name")        public final String name;
        @SerializedName("description") public final String description;
        @SerializedName("members")     public final List<String> members;
        public CreateGroupRequest(String n, String d, List<String> m) {
            name = n; description = d; members = m;
        }
    }

    public static class GroupResponse {

        @SerializedName("id")          public String id;
        @SerializedName("name")        public String name;
        @SerializedName("description") public String description;
        @SerializedName("members")     public List<String> members;
        @SerializedName("createdBy")   public String creator;
        public Date getCreatedAtDate() {
            return new Date(createdAt.seconds * 1000L + createdAt.nanos / 1_000_000L);
        }

        /** Mirror the Firestore-style timestamp object */
        public static class CreatedAt {
            @SerializedName("seconds")
            public long seconds;

            @SerializedName("nanos")
            public int nanos;
        }
        @SerializedName("createdAt")   public CreatedAt createdAt;
    }

    public static class GroupsResponse {
        @SerializedName("groups")
        public List<GroupResponse> groups;
    }

}
