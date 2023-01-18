package com.group.myapplication.Model;

import java.util.Date;

public class Comments extends PostId {
    public String user, comment;
    private Date time;

    public String getUser() {
        return user;
    }

    public String getComment() {
        return comment;
    }

    public Date getTime() {
        return time;
    }
}
