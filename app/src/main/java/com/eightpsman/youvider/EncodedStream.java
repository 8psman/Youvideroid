package com.eightpsman.youvider;

/**
 * Youvider
 * Created by 8psman on 2/5/2015.
 * Email: 8psman@gmail.com
 */
public class EncodedStream {

    public Itag itag;
    public String url;

    public EncodedStream(int itag, String url){
        this.url = url;
        this.itag = Itag.getItag(itag);
    }
}
