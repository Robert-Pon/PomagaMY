package com.name.social_helper_r_p.connections;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RequestData {
    private List<Data> data = new ArrayList<>();

    public List<Data> getData(){
        return data;
    }
    /**
     Deleting param by name from "Form list"
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Data> deleteDataByName(String name){
        data = data.stream().filter(e->e.getName()!=name).collect(Collectors.toList());
        return data;
    }

    /**
     Adding/Setting param in "Form list"
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Data> setData(Data param){
        System.out.println(param.getObject());
        System.out.println(param.getName());
        try {
            boolean x = data.stream().anyMatch(e -> e.getName() == param.getName());
            if (x == true) {
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).getName() == param.getName()) {
                        data.set(i, param);
                    }
                }
            } else {
                data.add(param);
            }
        }catch (Exception e){
            System.out.println(e);
        }
        return data;
    }

}
