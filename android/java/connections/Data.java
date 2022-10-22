package com.name.social_helper_r_p.connections;

public class Data {
    private Object object;
    private String name;
    private String type = "param";

    public Data(String name, String object){
        this.name = name;
        this.object = object;
    }
    public Data(String name, Object object, String type){
        this.name = name;
        this.object = object;
        this.type = type;
    }
    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
