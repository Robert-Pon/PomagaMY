package com.name.social_helper_r_p.connections;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.List;
import java.util.stream.Collectors;

public class Request {
    private String url;
    private String path;
    private String type= "";
    private String method;
    private List<String[]> headers;
    public Request(){

    }

    public Request(String url, String path, String type, String method){
        this.method = method;
        this.path = path;
        this.url = url;
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<String[]> getHeaders() {
        return headers;
    }

    public void setAllHeaders(List<String[]> headers) {
        this.headers = headers;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void deleteHeaderByName(String name){
        headers = headers.stream().filter(e->e[0]!=name).collect(Collectors.toList());

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setHeader(String name, String value){
        boolean x = headers.stream().anyMatch(e->e[0]==name);
        if(x==true){
            for(int i = 0; i < headers.size(); i++){
                if(headers.get(i)[0]==name){
                    headers.set(i, new String[]{name, value});
                }
            }
        }else{
            headers.add(new String[]{name, value});
        }
    }
    public void deleteHeaderByIndex(int index){
        headers.remove(index);
    }



}
