package com.nil.treeclause;

import com.google.gson.annotations.SerializedName;

public class ServerResponse{

    @SerializedName("status")
    boolean status;
    @SerializedName("message")
    String message;
    @SerializedName("tree_id")
    String tree_id;


    String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public String getTree_id() {
        return tree_id;
    }
}