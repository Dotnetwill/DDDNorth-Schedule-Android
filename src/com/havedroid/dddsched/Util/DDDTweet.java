package com.havedroid.dddsched.Util;

import java.io.Serializable;
import java.util.Date;

public class DDDTweet implements Serializable {

    public String user;
    public String content;
    public String profileImageUrl;
    public Date createdAt;
    public long id;

    public DDDTweet(String user, String content, String profileImageUrl, Date createdAt, long id){
        this.user = user;
        this.content = content;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt;
        this.id = id;
    }
}
