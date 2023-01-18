package com.group.myapplication.Model1;

import com.group.myapplication.Model.PostId;

import java.util.Date;

public class Request1 extends PostId {

    public String image1,user1, total, da_address;
    private Date time1;

    public String getImage1() {
        return image1;
    }

    public String getUser1() {
        return user1;
    }

    public Date getTime1() {
        return time1;
    }

    public String getTotal() {
        return total;
    }

    public String getDa_address() {
        return da_address;
    }
}
