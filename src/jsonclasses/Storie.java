package jsonclasses;

import com.google.gson.annotations.SerializedName;

public class Storie {

    public  String type;
    public  String url;
    @SerializedName("posted_to")
    public String postedTo;
}
