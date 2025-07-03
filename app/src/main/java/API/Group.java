package API;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Group {
    @SerializedName("id")              public String id;
    @SerializedName("name")            public String name;
    @SerializedName("description")     public String description;
    @SerializedName("members")         public List<String> members;
    @SerializedName("membersID")       public List<String> membersID;
    @SerializedName("creator")         public  String creator;

    @SerializedName("tasks")       public List<String> tasks;
}
