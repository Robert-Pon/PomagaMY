package com.name.social_helper_r_p.connections;

public class Error {
    private int index = -1;
    private String message = null;
    public Error(int index, String message){
        this.index = index;
        this.message = message;
        System.out.println("\u001B[31mError, index: "+index+"\u001B[0m");
        System.out.println("\u001B[34mMessage: "+message+"\u001B[0m");
    }

    public int getIndex() {
        return index;
    }

    public String getMessage() {
        return message;
    }
}
