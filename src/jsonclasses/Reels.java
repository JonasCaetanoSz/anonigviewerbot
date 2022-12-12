package jsonclasses;

import com.google.gson.annotations.SerializedName;

public class Reels {

    @SerializedName("video_url")
    public String url;
    @SerializedName("caption_text")
    public String subtitle;
}
