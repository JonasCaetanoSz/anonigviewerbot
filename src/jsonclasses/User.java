package jsonclasses;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("is_private")
    public boolean isPrivate;
    public String name;
    @SerializedName("pk_id")
    public String pkId;
    @SerializedName("profile_pic_url")
    public String profilePicUrl;
    @SerializedName("profile_url")
    public String profileUrl;
    public String username;

}
