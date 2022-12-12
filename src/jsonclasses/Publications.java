package jsonclasses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Publications {

    @SerializedName("posts")
    public List<Publication> medias;

    public  String subtitle;

    @SerializedName("total_fotos")
    public int totalFotos;

    @SerializedName("total_videos")
    public  int totalVideos;
}
