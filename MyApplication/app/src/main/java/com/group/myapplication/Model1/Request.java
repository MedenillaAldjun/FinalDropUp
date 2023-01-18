package com.group.myapplication.Model1;

import com.group.myapplication.Model.PostId;

import java.util.Date;

public class Request extends PostId {

    public String image1,user1,caption1, total, documentID;
    private Date time1;

    public String getImage1() {
        return image1;
    }

    public String getUser1() {
        return user1;
    }

    public String getCaption1() {
        return caption1;
    }

    public Date getTime1() {
        return time1;
    }

    public String getTotal() {
        return total;
    }

    public String getDocumentID() {
        return documentID;
    }
}
